//
//  Created by tsug on 3/31/12.
//
// To change the template use AppCode | Preferences | File Templates.
//

#include <iostream>
#include <assert.h>
#include "FBPanel.h"
#include "MoveType.h"
#include "GarbageBlockType.h"

using namespace std;

FBPanel::FBPanel(unsigned int seed, int playerId, BlockType initialBlockTypes[PANEL_WIDTH][PANEL_HEIGHT / 4 + 1]) {
    this->random = new SimpleRNG(seed);
    this->state = PLAY;
    this->move = NULL;
    this->stateTick = 0;
    this->freezingTime = 0;
    this->scrollingOffset = 0;
    scrollingSpeed = levelScrollingSpeed = INITIAL_SCROLLING_SPEED;

    // cursor
    cursor.x = (X / 2) - 1;
    cursor.y = (Y_DISPLAY / 2) - 1;

    // Blocks initialization
    for (int x = 0; x < X; x++) {
        for (int y = 0; y < Y; y++) {
            this->blocks[x][y] = NULL;
        }
    }

    if (initialBlockTypes != NULL) {
        for (int j = 0; j < (Y_DISPLAY / 4) + 1; j++) {
            for (int i = 0; i < X; i++) {
                blocks[i][j] = newBlock(initialBlockTypes[i][j]);
            }
        }
    }
}

FBPanel::~FBPanel() {
    // RNG deletion
    delete random;

    // Blocks deletion
    for (int x = 0; x < X; x++) {
        for (int y = 0; y < Y; y++) {
            if (blocks[x][y] != NULL) {
                delete blocks[x][y];
            }
        }
    }
}


Block *FBPanel::newRandom(const BlockType excludedType, const int poppingIndex, const int skillChainLevel) {
    const int randomIndex = this->random->nextInt() % numberOfRegularBlocks;
    int index = randomIndex == lastIndex ? (randomIndex + 1) % numberOfRegularBlocks : randomIndex;
    if (index == excludedType) {
        index = (index + 1) % numberOfRegularBlocks;
    }
    lastIndex = index;
    return newBlock((BlockType) index, poppingIndex, skillChainLevel);
}

Block *FBPanel::newBlock(const BlockType blockType, const int index, const int skillChainLevel) const {
    return new Block(random->nextInt(), blockType, index, skillChainLevel);
}

PanelSituation *FBPanel::onTick(const long tick) {
    events.clear();

    if (stateTick > 0) {
        stateTick--;
    }

    if (stateTick == 0) {
        switch (state) {
            case QUAKING:
                state = PLAY;
                break;

            case GAMEOVER_PHASE1:
                state = GAMEOVER_PHASE2;
                stateTick = PANEL_QUAKINGTIME - 1;
                break;

            case GAMEOVER_PHASE2:
                state = GAMEOVER_PHASE3;
                stateTick = 2 * CORE_FREQUENCY;
                break;

            case GAMEOVER_PHASE3:
                // TODO
                break;

            default:
                break;
        }
    }

    switch (state) {
        case QUAKING:
        case PLAY: {
            processMove();

            mechanics();

            Combo *currentCombo = detectCombo();
            if (currentCombo->blocks.size() > 0) {
                processCombo(currentCombo);
            } else {
                delete currentCombo;
            }

            if (!switching) {
                scrolling(tick);
            }

//            // Drop garbage if required
//            if ((getSkillChainLevel() == 1)) {
//                dropGarbages();
//            }
        }
            break;

        case GAMEOVER_PHASE1:
            if ((stateTick % 4) == 0) {
                // Game over phase 1: making compressed blocks "explode"
                for (int i = 0; i < X; i++) {
                    // Column is compressed if a block has reach Y_DISPLAY height
                    if (blocks[i][Y_DISPLAY] == NULL) {
                        continue;
                    }

                    for (int j = Y_DISPLAY; j > 0; j--) {
                        Block *current = blocks[i][j];
                        if (!Block::isComputable(current)) {
                            continue;
                        }
                        current->explode(-1);
                        break;
                    }
                }
            }
            break;

        case GAMEOVER_PHASE2:
            break;

        case GAMEOVER_PHASE3:
            if ((stateTick % 2) == 0) {
                if (wallOffset != Y_DISPLAY) {
                    wallOffset++;
                }
            }
            break;
    }
    if (move != NULL) {
        delete move;
        move = NULL;
    }
    return getSituation();
}

