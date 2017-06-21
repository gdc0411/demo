//
//  RCTLePayViewController.h
//  LECValleyUIKit
//
//  Created by RaoJia on 2017/6/19.
//  Copyright © 2017年 LeCloud. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RCTLePayViewControllerDelegate.h"

@interface RCTLePayViewController : UIViewController

@property (nonatomic, weak) id<RCTLePayViewControllerDelegate> viewControllerDelegate;

@end
