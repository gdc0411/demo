//
//  RCTLePushViewController.m
//  LECValleyUIKit
//
//  Created by RaoJia on 2017/2/8.
//  Copyright © 2017年 Facebook. All rights reserved.
//

#import "RCTLePushViewController.h"

@implementation RCTLePushViewController

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [_viewControllerDelegate pushViewControllerDidDismiss:self];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [_viewControllerDelegate pushViewControllerWillDismiss:self];
    [super viewWillDisappear:animated];
}

- (void)viewWillLayoutSubviews
{
//    [_viewControllerDelegate pushViewShouldRotateToOrientation:self];
    [super viewWillLayoutSubviews];
}


@end