void FBPanel::processMove() {
    if (move == NULL) {
        return;
    }
    const int type = move->type;
    switch (type) {
        case BLOCK_SWITCH: {
            Block *src = blocks[cursor.x][cursor.y];
            Block *dst = blocks[cursor.x + 1][cursor.y];
            const Block *aboveSrc = blocks[cursor.x][cursor.y + 1];
            const Block *aboveDst = blocks[cursor.x + 1][cursor.y + 1];
            if ((src == NULL) && (dst == NULL)) {
                break;
            }
            if (src != NULL) {
                if (!Block::isComputable(src)) {
                    break;
                }
            }
            if (dst != NULL) {
                if (!Block::isComputable(dst)) {
                    break;
                }
            }

            // Swap column fix: prevent switch if the above block is switching or suspending
            if ((dst == NULL) && (aboveDst != NULL)) {
                if ((aboveDst->state == SWITCHING_BACK) || (aboveDst->state == SWITCHING_FORTH) || (aboveDst->state == HOVERING)) {
                    break;
                }
            }
            if ((src == NULL) && (aboveSrc != NULL)) {
                if ((aboveSrc->state == SWITCHING_BACK) || (aboveSrc->state == SWITCHING_FORTH) || (aboveSrc->state == HOVERING)) {
                    break;
                }
            }

            if (src == NULL) {
                src = blocks[cursor.x][cursor.y] = newBlock(INVISIBLE);
            }
            if (dst == NULL) {
                dst = blocks[cursor.x + 1][cursor.y] = newBlock(INVISIBLE);
            }
            src->switchForth();
            dst->switchBack();
            events.insert(new PanelEvent(CURSOR_SWAP));
        }
            break;

        case CURSOR_DOWN:
            if (cursor.y != 1) {
                cursor.y--;
                events.insert(new PanelEvent(CURSOR_MOVE));
            }
            break;

        case CURSOR_LEFT:
            if (cursor.x != 0) {
                cursor.x--;
                events.insert(new PanelEvent(CURSOR_MOVE));
            }
            break;
        case CURSOR_RIGHT:
            if (cursor.x != (X - 2)) {
                cursor.x++;
                events.insert(new PanelEvent(CURSOR_MOVE));
            }
            break;
        case CURSOR_UP:
            if (cursor.y != (gracing ? Y_DISPLAY : Y_DISPLAY - 1)) {
                cursor.y++;
                events.insert(new PanelEvent(CURSOR_MOVE));
            }
            break;
        case LIFT:
            freeze(0);
            if (!lifting && !locked) {
                lifting = true;
                skillChainLevel = 1;
            }
            break;
    }
}

void FBPanel::dropGarbages() {
    for (vector<Garbage *>::iterator garbage = garbageStack.begin(); garbage != garbageStack.end(); garbage++) {
        const int y = (Y_DISPLAY + (*garbage)->height) - 1;
        for (int h = 0, j = y; h < (*garbage)->height; h++, j--) {
            for (int i = 0, w = 0; w < (*garbage)->width; i++, w++) {
                if (blocks[i][j] != NULL) {
                    return;
                }
            }
        }
        garbageStack.erase(garbage);
        const int xPos = (*garbage)->width < X ? random->nextInt() % (X - (*garbage)->width) : 0;
        (*garbage)->inject(xPos, y);
    }
}

void FBPanel::scrolling(const long tick) {
    // Update level scrolling speed
    if (((tick % NEXT_LEVEL) == 0) && (levelScrollingSpeed > 1) && (tick > 0)) {
        levelScrollingSpeed /= 2;
    }

    if (locked) {
        return;
    }

    if (freezingTime > 0) {
        freezingTime--;
        return;
    }

    // Starting from here, we need to scroll
    gracePeriod();
    if (gracing) {
        return;
    }

    scrollingSpeed = lifting ? 1 : levelScrollingSpeed;

    bool needNewLine = false;
    if (tick % scrollingSpeed == 0) {
        scrollingOffset += lifting ? 2 : 1;
        if (scrollingOffset >= BLOCK_LOGICALHEIGHT) {
            needNewLine = true;
        }
        scrollingOffset %= BLOCK_LOGICALHEIGHT;
    }

    // new line
    if (needNewLine) {
        newLine();
        gracePeriod();
        // scroll the cursor
        if (cursor.y != (gracing ? Y_DISPLAY : Y_DISPLAY - 1)) {
            cursor.y++;
        }
    }
}

