//
//  AppDelegate.h
//  FuriousBlocks
//
//  Created by Bertrand Quenin on 3/28/12.
//  Copyright __MyCompanyName__ 2012. All rights reserved.
//

#import <UIKit/UIKit.h>

@class RootViewController;

@interface AppDelegate : NSObject <UIApplicationDelegate> {
    UIWindow *window;
    RootViewController *viewController;
}

@property(nonatomic, retain) UIWindow *window;

@end
