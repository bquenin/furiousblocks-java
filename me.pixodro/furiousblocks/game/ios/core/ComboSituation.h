//
//  Created by tsug on 4/10/12.
//
// To change the template use AppCode | Preferences | File Templates.
//



#ifndef __ComboSituation_H_
#define __ComboSituation_H_

#include <iostream>

class ComboSituation {
    public:
    const int size;
    const int skillChainLevel;

    ComboSituation(const int size, const int skillChainLevel) : size(size), skillChainLevel(skillChainLevel) {
    }
};

#endif //__ComboSituation_H_
