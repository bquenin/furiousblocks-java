//
// Created by tsug on 5/4/12.
//
// To change the template use AppCode | Preferences | File Templates.
//



#ifndef __referenceManager_H_
#define __referenceManager_H_

#include <iostream>
#include <map>
#include <assert.h>
#include "tbb.h"

using namespace std;
using namespace tbb;

class referenceManager {
private:
    referenceManager() {};
    ~referenceManager() {};
    static map<void *, atomic<int> > pointersToReferenceCount;

public:
    template <typename T>
    static void reference(T *ptr) {
        pointersToReferenceCount[ptr]++;
    };

    template <typename T>
    static void release(T *ptr) {
        int referenceCount = pointersToReferenceCount[ptr]--;
        assert(referenceCount >= 0);
        if (referenceCount == 0) {
            delete ptr;
            pointersToReferenceCount.erase(ptr);
        }
    };
};

#endif //__referenceManager_H_
