//
//  RCTLeVideoPlayerViewController.m
//  RCTLeVideo
//
//  Created by RaoJia on 25.11.2016.
//  Copyright © 2016 LeCloud. All rights reserved.
//

#import "RCTLeVideoPlayerViewController.h"

@interface RCTLeVideoPlayerViewController ()
{
  
}

@end

@implementation RCTLeVideoPlayerViewController

- (void)viewDidDisappear:(BOOL)animated
{
  [super viewDidDisappear:animated];
  [_rctDelegate videoPlayerViewControllerDidDismiss:self];
}

- (void)viewWillDisappear:(BOOL)animated
{
  [_rctDelegate videoPlayerViewControllerWillDismiss:self];
  [super viewWillDisappear:animated];
}

- (void)viewWillLayoutSubviews
{
  [_rctDelegate videoPlayerViewShouldRotateToOrientation:self];
  [super viewWillLayoutSubviews];
}

//- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
//NS_DEPRECATED_IOS(2_0, 6_0){
//  return (interfaceOrientation == UIInterfaceOrientationLandscapeRight);
//}

//- (UIInterfaceOrientationMask)supportedInterfaceOrientations {
//  return UIInterfaceOrientationMaskLandscapeRight;
//}
//
//- (BOOL)shouldAutorotate
//{
//  return NO;
//}


@end
