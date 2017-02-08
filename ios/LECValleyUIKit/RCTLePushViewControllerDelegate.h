//
//  RCTLePushViewControllerDelegate.h
//  LECValleyUIKit
//
//  Created by RaoJia on 2017/2/8.
//  Copyright © 2017年 LeCloud. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol RCTLePushViewControllerDelegate <NSObject>
- (void)videoPlayerViewControllerWillDismiss:(UIViewController *)playerViewController;
- (void)videoPlayerViewControllerDidDismiss:(UIViewController *)playerViewController;
- (void)videoPlayerViewShouldRotateToOrientation:(UIViewController *)playerViewController;
@end
