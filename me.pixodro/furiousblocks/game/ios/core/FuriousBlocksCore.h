//
//  Created by tsug on 3/28/12.
//
// To change the template use AppCode | Preferences | File Templates.
//

#ifndef __FuriousBlocksCore_H_
#define __FuriousBlocksCore_H_

#include <iostream>
#include <map>
#include "atomic.h"

#include "Player.h"
#include "FBPanel.h"
#include "GameSituation.h"
#include "tbb.h"

using namespace std;
using namespace tbb;

class FuriousBlocksCore : public PanelListener {
private:
    map<Player *, tbb_thread *> playerToThread;
    int seed;
    BlockType initialBlockTypes[PANEL_WIDTH][PANEL_HEIGHT / 4 + 1];
    long tick;
    atomic<GameSituation *> gameSituation;

protected:
    map<Player *, FBPanel *> playerToPanel;

    void before();
    void after();
    void onCombo();

public:
    FuriousBlocksCore(const int seed);
    ~FuriousBlocksCore();
    void operator()(FuriousBlocksCore *core); // TBB mandates that you supply () operator
    void onTick(long tick);
    void addPlayer(Player *newPlayer);
    int hashCode();
    GameSituation *getGameSituation();
};

#endif //__FuriousBlocksCore_H_
