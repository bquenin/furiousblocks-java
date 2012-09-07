//
//  Created by tsug on 3/31/12.
//
// To change the template use AppCode | Preferences | File Templates.
//

#ifndef __Panel_H_
#define __Panel_H_

#include <iostream>
#include <set>
#include <vector>
#include "FuriousBlocksCoreDefaults.h"
#include "BlockType.h"
#include "PanelEvent.h"
#include "Block.h"
#include "SimpleRNG.h"
#include "FBPoint.h"
#include "PanelState.h"
#include "Move.h"
#include "Combo.h"
#include "PanelListener.h"
#include "GarbageSituation.h"
#include "PanelSituation.h"

using namespace std;

class FBPanel {
public:

    class BlockBar {
    private:
        int id;
        bool doneRevealing;

    protected:
        FBPanel *parent;
        Block ***blocks;
        set<Block *> barBlocks;

    public:
        int owner;
        int width;
        int height;
        BlockBar(FBPanel *parent, const int width, const int height, const int owner);
        ~BlockBar();
        bool contains(Block *block) const;
        bool hasToFall(int xOrigin, int yOrigin);
        void fall(int xOrigin, int yOrigin);
        void idle();
        int blink(int poppingIndex);
        bool isRevealing();
        bool isDoneRevealing();
        virtual void onDoneRevealing() = 0;
    };

    class Clearing {
    private:
        set<FBPanel::BlockBar *> bars;

    public:
        void addBlockBar(BlockBar *bar);
        bool isDoneRevealing();
        void doneRevealing();
        bool contains(Block *block);
        bool isEmpty();
        void removeBar(BlockBar *bar);
    };

    class Garbage : public BlockBar {
    private:
        const Combo *triggeringCombo;

    public:
        bool skill;

        Garbage(FBPanel *parent, const int width, const int height, const int owner, const bool skill);
        ~Garbage();
        void inject(int x, int y);
        int reveal(const int xOrigin, const int yOrigin, int revealingTime, Clearing *parentClearing);
        void onDoneRevealing();
        int blink(int poppingIndex, Combo const *combo);
        GarbageSituation *getSituation();
    };

    class BlockLine : public BlockBar {
    public:
        BlockLine(FBPanel *parent, const int width, const int owner);
        ~BlockLine();
        void inject(int x, int y);
        int reveal(int xOrigin, int yOrigin, int revealingTime);
        void onDoneRevealing();
    };

    static const int numberOfRegularBlocks = 5; // 5 is the number of "regular" blocks type
    bool gameOver;

    FBPanel(unsigned int seed, int playerId, BlockType initialBlockTypes[PANEL_WIDTH][PANEL_HEIGHT / 4 + 1]);
    ~FBPanel();
    void addPanelListener(PanelListener *panelListener);
    Block *newRandom(const BlockType type = (BlockType) -1, int poppingIndex = 0, int skillChainLevel = 0);
    Block *newBlock(const BlockType type, int index = 0, int skillChainLevel = 0) const;
    PanelSituation *onTick(const long tick);
    void processMove();
    void scrolling(const long tick);
    void gracePeriod();
    void freeze(const int freezingTime);
    void newLine();
    void quake();
    Combo *getComboByBlock(Block *block) const;
    Garbage *getGarbageByBlock(Block *block) const;
    Clearing *getClearingByBlock(Block *block) const;
    Combo *detectCombo();
    void processCombo(Combo *combo);
    Garbage *newGarbage(int width, int height, int owner, bool skill);
    int hashCode();
    void submitMove(Move *move);

protected:
    static const int X = PANEL_WIDTH;
    static const int Y_DISPLAY = PANEL_HEIGHT;;
    static const int Y = Y_DISPLAY + (Y_DISPLAY * 4);

    Block *blocks[X][Y];
    set<Combo *> combos;

private:
    static const int INITIAL_SCROLLING_SPEED = CORE_FREQUENCY;
    static const long NEXT_LEVEL = CORE_FREQUENCY * 1000; // Next level every minute

    int playerId;
    bool comboMask[X][Y];

    int lastIndex;
    SimpleRNG *random;

    set<PanelListener *> panelListeners;
    set<PanelEvent *> events;

    set<Garbage *> garbages;
    set<Clearing *> clearings;
    vector<Garbage *> garbageStack;

    FBPoint cursor;
    long localTick;
    PanelState state;
    int stateTick;

    int levelScrollingSpeed;
    int scrollingSpeed;
    int scrollingOffset;
    int freezingTime;
    int bonusFreezingTime;
    int skillChainLevel;
    Move *move;
    bool locked;
    bool lifting;
    bool gracing;
    bool switching;
    int wallOffset;

    int score;
    int rank;

    void mechanics();
    void dropGarbages();
    void stackGarbage(Garbage *garbage);
    PanelSituation *getSituation();
};

#endif //__Panel_H_
