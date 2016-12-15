//
//  RCTLeVideoPlayerViewController.h
//  RCTLeVideo
//
//  Created by RaoJia on 25.11.2016.
//  Copyright © 2016 LeCloud. All rights reserved.
//

#import "LCBaseViewController.h"
#import "RCTLeVideoPlayerViewControllerDelegate.h"

@interface RCTLeVideoPlayerViewController : LCBaseViewController
@property (nonatomic, weak) id<RCTLeVideoPlayerViewControllerDelegate> rctDelegate;
@end