void FBPanel::gracePeriod() {
    // Grace period
    bool topLineEmpty = true;
    for (int x = 0; x < X; x++) {
        Block **blockColumn = blocks[x];
        if (blockColumn[Y_DISPLAY] != NULL) {
            topLineEmpty = false;
            break;
        }
    }

    if (!topLineEmpty) {
        // 1/ we need to scroll,
        // 2/ the top line of the panel is not empty
        // 3/ we are already in the grace period
        // Game Over !
        if (gracing) {
            garbageStack.clear();
            gameOver = true;
            state = GAMEOVER_PHASE1;
            stateTick = PANEL_QUAKINGTIME - 1;
        } else {
            lifting = false;
            gracing = true;
            freeze(CORE_FREQUENCY * 2);
        }
        return;
    }
    gracing = false;
}

void FBPanel::freeze(const int freezingTime) {
    this->freezingTime = freezingTime;
}

void FBPanel::newLine() {
    if (lifting) {
        freeze(CORE_FREQUENCY);
        scrollingOffset = 0;
    }
    lifting = false;

    // scroll the panel lines up
    for (int y = Y - 1; y > 0; y--) {
        for (int x = 0; x < X; x++) {
            blocks[x][y] = blocks[x][y - 1];
        }
    }

    // create the new line
    for (int x = 0; x < X; x++) {
        blocks[x][0] = blocks[x][1] == NULL ? newRandom() : newRandom(blocks[x][1]->type);
    }
}

void FBPanel::addPanelListener(PanelListener *panelListener) {
    if (panelListeners.find(panelListener) == panelListeners.end()) {
        panelListeners.insert(panelListener);
    }
}

void FBPanel::stackGarbage(Garbage *garbage) {
    if (garbage->skill) {
        for (int i = garbageStack.size() - 1; i >= 0; i--) {
            const Garbage *stackedGarbage = (const Garbage *) garbageStack.at(i);
            if (stackedGarbage->skill && stackedGarbage->height == garbage->height - 1 && stackedGarbage->owner == garbage->owner) {
                garbageStack[i] = garbage;
                return;
            }
        }
    }
    garbageStack.push_back(garbage);
}

void FBPanel::quake() {
    state = QUAKING;
    stateTick = PANEL_QUAKINGTIME - 1;
}

