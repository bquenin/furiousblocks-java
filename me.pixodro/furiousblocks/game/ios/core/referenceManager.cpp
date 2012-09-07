//
// Created by tsug on 5/4/12.
//
// To change the template use AppCode | Preferences | File Templates.
//


#include "referenceManager.h"

map<void *, atomic<int> > referenceManager::pointersToReferenceCount;



