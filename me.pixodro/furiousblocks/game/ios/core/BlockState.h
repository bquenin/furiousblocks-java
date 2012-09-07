//
//  Created by tsug on 3/31/12.
//
// To change the template use AppCode | Preferences | File Templates.
//

#ifndef __BlockState_H_
#define __BlockState_H_

enum BlockState {
    IDLE, //
    SWITCHING_FORTH, //
    DONE_SWITCHING_FORTH, //
    SWITCHING_BACK, //
    HOVERING, //
    DONE_HOVERING, //
    FALLING, //
    BLINKING, //
    DONE_BLINKING, //
    EXPLODING, //
    DONE_EXPLODING, //
    REVEALING, //
    DONE_REVEALING, //
    AIRBOUNCING, //
    DONE_AIRBOUNCING, //
    TO_DELETE
};

#endif //__BlockState_H_