void FBPanel::mechanics() {
    switching = false;
    locked = false;
    int revealingTime = BLOCK_REVEALINGTIMEBASE;
    for (int y = 1; y < Y; y++) {
        for (int x = 0; x < X; x++) {
            // Check each block of the panel
            Block *current = blocks[x][y];

            // null, go on
            if (current == NULL) {
                continue;
            }

            PanelEvent *event = current->update();
            if (event != NULL) {
                event->data1 = current->poppingSkillChainLevel;
                event->data2 = current->poppingIndex;
                events.insert(event);
            }

            const BlockType type = current->type;
            const BlockState state = current->state;
            switch (state) {
                case DONE_SWITCHING_FORTH:
                    blocks[x][y] = blocks[x + 1][y];
                    blocks[x + 1][y] = current;

                    blocks[x][y]->idle();
                    blocks[x + 1][y]->idle();

                    if (blocks[x][y]->type == INVISIBLE) {
                        blocks[x][y]->toDelete();
                    } else if (blocks[x][y - 1] == NULL) {
                        blocks[x][y]->hover(BLOCK_HOVERINGTIME);
                    }
                    if (blocks[x + 1][y]->type == INVISIBLE) {
                        blocks[x + 1][y]->toDelete();
                    } else if (blocks[x + 1][y - 1] == NULL) {
                        blocks[x + 1][y]->hover(BLOCK_HOVERINGTIME);
                    }

                    // process the block again, we don't want it to be idle for a tick if it has to fall.
                    x--;
                    break;

                case TO_DELETE:
                    // Skill chain management:
                    // A block has to be deleted (a clearing has been completed), set the above blocks (that are gonna fall) eligible for
                    // a skill chain

                    // Exclude invisible blocks
                    if (blocks[x][y]->type != INVISIBLE) {
                        for (int k = y + 1; k < Y; k++) {

                            // If the block is not computable just exit the loop
                            if (!Block::isComputable(blocks[x][k])) {
                                break;
                            }

                            // else, flag it as falling from clearing
                            blocks[x][k]->fallingFromClearing = true;
                        }
                    }

                    // delete the block
                    blocks[x][y] = NULL;
                    delete current;
                    break;

                case IDLE:
                case FALLING:
                case DONE_HOVERING:
                case DONE_AIRBOUNCING:
                    switch (type) {
                        case BLUE:
                        case GREEN:
                        case PURPLE:
                        case RED:
                        case YELLOW:
                            // Does the block have to fall ?
                            if (blocks[x][y - 1] == NULL) {
                                // yes, move the block down
                                current->fall();
                                blocks[x][y - 1] = current;
                                blocks[x][y] = NULL;
                            } else {
                                // no, land
                                current->idle();
                                events.insert(new PanelEvent(BLOCK_LAND));
                            }
                            break;
                        case GARBAGE: {
                            Garbage *garbage = getGarbageByBlock(current);
                            assert(garbage != NULL);
                            // Does the garbage have to fall ?
                            if (garbage->hasToFall(x, y)) {
                                garbage->fall(x, y);
                            } else {
                                garbage->idle();
                                quake();
                            }
                            // Necessary trick
                            x += garbage->width - 1;
                        }
                            break;
                        case INVISIBLE:
                            break;
                    }
                    break;

                case DONE_BLINKING:
                    switch (type) {
                        case BLUE:
                        case GREEN:
                        case PURPLE:
                        case RED:
                        case YELLOW: {
                            const Combo *combo = getComboByBlock(current);
                            assert(combo != NULL);

                            for (set<Block *>::const_iterator i = combo->blocks.begin(); i != combo->blocks.end(); i++) {
                                Block *block = (Block *) (*i);
                                if (block->state == EXPLODING) {
                                    break;
                                }
                                block->explode(revealingTime += BLOCK_REVEALINGTIMEINCREMENT);
                            }
                        }
                            break;
                        case GARBAGE: {
                            Clearing *clearing = getClearingByBlock(current);
                            assert(clearing != NULL);
                            Garbage *garbage = getGarbageByBlock(current);
                            assert(garbage != NULL);
                            revealingTime = garbage->reveal(x, y, revealingTime, clearing);
                            clearing->removeBar(garbage);
                            x += garbage->width - 1;
                        }
                            break;
                        case INVISIBLE:
                            break;
                    }
                    break;

                case DONE_EXPLODING:
                    switch (type) {
                        case BLUE:
                        case GREEN:
                        case PURPLE:
                        case RED:
                        case YELLOW: {
                            Combo *combo = getComboByBlock(current);
                            assert(combo != NULL);
                            bool doneExploding = true;

                            for (set<Block *>::const_iterator i = combo->blocks.begin(); i != combo->blocks.end(); i++) {
                                if ((*i)->state != DONE_EXPLODING) {
                                    doneExploding = false;
                                    break;
                                }
                            }

                            if (doneExploding) {
                                for (set<Block *>::const_iterator i = combo->blocks.begin(); i != combo->blocks.end(); i++) {
                                    (*i)->toDelete();
                                }
                                combo->blocks.clear();
                                combos.erase(combo);
                                delete combo;

                                // process the block again
                                x--;
                            }
                        }
                            break;
                        case GARBAGE:
                        case INVISIBLE:
                            break;
                    }
                    break;

                case DONE_REVEALING: {
                    Clearing *clearing = getClearingByBlock(current);
                    assert(clearing != NULL);
                    if (clearing->isDoneRevealing()) {
                        clearing->doneRevealing();
                        clearings.erase(clearing);
                    }
                }
                    break;

                case EXPLODING:
                case BLINKING:
                case REVEALING:
                case AIRBOUNCING:
                    locked = true;
                    break;

                case HOVERING:
                    break;

                case SWITCHING_BACK:
                case SWITCHING_FORTH:
                    switching = true;
                    break;
            }
        }
    }
}

Combo *FBPanel::getComboByBlock(Block *block) const {
    for (set<Combo *>::const_iterator i = combos.begin(); i != combos.end(); i++) {
        Combo *combo = (Combo *) (*i);
        set<Block *>::const_iterator comboBlock = combo->blocks.find(block);
        if (comboBlock != combo->blocks.end()) {
            return combo;
        }
    }
    return NULL;
}

FBPanel::Garbage *FBPanel::getGarbageByBlock(Block *block) const {
    for (set<Garbage *>::const_iterator garbage = garbages.begin(); garbage != garbages.end(); garbage++) {
        if ((*garbage)->contains(block)) {
            return (*garbage);
        }
    }
    return NULL;
}

FBPanel::Clearing *FBPanel::getClearingByBlock(Block *block) const {
    for (set<Clearing *>::const_iterator i = clearings.begin(); i != clearings.end(); i++) {
        Clearing *clearing = (Clearing *) (*i);
        if (clearing->contains(block)) {
            return clearing;
        }
    }
    return NULL;
}

