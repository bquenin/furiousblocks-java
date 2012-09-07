//
//  Created by tsug on 4/1/12.
//
// To change the template use AppCode | Preferences | File Templates.
//

#ifndef __Move_H_
#define __Move_H_

class Move {
    public:
    unsigned char type;

    Move(const unsigned char type);
    Move(const Move *move);
};

#endif //__Move_H_
