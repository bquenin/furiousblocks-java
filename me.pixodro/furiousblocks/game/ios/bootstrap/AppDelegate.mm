//
//  AppDelegate.m
//  FuriousBlocks
//
//  Created by Bertrand Quenin on 3/28/12.
//  Copyright __MyCompanyName__ 2012. All rights reserved.
//

#import "cocos2d.h"

#import "AppDelegate.h"
#import "GameConfig.h"
#import "RootViewController.h"
#import "Game.h"
//#import "FuriousBlocksCoreConfiguration.h"

@implementation AppDelegate

@synthesize window;

- (void)removeStartupFlicker {
    //
    // THIS CODE REMOVES THE STARTUP FLICKER
    //
    // Uncomment the following code if you Application only supports landscape mode
    //
#if GAME_AUTOROTATION == kGameAutorotationUIViewController

//	CC_ENABLE_DEFAULT_GL_STATES();
//	CCDirector *director = [CCDirector sharedDirector];
//	CGSize size = [director winSize];
//	CCSprite *sprite = [CCSprite spriteWithFile:@"Default.png"];
//	sprite.position = ccp(size.width/2, size.height/2);
//	sprite.rotation = -90;
//	[sprite visit];
//	[[director openGLView] swapBuffers];
//	CC_ENABLE_DEFAULT_GL_STATES();

#endif // GAME_AUTOROTATION == kGameAutorotationUIViewController	
}

- (void)applicationDidFinishLaunching:(UIApplication *)application {
    // Init the window
    window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];

    // Try to use CADisplayLink director
    // if it fails (SDK < 3.1) use the default director
    if (![CCDirector setDirectorType:kCCDirectorTypeDisplayLink]) {
        [CCDirector setDirectorType:kCCDirectorTypeDefault];
    }

    CCDirector *director = [CCDirector sharedDirector];

    // Init the View Controller
    viewController = [[RootViewController alloc] initWithNibName:nil bundle:nil];
    viewController.wantsFullScreenLayout = YES;

    //
    // Create the EAGLView manually
    //  1. Create a RGB565 format. Alternative: RGBA8
    //	2. depth format of 0 bit. Use 16 or 24 bit for 3d effects, like CCPageTurnTransition
    //
    //
    EAGLView *glView = [EAGLView viewWithFrame:[window bounds]
                                   pixelFormat:kEAGLColorFormatRGB565    // kEAGLColorFormatRGBA8
                                   depthFormat:0                        // GL_DEPTH_COMPONENT16_OES
    ];

    // attach the openglView to the director
    [director setOpenGLView:glView];

    // Enables High Res mode (Retina Display) on iPhone 4 and maintains low res on all other devices
    if (![director enableRetinaDisplay:YES])
    CCLOG(@"Retina Display Not supported");

    //
    // VERY IMPORTANT:
    // If the rotation is going to be controlled by a UIViewController
    // then the device orientation should be "Portrait".
    //
    // IMPORTANT:
    // By default, this template only supports Landscape orientations.
    // Edit the RootViewController.m file to edit the supported orientations.
    //
#if GAME_AUTOROTATION == kGameAutorotationUIViewController
    [director setDeviceOrientation:kCCDeviceOrientationPortrait];
#else
    [director setDeviceOrientation:kCCDeviceOrientationPortrait];
#endif

    [director setAnimationInterval:1.0 / 60];
    [director setDisplayFPS:YES];
//    [director setDepthTest:NO];

    // make the OpenGLView a child of the view controller
    [viewController setView:glView];

    // make the View Controller a child of the main window
    [window addSubview:viewController.view];

    [window makeKeyAndVisible];

    // Default texture format for PNG/BMP/TIFF/JPEG/GIF images
    // It can be RGBA8888, RGBA4444, RGB5_A1, RGB565
    // You can change anytime.
    [CCTexture2D setDefaultAlphaPixelFormat:kCCTexture2DPixelFormat_RGBA8888];

    // Removes the startup flicker
    [self removeStartupFlicker];

    // Run the intro Scene
    [[CCDirector sharedDirector] runWithScene:[Game scene]];

////    tbb::task_scheduler_init init(1);
//    // Create and use a new C++ object
//    FuriousBlocksCoreConfiguration *configuration = new FuriousBlocksCoreConfiguration();
//    FuriousBlocksCore *core = new FuriousBlocksCore(configuration, 0);
//    Player *player = new Player();
//    tbb::tbb_thread *thread = new tbb::tbb_thread(*core);
//
//
//    core->addPlayer(player);
//
//    for (int i = 0; i < 100; i++) {
//        core->onTick(i);
//        cout << "hashCode = " << core->hashCode() << endl;
//    }
//
//    delete core;
//    delete player;
//    delete configuration;
}

- (void)applicationWillResignActive:(UIApplication *)application {
    [[CCDirector sharedDirector] pause];
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    [[CCDirector sharedDirector] resume];
}

- (void)applicationDidReceiveMemoryWarning:(UIApplication *)application {
    [[CCDirector sharedDirector] purgeCachedData];
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    [[CCDirector sharedDirector] stopAnimation];
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
    [[CCDirector sharedDirector] startAnimation];
}

- (void)applicationWillTerminate:(UIApplication *)application {
    CCDirector *director = [CCDirector sharedDirector];

    [[director openGLView] removeFromSuperview];

    [viewController release];

    [window release];

    [director end];
}

- (void)applicationSignificantTimeChange:(UIApplication *)application {
    [[CCDirector sharedDirector] setNextDeltaTimeZero:YES];
}

- (void)dealloc {
    [[CCDirector sharedDirector] end];
    [window release];
    [super dealloc];
}

@end