Combo *FBPanel::detectCombo() {
    Combo *currentCombo = new Combo(playerId);
    // ComboMask initialization
    for (int x = 0; x < X; x++) {
        for (int y = 0; y < Y; y++) {
            this->comboMask[x][y] = false;
        }
    }

    // First build the combo mask because we need to insert the blocks in a specific order to respect original game clearing order
    for (int y = 1; y < Y; y++) {
        for (int x = 0; x < X; x++) {
            // Check each block of the panel
            const Block *current = blocks[x][y];

            // null, go on
            if (current == NULL || current->state != IDLE || !current->movable || !current->combinable) {
                continue;
            }

            // Check right
            int xIdem = 1;
            for (int right = x + 1; right < X; right++) {
                const Block *rightBlock = blocks[right][y];
                if (rightBlock == NULL || rightBlock->state != IDLE || rightBlock->type != current->type) {
                    break;
                }
                xIdem++;
            }

            // Check above
            int yIdem = 1;
            for (int above = y + 1; above < Y; above++) {
                const Block *aboveBlock = blocks[x][above];
                if (aboveBlock == NULL || aboveBlock->state != IDLE || aboveBlock->type != current->type) {
                    break;
                }
                yIdem++;
            }

            if (xIdem >= 3) {
                for (int k = x; k < (x + xIdem); k++) {
                    comboMask[k][y] = true;
                }
            }

            if (yIdem >= 3) {
                for (int k = y; k < (y + yIdem); k++) {
                    comboMask[x][k] = true;
                }
            }
        }
    }

    // Then insert the block following the original game clearing order: from top to bottom, and from left to right
    int poppingIndex = 0;
    for (int y = Y - 1; y > 0; y--) {
        for (int x = 0; x < X; x++) {
            if (!comboMask[x][y]) {
                continue;
            }
            currentCombo->blocks.insert(blocks[x][y]);
            blocks[x][y]->combo = true;
            blocks[x][y]->poppingIndex = poppingIndex++;
        }
    }
    return currentCombo;
}

void FBPanel::processCombo(Combo *combo) {
    const int comboSize = combo->blocks.size();

    // A new combo has been performed, process it
    // Add the combo to the panel
    combos.insert(combo);

    // Lock the panel as a combo clearing is starting
    locked = true;

    // A combo is a skill combo when the falling block is falling from a previous clear
    bool isSkillCombo = false;

    for (set<Block *>::const_iterator block = combo->blocks.begin(); block != combo->blocks.end(); block++) {
        isSkillCombo = (*block)->fallingFromClearing;
        if (isSkillCombo) {
            break;
        }
    }

    // Compute panel skill chain and assign it to the combo
    int comboSkillChainLevel = 1;
    if (isSkillCombo) {
        skillChainLevel++;
        comboSkillChainLevel = skillChainLevel;
        events.insert(new PanelEvent(SKILL_COMBO));
        score += (1000 * skillChainLevel) + (combo->blocks.size() * 100);
    } else {
        skillChainLevel = 1;
        score += combo->blocks.size() * 100;
        //      events.add(new PanelEvent(PanelEventType.SKILLCHAIN_END, skillChainLevel));
    }

    for (set<Block *>::const_iterator block = combo->blocks.begin(); block != combo->blocks.end(); block++) {
        (*block)->poppingSkillChainLevel = skillChainLevel;

    }
    combo->skillChainLevel = comboSkillChainLevel;

    for (set<Block *>::const_iterator block = combo->blocks.begin(); block != combo->blocks.end(); block++) {
        (*block)->blink();
    }

    // notify listeners
//    for (final PanelListener panelListener : panelListeners) {
//        panelListener.onCombo(combo);
//    }

    // Bonus freeze : the panel has an additional freezing time if the combo size is at least 4 or if the combo is a skill chain
    // bonus freezing time is expressed in seconds
    if ((comboSize >= 4) || (combo->skillChainLevel > 1)) {
        if (comboSize >= 4) {
            bonusFreezingTime = (comboSize / 2) * CORE_FREQUENCY;
        }
        if (combo->skillChainLevel > 1) {
            if (combo->skillChainLevel == 2) {
                bonusFreezingTime = 5 * CORE_FREQUENCY;
            } else {
                bonusFreezingTime += (combo->skillChainLevel / 2) + CORE_FREQUENCY;
            }
        }
        freeze(bonusFreezingTime);
    }

    // Check if a combo triggered a garbage blinking, and make it blink accordingly
    // All garbages cleared via the same combo form a "clearing"
    int poppingIndex = comboSize;
    Clearing *clearing = new Clearing();
    for (int y = 1; y < Y; y++) {
        for (int x = 0; x < X; x++) {
            // Check each block of the panel
            Block *current = blocks[x][y];

            // null, go on
            if (current == NULL || current->state != BLINKING || current->stateTick != BLOCK_BLINKINGTIME) {
                continue;
            }

            // Check for garbage to blink above
            if (y + 1 < Y) {
                Block *aboveBlock = blocks[x][y + 1];
                Garbage *garbage = getGarbageByBlock(aboveBlock);
                if (aboveBlock != NULL && garbage != NULL) {
                    poppingIndex = garbage->blink(poppingIndex, combo);
                    clearing->addBlockBar(garbage);
                }
            }

            // Check for garbage to blink below
            if (y - 1 > 0) {
                Block *belowBlock = blocks[x][y - 1];
                Garbage *garbage = getGarbageByBlock(belowBlock);
                if (belowBlock != NULL && garbage != NULL) {
                    poppingIndex = garbage->blink(poppingIndex, combo);
                    clearing->addBlockBar(garbage);
                }
            }

            // Check for garbage to blink on the right
            if (x + 1 < X) {
                Block *rightBlock = blocks[x + 1][y];
                Garbage *garbage = getGarbageByBlock(rightBlock);
                if (rightBlock != NULL && garbage != NULL) {
                    poppingIndex = garbage->blink(poppingIndex, combo);
                    clearing->addBlockBar(garbage);
                }
            }

            // Check for garbage to blink on the left
            if (x - 1 > 0) {
                Block *leftBlock = blocks[x - 1][y];
                Garbage *garbage = getGarbageByBlock(leftBlock);
                if (leftBlock != NULL && garbage != NULL) {
                    poppingIndex = garbage->blink(poppingIndex, combo);
                    clearing->addBlockBar(garbage);
                }
            }
        }
    }

    if (!clearing->isEmpty()) {
        clearings.insert(clearing);
    } else {
        delete clearing;
    }
}

