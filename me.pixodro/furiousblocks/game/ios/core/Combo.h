//
//  Created by tsug on 4/2/12.
//
// To change the template use AppCode | Preferences | File Templates.
//

#ifndef __Combo_H_
#define __Combo_H_

#include <iostream>
#include <set>
#include "FBPoint.h"
#include "Block.h"
#include "ComboSituation.h"

using namespace std;

class Combo {
    public:
    set<Block *> blocks;
    int owner;
    int skillChainLevel;

    Combo(const int owner);
    ComboSituation *getSituation();
};

#endif //__Combo_H_
