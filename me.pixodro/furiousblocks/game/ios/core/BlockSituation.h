//
//  Created by tsug on 4/10/12.
//
// To change the template use AppCode | Preferences | File Templates.
//



#ifndef __BlockSituation_H_
#define __BlockSituation_H_

#include <iostream>
#include "BlockState.h"
#include "BlockType.h"


class BlockSituation {
    public:
    long id;
    BlockType type;
    BlockState state;
    int stateTick;
    int garbageBlockType;
    int garbageOwner;
    bool combo;
    bool movable;
    bool combinable;

    BlockSituation(long id, BlockType type, BlockState state, int stateTick, int garbageBlockType, int garbageOwner, bool combo, bool movable, bool combinable)
    : id(id), type(type), state(state), stateTick(stateTick), garbageBlockType(garbageBlockType), garbageOwner(garbageOwner), combo(combo), movable(movable), combinable(combinable) {
    }

    const static bool isComputable(const BlockSituation *blockSituation) {
        return blockSituation != NULL && blockSituation->movable && blockSituation->state == IDLE;
    }

    const static bool isMovable(const BlockSituation *blockSituation) {
        return blockSituation == NULL && blockSituation->state == IDLE && blockSituation->type != GARBAGE;
    }

};

#endif //__BlockSituation_H_