PanelSituation *FBPanel::getSituation() {
    // Blocks allocation, just give the player/renderer the visible blocks
    BlockSituation ***blockSituations = new BlockSituation **[PANEL_WIDTH];
    for (int x = 0; x < PANEL_WIDTH; x++) {
        blockSituations[x] = new BlockSituation *[PANEL_HEIGHT + 1];
        for (int y = 0; y < PANEL_HEIGHT + 1; y++) {
            blockSituations[x][y] = blocks[x][y] == NULL ? NULL : blocks[x][y]->getSituation();
        }
    }
    set<ComboSituation *> comboSituations;
    for (set<Combo *>::iterator combo = combos.begin(); combo != combos.end(); combo++) {
        comboSituations.insert((*combo)->getSituation());
    }
    set<GarbageSituation *> garbageStackSituation;
    for (vector<Garbage *>::iterator garbage = garbageStack.begin(); garbage != garbageStack.end(); garbage++) {
        garbageStackSituation.insert((*garbage)->getSituation());
    }
    return new PanelSituation(blockSituations, locked, comboSituations, cursor, scrollingOffset, state, stateTick, garbageStackSituation, skillChainLevel, freezingTime, gameOver, wallOffset, events, gracing, score, rank, !clearings.empty());
}

int FBPanel::hashCode() {
    if (blocks == NULL) {
        return 0;
    }

    int hash = 1;
    for (int x = 0; x < X; x++) {
        for (int y = 0; y < Y; y++) {
            hash = 31 * hash + (this->blocks[x][y] == NULL ? 0 : this->blocks[x][y]->id);
        }
    }
    return hash;
}

void FBPanel::submitMove(Move *move) {
    this->move = new Move(move);
}

FBPanel::Garbage *FBPanel::newGarbage(int width, int height, int owner, bool skill) {
    return new Garbage(this, width, height, owner, skill);
}

FBPanel::BlockBar::BlockBar(FBPanel *parent, const int width, const int height, const int owner) {
    this->parent = parent;
    this->width = width;
    this->height = height;
    this->owner = owner;
    this->doneRevealing = false;
    this->id = parent->random->nextInt();
    this->blocks = (Block ***) parent->blocks;
}

bool FBPanel::BlockBar::contains(Block *block) const {
    return barBlocks.find(block) != barBlocks.end();
}

bool FBPanel::BlockBar::hasToFall(const int xOrigin, const int yOrigin) {
    for (int y = yOrigin; y < yOrigin + height && y < parent->Y; y++) {
        for (int x = xOrigin; x < xOrigin + width && x < parent->X; x++) {
            if (!contains(blocks[x][y]) || blocks[x][y]->state == FALLING) {
                return false;
            }
        }
    }

    // Check if the whole garbage can fall
    for (int x = xOrigin; x < xOrigin + width && x < parent->X; x++) {
        if (blocks[x][yOrigin - 1] != NULL) {
            return false;
        }
    }
    return true;
}

