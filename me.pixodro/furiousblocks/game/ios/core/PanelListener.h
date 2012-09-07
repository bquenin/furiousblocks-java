//
//  Created by tsug on 3/31/12.
//
// To change the template use AppCode | Preferences | File Templates.
//

#ifndef __PanelListener_H_
#define __PanelListener_H_

class PanelListener {
    public:
    virtual ~PanelListener() {};

    private:
    virtual void onCombo() = 0;
};

#endif //__PanelListener_H_
