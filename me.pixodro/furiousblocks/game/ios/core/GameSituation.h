//
//  Created by tsug on 4/10/12.
//
// To change the template use AppCode | Preferences | File Templates.
//



#ifndef __GameSituation_H_
#define __GameSituation_H_

#include <iostream>
#include <map>
#include "PanelSituation.h"

using namespace std;

class GameSituation {
    public:
    map<int, PanelSituation *> panelSituations;
    const long tick;

    GameSituation(const map<int, PanelSituation *> &panelSituations, const long tick) : panelSituations(panelSituations), tick(tick) {
//        final Map<Integer, Integer> scoreToPlayer = new TreeMap<Integer, Integer>();
//        for (final Map.Entry<Integer, PanelSituation> entry : panelSituations.entrySet()) {
//            scoreToPlayer.put(entry.getKey(), entry.getValue().getScore());
//        }
//
//        final Map<Integer, Integer> sortedMap = sortByValues(scoreToPlayer);
//        int rank = 1;
//        for (final int key : sortedMap.keySet()) {
//            panelSituations.get(key).setRank(rank++);
//        }
    }

    ~GameSituation() {
        for (map<int, PanelSituation *>::iterator entry = panelSituations.begin(); entry != panelSituations.end(); entry++) {
            delete entry->second;
        }
        panelSituations.clear();
    }

};

#endif //__GameSituation_H_