void FBPanel::BlockBar::fall(const int xOrigin, const int yOrigin) {
    for (int y = yOrigin; y < yOrigin + height && y < parent->Y; y++) {
        for (int x = xOrigin; x < xOrigin + width && x < parent->X; x++) {
            blocks[x][y]->fall();
            blocks[x][y - 1] = blocks[x][y];
            blocks[x][y] = NULL;
        }
    }
}

void FBPanel::BlockBar::idle() {
    for (set<Block *>::const_iterator block = barBlocks.begin(); block != barBlocks.end(); block++) {
        (*block)->idle();
    }
}

int FBPanel::BlockBar::blink(const int poppingIndex) {
    set<Block *>::const_iterator first = barBlocks.begin();
    if ((*first)->state == BLINKING) {
        return poppingIndex;
    }
    int index = poppingIndex;
    for (set<Block *>::const_iterator block = barBlocks.begin(); block != barBlocks.end(); block++) {
        (*block)->blink();
        (*block)->poppingIndex = index++;
        (*block)->poppingSkillChainLevel = parent->skillChainLevel;
    }
    return index;
}

bool FBPanel::BlockBar::isRevealing() {
    for (set<Block *>::const_iterator block = barBlocks.begin(); block != barBlocks.end(); block++) {
        if ((*block)->state == REVEALING) {
            return true;
        }
    }
    return false;
}

bool FBPanel::BlockBar::isDoneRevealing() {
    if (doneRevealing) {
        return true;
    }
    for (set<Block *>::const_iterator block = barBlocks.begin(); block != barBlocks.end(); block++) {
        if ((*block)->state != DONE_REVEALING) {
            return false;
        }
    }
    doneRevealing = true;
    return true;
}

FBPanel::Garbage::Garbage(FBPanel *parent, int width, int height, int owner, bool skill) : BlockBar(parent, width, height, owner) {
    this->skill = skill;
}

void FBPanel::Garbage::inject(const int x, const int y) {
    for (int j = y, h = 0; h < height; j--, h++) {
        for (int i = x, w = 0; w < width; i++, w++) {
            if (blocks[i][j] == NULL) {
                blocks[i][j] = parent->newBlock(GARBAGE);
            } else {
                blocks[i][j] = parent->newBlock(GARBAGE, blocks[i][j]->poppingIndex, blocks[i][j]->poppingSkillChainLevel);
            }
            blocks[i][j]->garbageOwner = owner;
            barBlocks.insert(blocks[i][j]);
        }
    }

//    garbages.add(this);
    switch (height) {
        case 1:
            for (int w = 1; w < (width - 1); w++) {
                blocks[x + w][y]->garbageBlockType = UPDOWN;
            }
            blocks[x][y]->garbageBlockType = UPLEFTDOWN;
            blocks[(x + width) - 1][y]->garbageBlockType = UPRIGHTDOWN;
            break;
        default:
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    blocks[x + w][y - h]->garbageBlockType = PLAIN;
                }
            }

            blocks[x][y]->garbageBlockType = UPLEFT;
            blocks[x][y - (height - 1)]->garbageBlockType = DOWNLEFT;
            blocks[(x + width) - 1][y]->garbageBlockType = UPRIGHT;
            blocks[(x + width) - 1][y - (height - 1)]->garbageBlockType = DOWNRIGHT;

            if (width > 2) {
                for (int w = 1; w < (width - 1); w++) {
                    blocks[x + w][y]->garbageBlockType = UP;
                    blocks[x + w][y - (height - 1)]->garbageBlockType = DOWN;
                }
            }

            if (height > 2) {
                for (int h = 1; h < (height - 1); h++) {
                    blocks[x][y - h]->garbageBlockType = LEFT;
                    blocks[(x + width) - 1][y - h]->garbageBlockType = RIGHT;
                }
            }
            break;
    }
}

int FBPanel::Garbage::blink(const int poppingIndex, const Combo *combo) {
    triggeringCombo = combo;
    return BlockBar::blink(poppingIndex);
}

