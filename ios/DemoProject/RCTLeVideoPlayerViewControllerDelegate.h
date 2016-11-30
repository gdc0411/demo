//
//  RCTLeVideoPlayerViewControllerDelegate.h
//
//  Created by RaoJia on 25.11.2016.
//  Copyright © 2016 LeCloud. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LCBaseViewController.h"

@protocol RCTLeVideoPlayerViewControllerDelegate <NSObject>
- (void)videoPlayerViewControllerWillDismiss:(LCBaseViewController *)playerViewController;
- (void)videoPlayerViewControllerDidDismiss:(LCBaseViewController *)playerViewController;
- (void)videoPlayerViewShouldRotateToOrientation:(LCBaseViewController *)playerViewController;
@end
