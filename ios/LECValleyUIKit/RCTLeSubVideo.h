//
//  RCTLeSubVideo.h
//  LECValleyUIKit
//
//  Created by RaoJia on 2016/12/18.
//  Copyright © 2016年 LeCloud. All rights reserved.
//

#import "RCTView.h"
#import "UIView+FindUIViewController.h"

#import "RCTLeVideoPlayerViewControllerDelegate.h"

@class RCTBridge;
@class LECPlayer;
@class LECPlayerOption;
@class LCBaseViewController;

@interface RCTLeSubVideo : UIView <RCTLeVideoPlayerViewControllerDelegate>

- (instancetype)initWithBridge:(RCTBridge *)bridge NS_DESIGNATED_INITIALIZER;

- (LCBaseViewController*)createPlayerViewController:(LECPlayer*)player withPlayerOption:(LECPlayerOption*)playerOption;


@end
