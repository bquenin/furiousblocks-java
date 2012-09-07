//
//  Created by tsug on 3/28/12.
//
// To change the template use AppCode | Preferences | File Templates.
//

#include "Player.h"

using namespace std;

Player::Player() : id(123) {}

Player::~Player() {}

void Player::operator()() {
    while (true) {
        cout << name << " player thread tick " << endl;
        sleep(1);
    }

//        try {
//            final boolean isMostRecentData = situations.size() <= 1;
//            final PanelSituation ps = situations.take();
//            playerTick(ps, isMostRecentData);
//        } catch (final InterruptedException e) {
//            // Ignore
//        } catch (final Throwable t) {
//            t.printStackTrace();
//            return;
//        }
//    }
}

Move *Player::onMoveRequest() {
    return NULL;
}

void Player::onSituationUpdate(PanelSituation *panelSituation) {

}
