//
//  Created by tsug on 4/2/12.
//
// To change the template use AppCode | Preferences | File Templates.
//

#include "Combo.h"

Combo::Combo(int owner) {
    this->owner = owner;
    this->skillChainLevel = 1;
}

ComboSituation *Combo::getSituation() {
    return new ComboSituation(blocks.size(), skillChainLevel);
}
