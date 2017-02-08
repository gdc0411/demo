//
//  RCTLePushViewControllerDelegate.h
//  LECValleyUIKit
//
//  Created by RaoJia on 2017/2/8.
//  Copyright © 2017年 LeCloud. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol RCTLePushViewControllerDelegate <NSObject>

- (void)pushViewControllerWillDismiss:(UIViewController *)viewViewController;

- (void)pushViewControllerDidDismiss:(UIViewController *)viewViewController;

- (void)pushViewShouldRotateToOrientation:(UIViewController *)viewViewController;

@end
