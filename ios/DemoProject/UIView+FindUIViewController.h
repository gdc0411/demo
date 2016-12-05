//
//  UIView+FindUIViewController.h
//  RCTLeVideo
//
//  Created by RaoJia on 28.11.2016.
//  Copyright © 2016 LeCloud. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIView (FindUIViewController)
- (UIViewController *) firstAvailableUIViewController;
- (id) traverseResponderChainForUIViewController;
@end
