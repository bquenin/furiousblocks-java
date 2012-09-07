//
//  Created by tsug on 3/31/12.
//
// To change the template use AppCode | Preferences | File Templates.
//

#include "PanelEvent.h"

PanelEvent::PanelEvent(const PanelEventType &type, const int data1, const int data2) {
    this->type = type;
    this->data1 = data1;
    this->data2 = data2;
}