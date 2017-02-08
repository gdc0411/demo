//
//  RCTLePushViewController.h
//  LECValleyUIKit
//
//  Created by RaoJia on 2017/2/8.
//  Copyright © 2017年 LeCloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RCTLePushViewControllerDelegate.h"

@interface RCTLePushViewController : UIViewController

@property (nonatomic, weak) id<RCTLePushViewControllerDelegate> viewControllerDelegate;

@end
