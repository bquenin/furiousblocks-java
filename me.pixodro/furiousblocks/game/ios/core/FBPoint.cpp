//
//  Created by tsug on 4/1/12.
//
// To change the template use AppCode | Preferences | File Templates.
//

#include "FBPoint.h"

FBPoint::FBPoint(const FBPoint & p) {
    this->x = p.x;
    this->y = p.y;
}

FBPoint::FBPoint(int x, int y) {
    this->x = x;
    this->y = y;
}
