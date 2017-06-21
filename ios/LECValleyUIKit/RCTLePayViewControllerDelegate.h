//
//  RCTLePayViewControllerDelegate.h
//  LECValleyUIKit
//
//  Created by RaoJia on 2017/6/19.
//  Copyright © 2017年 LeCloud. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol RCTLePayViewControllerDelegate <NSObject>

- (void)payViewControllerWillDismiss:(UIViewController *)viewViewController;

- (void)payViewControllerDidDismiss:(UIViewController *)viewViewController;

@end
