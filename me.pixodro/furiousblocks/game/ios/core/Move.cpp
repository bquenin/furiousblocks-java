//
//  Created by tsug on 4/1/12.
//
// To change the template use AppCode | Preferences | File Templates.
//

#include "Move.h"

Move::Move(unsigned char type) {
    this->type = type;
}

Move::Move(const Move *move) {
    this->type = move->type;
}