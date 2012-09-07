//
//  Created by tsug on 5/4/12.
//
// To change the template use AppCode | Preferences | File Templates.
//



#ifndef __ThreadExecutor_H_
#define __ThreadExecutor_H_

#include <iostream>
#include "FuriousBlocksCore.h"

template <class T>
class ThreadExecutor {
public:
    void operator()(T *core) {
        for (int tick = 0; tick < 10000; tick++) {
//            cout << "thread running in parallel @ addr " << core << endl;
            core->onTick(tick);
            usleep(60000);
        }
    }

};

#endif //__ThreadExecutor_H_