int FBPanel::Garbage::reveal(const int xOrigin, const int yOrigin, int revealingTime, Clearing *parentClearing) {
    if (isRevealing()) {
        return revealingTime;
    }
    parent->garbages.erase(this);

    int revealingTimeIncrement = revealingTime;
    // Make the triggering combo explode first
    if (triggeringCombo != NULL) {
        for (set<Block *>::const_iterator block = triggeringCombo->blocks.begin(); block != triggeringCombo->blocks.end(); block++) {
            if ((*block)->state == EXPLODING) {
                break;
            }
            (*block)->explode(revealingTimeIncrement += BLOCK_REVEALINGTIMEINCREMENT);
        }
    }

    // Inject the new line
    BlockLine *subLine = new BlockLine(parent, width, owner);
    subLine->inject(xOrigin, yOrigin);
    parentClearing->addBlockBar(subLine);
    revealingTimeIncrement = subLine->reveal(xOrigin, yOrigin, revealingTimeIncrement);

    // Inject sub garbage
    if (height > 1) {
        Garbage *subGarbage = parent->newGarbage(width, height - 1, owner, skill);
        subGarbage->inject(xOrigin, yOrigin + height - 1);
        parentClearing->addBlockBar(subGarbage);

        for (int y = yOrigin + 1; y < yOrigin + height && y < parent->Y; y++) {
            for (int x = xOrigin; x < xOrigin + width && x < parent->X; x++) {
                if (y <= parent->Y_DISPLAY) {
                    blocks[x][y]->reveal(revealingTimeIncrement += BLOCK_REVEALINGTIMEINCREMENT);
                } else {
                    blocks[x][y]->reveal(0);
                }
            }
        }
    }
    return revealingTimeIncrement;
}

GarbageSituation *FBPanel::Garbage::getSituation() {
    return new GarbageSituation(width, height, owner);
}

void FBPanel::Garbage::onDoneRevealing() {
    for (set<Block *>::const_iterator block = barBlocks.begin(); block != barBlocks.end(); block++) {
        (*block)->idle();
    }
}

FBPanel::BlockLine::BlockLine(FBPanel *parent, int width, int owner) : BlockBar(parent, width, 1, owner) {
}

void FBPanel::BlockLine::inject(const int x, const int y) {
    for (int j = y, h = 0; h < height; j--, h++) {
        for (int i = x, w = 0; w < width; i++, w++) {
            if (blocks[i][j] == NULL) {
                blocks[i][j] = parent->newRandom((const BlockType) -1);
            } else {
                blocks[i][j] = parent->newRandom((const BlockType) -1, blocks[i][j]->poppingIndex, blocks[i][j]->poppingSkillChainLevel);
            }
            blocks[i][j]->garbageOwner = owner;
            barBlocks.insert(blocks[i][j]);
        }
    }
}

void FBPanel::BlockLine::onDoneRevealing() {
    for (set<Block *>::const_iterator block = barBlocks.begin(); block != barBlocks.end(); block++) {
        (*block)->airBounce();
        (*block)->fallingFromClearing = true;
    }
}

int FBPanel::BlockLine::reveal(const int xOrigin, const int yOrigin, const int revealingTime) {
    if (isRevealing()) {
        return revealingTime;
    }

    int revealingTimeIncrement = revealingTime;
    for (int y = yOrigin; y < yOrigin + height && y < parent->Y; y++) {
        for (int x = xOrigin; x < xOrigin + width && x < parent->X; x++) {
            if (y <= parent->Y_DISPLAY) {
                blocks[x][y]->reveal(revealingTimeIncrement += BLOCK_REVEALINGTIMEINCREMENT);
            } else {
                blocks[x][y]->reveal(0);
            }
        }
    }
    return revealingTimeIncrement;
}

void FBPanel::Clearing::addBlockBar(BlockBar *bar) {
    bars.insert(bar);
}

bool FBPanel::Clearing::isDoneRevealing() {
    for (set<BlockBar *>::const_iterator bar = bars.begin(); bar != bars.end(); bar++) {
        if (!(*bar)->isDoneRevealing()) {
            return false;
        }
    }
    return true;
}

void FBPanel::Clearing::doneRevealing() {
    for (set<BlockBar *>::const_iterator bar = bars.begin(); bar != bars.end(); bar++) {
        (*bar)->onDoneRevealing();
    }
}

bool FBPanel::Clearing::contains(Block *block) {
    for (set<BlockBar *>::const_iterator bar = bars.begin(); bar != bars.end(); bar++) {
        if ((*bar)->contains(block)) {
            return true;
        }
    }
    return false;
}

bool FBPanel::Clearing::isEmpty() {
    return bars.empty();
}

void FBPanel::Clearing::removeBar(BlockBar *bar) {
    bars.erase(bar);
}
