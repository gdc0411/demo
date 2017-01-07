//
//  RCTLeVideo.h
//  RCTLeVideo
//
//  Created by RaoJia on 25.11.2016.
//  Copyright © 2016 LeCloud. All rights reserved.
//

#import <React/RCTView.h>

#import "UIView+FindUIViewController.h"

#import "RCTLeVideoPlayerViewControllerDelegate.h"

@class RCTBridge;
@class LECPlayer;
@class LECPlayerOption;
@class LCBaseViewController;

typedef NS_ENUM (int,LCPlayerMode){
  LCPlayerVod = 10000,
  LCPlayerLive = 10001,
  LCPlayerActionLive = 10002,
  LCPlayerMobileLive = 10003,
  LCOtherMode
};


@class RCTEventDispatcher;

@interface RCTLeVideo : UIView <RCTLeVideoPlayerViewControllerDelegate>


- (instancetype)initWithBridge:(RCTBridge *)bridge NS_DESIGNATED_INITIALIZER;

- (LCBaseViewController*)createPlayerViewController:(LECPlayer*)player withPlayerOption:(LECPlayerOption*)playerOption;

@end
