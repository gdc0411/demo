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


@class RCTEventDispatcher;

@interface RCTLeVideo : UIView <RCTLeVideoPlayerViewControllerDelegate>

- (instancetype)initWithEventDispatcher:(RCTEventDispatcher *)eventDispatcher NS_DESIGNATED_INITIALIZER;

- (LCBaseViewController*)createPlayerViewController:(LECVODPlayer*)player withPlayerItem:(LECPlayerOption*)playerItem;

@end
