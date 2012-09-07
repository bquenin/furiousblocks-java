//
//  Created by tsug on 3/28/12.
//
// To change the template use AppCode | Preferences | File Templates.
//

#ifndef __Player_H_
#define __Player_H_

#include <iostream>
#include <string>
#include "Move.h"
#include "PanelSituation.h"

using namespace std;

class Player {
    public:
    int id;
    string name;

    Player();
    ~Player();
    void operator ()(); // TBB mandates that you supply () operator

    Move *onMoveRequest();
    void onSituationUpdate(PanelSituation *panelSituation);
};

#endif //__Player_H_
