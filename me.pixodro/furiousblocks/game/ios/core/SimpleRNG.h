//
//  Created by tsug on 4/1/12.
//
// To change the template use AppCode | Preferences | File Templates.
//

#ifndef __SimpleRNG_H_
#define __SimpleRNG_H_

#include <iostream>

using namespace std;

class SimpleRNG {
    private:
    int m_w;
    int m_z;

    public:
    SimpleRNG(const int u = 521288629, const int v = 362436069);
    int nextInt();
    double nextDouble();
};

#endif //__SimpleRNG_H_
