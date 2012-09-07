//
//  Created by tsug on 3/28/12.
//
// To change the template use AppCode | Preferences | File Templates.
//

#include "FuriousBlocksCore.h"
#include "referenceManager.h"

FuriousBlocksCore::FuriousBlocksCore(int seed) : seed(seed) {
    this->gameSituation = NULL;
    SimpleRNG *random = new SimpleRNG(seed);

    // Initial panel
    for (int y = 0; y < PANEL_HEIGHT / 4 + 1; y++) {
        for (int x = 0; x < PANEL_WIDTH; x++) {
            initialBlockTypes[x][y] = (BlockType) (random->nextInt() % FBPanel::numberOfRegularBlocks);
        }
    }
}

FuriousBlocksCore::~FuriousBlocksCore() {
    for (map<Player *, FBPanel *>::const_iterator entry = playerToPanel.begin(); entry != playerToPanel.end(); entry++) {
        delete entry->second;
    }
    playerToPanel.clear();
    playerToThread.clear();
}

void FuriousBlocksCore::addPlayer(Player *newPlayer) {
    FBPanel *panel = new FBPanel(seed + newPlayer->id, newPlayer->id, initialBlockTypes);
    panel->addPanelListener(this);
    //        panel.stackGarbage(panel.newGarbage(3, 1, newPlayer.getId(), true));
    //    panel.stackGarbage(panel.newGarbage(6, 1, newPlayer.getId(), true));
    //    panel.stackGarbage(panel.newGarbage(6, 2, newPlayer.getId(), true));
    //    panel.stackGarbage(panel.newGarbage(6, 2, newPlayer.getId(), true));

//    playerToThread.put(newPlayer, new Thread(newPlayer, newPlayer.getName()));
    playerToPanel[newPlayer] = panel;
}

int FuriousBlocksCore::hashCode() {
    if (playerToPanel.empty()) {
        return 0;
    }

    int hash = 1;
    for (map<Player *, FBPanel *>::const_iterator entry = playerToPanel.begin(); entry != playerToPanel.end(); entry++) {
        hash = 31 * hash + entry->second->hashCode();
    }
    return hash;
}

GameSituation *FuriousBlocksCore::getGameSituation() {
    referenceManager::reference(gameSituation.load());
    return gameSituation.load();
}

void FuriousBlocksCore::before() {
//    for (map<Player *, FBPanel *>::const_iterator entry = playerToPanel.begin(); entry != playerToPanel.end(); entry++) {
//        playerToThread[entry->first] = new tbb::tbb_thread(*entry->first);
//    }
}

void FuriousBlocksCore::onTick(long tick) {
    // Request player moves
    for (map<Player *, FBPanel *>::const_iterator entry = playerToPanel.begin(); entry != playerToPanel.end(); entry++) {
        Player *player = entry->first;
        FBPanel *panel = entry->second;
        if (panel->gameOver) {
            continue;
        }
        Move *move = player->onMoveRequest();
        if (move != NULL) {
            panel->submitMove(move);
            delete move;
        }
    }

    // Panels tick
    map<int, PanelSituation *> panelSituations;
    for (map<Player *, FBPanel *>::const_iterator entry = playerToPanel.begin(); entry != playerToPanel.end(); entry++) {
        Player *player = entry->first;
        FBPanel *panel = entry->second;
        PanelSituation *panelSituation = panel->onTick(tick);
        panelSituations[player->id] = panelSituation;
        if (panel->gameOver) {
            continue;
        }
        player->onSituationUpdate(panelSituation);
    }

    // Update game situation and keep a reference on it
    GameSituation *newSituation = new GameSituation(panelSituations, tick);
    referenceManager::reference(newSituation);

    // Share it and release the previous one
    GameSituation *previousSituation = gameSituation.fetch_and_store(newSituation);
    if (previousSituation != NULL) {
        referenceManager::release(previousSituation);
    }
}

void FuriousBlocksCore::after() {
}

void FuriousBlocksCore::onCombo() {
//    for (final Map.Entry<Player, Panel> entry : playerToPanel.entrySet()) {
//        final Player player = entry.getKey();
//        final Panel panel = entry.getValue();
//        if ((player.getId() != combo.getOwner()) && !panel.isGameOver()) {
//            if (combo.getSkillChainLevel() > 1) {
//                panel.stackGarbage(panel.newGarbage(configuration.getPanelWidth(), (combo.getSkillChainLevel() - 1), combo.getOwner(), true));
//            }
//
//            if (combo.size() > 3) {
//                // Loop and decrease the size of the combo to generate
//                // adequate garbages
//                for (int size = (combo.size() - 1); size >= 0; ) {
//                    if (size >= configuration.getPanelWidth()) {
//                        panel.stackGarbage(panel.newGarbage(configuration.getPanelWidth(), 1, combo.getOwner(), false));
//                        size -= configuration.getPanelWidth();
//                    } else if (size > 2) {
//                        panel.stackGarbage(panel.newGarbage(size, 1, combo.getOwner(), false));
//                        break;
//                    } else {
//                        break;
//                    }
//                }
//            }
//        }
//    }
}

