//
//  Created by tsug on 4/1/12.
//
// To change the template use AppCode | Preferences | File Templates.
//

#ifndef __FBPoint_H_
#define __FBPoint_H_

#include <iostream>

class FBPoint {
    public:
    int x;
    int y;

    FBPoint(const FBPoint & p);
    FBPoint(int x = 0, int y = 0);
};

#endif //__FBPoint_H_
