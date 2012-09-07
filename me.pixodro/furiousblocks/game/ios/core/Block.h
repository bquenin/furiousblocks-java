//
//  Created by tsug on 3/31/12.
//
// To change the template use AppCode | Preferences | File Templates.
//

#ifndef __Block_H_
#define __Block_H_

#include <iostream>
#include "BlockType.h"
#include "BlockState.h"
#include "PanelEvent.h"
#include "BlockSituation.h"

class Block {
    public:
    int id;
    BlockType type;
    BlockState state;
    int stateTick;
    int garbageBlockType;
    int garbageOwner;
    int poppingIndex;
    int poppingSkillChainLevel;
    bool combo;
    bool fallingFromClearing;
    bool movable;
    bool combinable;

    Block(const int id, const BlockType type, const int index = 0, const int skillChainLevel = 0);
    ~Block();
    void idle();
    void switchBack();
    void switchForth();
    void hover(int hoveringTime);
    void fall();
    void land();
    void blink();
    void explode(int explodingTime);
    void reveal(int revealingTime);
    void airBounce();
    void toDelete();
    PanelEvent *update();
    static bool isComputable(Block *const block);
    BlockSituation *getSituation();
};

#endif //__Block_H_
