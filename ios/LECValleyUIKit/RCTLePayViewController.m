//
//  RCTLePayViewController.m
//  LECValleyUIKit
//
//  Created by RaoJia on 2017/6/19.
//  Copyright © 2017年 LeCloud. All rights reserved.
//

#import "RCTLePayViewController.h"

@interface RCTLePayViewController ()

@end

@implementation RCTLePayViewController

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    [_viewControllerDelegate payViewControllerDidDismiss:self];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [_viewControllerDelegate payViewControllerWillDismiss:self];
    [super viewWillDisappear:animated];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
