//
//  Created by tsug on 3/31/12.
//
// To change the template use AppCode | Preferences | File Templates.
//

#include "Block.h"
#include "FuriousBlocksCoreDefaults.h"

Block::Block(const int id, const BlockType type, const int index, const int skillChainLevel) {
    this->id = id;
    this->type = type;
    movable = true;
    combinable = true;

    switch (type) {
        case GARBAGE:
            movable = false;
            combinable = false;
            break;

        case INVISIBLE:
            combinable = false;
            break;

        default:
            break;
    }

    idle();
    fallingFromClearing = false;
    poppingIndex = index;
    poppingSkillChainLevel = skillChainLevel;
}

void Block::idle() {
    if (state == IDLE) {
        fallingFromClearing = false;
    }
    state = IDLE;
    stateTick = 0;
    poppingIndex = 0;
    poppingSkillChainLevel = 0;
}

void Block::switchBack() {
    state = SWITCHING_BACK;
    stateTick = BLOCK_SWITCHINGTIME;
}

void Block::switchForth() {
    state = SWITCHING_FORTH;
    stateTick = BLOCK_SWITCHINGTIME;
}

void Block::hover(const int hoveringTime) {
    state = HOVERING;
    stateTick = hoveringTime;
}

void Block::fall() {
    state = FALLING;
    stateTick = 0;
}

void Block::blink() {
    state = BLINKING;
    stateTick = BLOCK_BLINKINGTIME;
}

void Block::explode(const int explodingTime) {
    state = EXPLODING;
    stateTick = explodingTime;
}

void Block::reveal(const int revealingTime) {
    state = REVEALING;
    stateTick = revealingTime;
}

void Block::airBounce() {
    state = AIRBOUNCING;
    stateTick = 28;
}

void Block::toDelete() {
    state = TO_DELETE;
}

PanelEvent *Block::update() {
    PanelEvent *event = NULL;

    if (stateTick > 0) {
        stateTick--;
    }

    if (stateTick == 0) {
        switch (state) {
            case BLINKING:
                state = DONE_BLINKING;
                break;

            case SWITCHING_FORTH:
                state = DONE_SWITCHING_FORTH;
                break;

            case HOVERING:
                state = DONE_HOVERING;
                break;

            case EXPLODING:
                event = new PanelEvent(BLOCK_POP);
                state = DONE_EXPLODING;
                break;

            case REVEALING:
                event = new PanelEvent(BLOCK_POP);
                state = DONE_REVEALING;
                break;

            case AIRBOUNCING:
                state = DONE_AIRBOUNCING;
                break;

            case DONE_BLINKING:
            case DONE_EXPLODING:
            case DONE_REVEALING:
            case DONE_SWITCHING_FORTH:
            case DONE_HOVERING:
            case IDLE:
            case TO_DELETE:
            case FALLING:
            case SWITCHING_BACK:
            case DONE_AIRBOUNCING:
                break;
        }
    }
    return event;
}

bool Block::isComputable(Block *const block) {
    return block != NULL && block->movable && block->state == IDLE;
}

BlockSituation *Block::getSituation() {
    return new BlockSituation(id, type, state, stateTick, garbageBlockType, garbageOwner, combo, movable, combinable);
}

Block::~Block() {
}
