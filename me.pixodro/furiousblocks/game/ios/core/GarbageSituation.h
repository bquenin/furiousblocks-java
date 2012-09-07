//
//  Created by tsug on 4/10/12.
//
// To change the template use AppCode | Preferences | File Templates.
//



#ifndef __GarbageSituation_H_
#define __GarbageSituation_H_

#include <iostream>

class GarbageSituation {
    public:
    const int width;
    const int height;
    const int owner;

    GarbageSituation(const int width, const int height, const int owner) : width(width), height(height), owner(owner) {
    }
};

#endif //__GarbageSituation_H_
