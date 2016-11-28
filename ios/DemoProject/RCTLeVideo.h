//
//  RCTLeVideo.h
//  RCTLeVideo
//
//  Created by RaoJia on 25.11.2016.
//  Copyright © 2016 LeCloud. All rights reserved.
//

#import "RCTView.h"
#import "UIView+FindUIViewController.h"

#import "RCTLeVideoPlayerViewControllerDelegate.h"

#import "LECVODPlayer.h"
#import "LECPlayerOption.h"
#import "LCBaseViewController.h"


typedef NS_ENUM (int,LCPlayerMode){
  LCPlayerVod = 10000,
  LCPlayerLive = 10001,
  LCPlayerActionLive = 10002,
  LCPlayerMobileLive = 10003,
  LCOtherMode
};


@class RCTEventDispatcher;

@interface RCTLeVideo : UIView <RCTLeVideoPlayerViewControllerDelegate>

//@property (nonatomic, copy) RCTBubblingEventBlock onVideoSourceLoad;


- (instancetype)initWithEventDispatcher:(RCTEventDispatcher *)eventDispatcher NS_DESIGNATED_INITIALIZER;

- (LCBaseViewController*)createPlayerViewController:(LECVODPlayer*)player withPlayerOption:(LECPlayerOption*)playerOption;

@end
