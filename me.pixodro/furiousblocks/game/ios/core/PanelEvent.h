//
//  Created by tsug on 3/31/12.
//
// To change the template use AppCode | Preferences | File Templates.
//

#include "PanelEventType.h"

#ifndef __PanelEvent_H_
#define __PanelEvent_H_

class PanelEvent {
    private:
    PanelEventType type;

    public:
    int data1;
    int data2;

    PanelEvent(const PanelEventType &type, const int data1 = 0, const int data2 = 0);

};

#endif //__PanelEvent_H_
