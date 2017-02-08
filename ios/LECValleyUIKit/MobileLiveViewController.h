//
//  MobileLiveViewController.h
//  LECValleyUIKit
//
//  Created by RaoJia on 2017/2/8.
//  Copyright © 2017年 Facebook. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface MobileLiveViewController : UIViewController

@property (weak, nonatomic) IBOutlet UISegmentedControl *segment;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;
@property (assign, nonatomic) BOOL isScroll;
@property (assign, nonatomic) NSInteger inter;

@end
