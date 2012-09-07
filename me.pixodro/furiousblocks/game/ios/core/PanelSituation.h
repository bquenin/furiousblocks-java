//
//  Created by tsug on 4/10/12.
//
// To change the template use AppCode | Preferences | File Templates.
//



#ifndef __PanelSituation_H_
#define __PanelSituation_H_

#include <iostream>
#include <set>
#include "BlockSituation.h"
#include "ComboSituation.h"
#include "PanelState.h"
#include "GarbageSituation.h"
#include "PanelEvent.h"
#include "FBPoint.h"
#import "FuriousBlocksCoreDefaults.h"

using namespace std;

class PanelSituation {
    public:
    BlockSituation ***blockSituations;
    bool isLocked;
    set<ComboSituation *> comboSituations;
    FBPoint cursorPosition;
    int scrollingOffset;
    PanelState state;
    int stateTick;
    set<GarbageSituation *> garbageStackSituation;
    int skillChainLevel;
    int freezingTime;
    bool gameOver;
    int wallOffset;
    set<PanelEvent *> events;
    bool gracing;
    int score;
    int rank;
    bool clearing;

    PanelSituation(BlockSituation ***blockSituations, bool isLocked, const set<ComboSituation *> &comboSituations, FBPoint cursorPosition, int scrollingOffset, PanelState state, int stateTick, const set<GarbageSituation *> &garbageStackSituation, int skillChainLevel, int freezingTime, bool gameOver, int wallOffset, const set<PanelEvent *> &events, bool gracing, int score, int rank, bool clearing)
    : blockSituations(blockSituations), isLocked(isLocked), comboSituations(comboSituations), cursorPosition(cursorPosition), scrollingOffset(scrollingOffset), state(state), stateTick(stateTick), garbageStackSituation(garbageStackSituation), skillChainLevel(skillChainLevel), freezingTime(freezingTime), gameOver(gameOver), wallOffset(wallOffset), events(events), gracing(gracing), score(score), rank(rank), clearing(clearing) {
    }

    ~PanelSituation() {
        for (set<ComboSituation *>::iterator i = comboSituations.begin(); i != comboSituations.end(); i++) {
            delete *i;
        }
        for (set<GarbageSituation *>::iterator i = garbageStackSituation.begin(); i != garbageStackSituation.end(); i++) {
            delete *i;
        }
        for (set<PanelEvent *>::iterator i = events.begin(); i != events.end(); i++) {
            delete *i;
        }
        // BlockSituations deletion
        for (int x = 0; x < PANEL_WIDTH; x++) {
            for (int y = 0; y < PANEL_HEIGHT + 1; y++) {
                if (this->blockSituations[x][y] != NULL) {
                    delete this->blockSituations[x][y];
                }
            }
        }
        for (int x = 0; x < PANEL_WIDTH; x++) {
            delete[] this->blockSituations[x];
        }
        delete[] this->blockSituations;
    }
};

#endif //__PanelSituation_H_
