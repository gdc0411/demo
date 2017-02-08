//
//  LetvCloudLiveViewController.m
//  LECValleyUIKit
//
//  Created by RaoJia on 2017/2/8.
//  Copyright © 2017年 Facebook. All rights reserved.
//


#import "LetvCloudLiveViewController.h"
#import "CaptureStreamingViewController.h"
#import <CommonCrypto/CommonDigest.h>
#import "LCStreamingManager.h"

@interface LetvCloudLiveViewController ()
@property (weak, nonatomic) IBOutlet UILabel *letvCloudLiveLabel;
@property (weak, nonatomic) IBOutlet UITextField *textUserID;
@property (weak, nonatomic) IBOutlet UITextField *textAppKey_cloud;
@property (weak, nonatomic) IBOutlet UITextField *textActivityID;
@property (weak, nonatomic) IBOutlet UISwitch *switchThirdView;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *letvCloudLiveid;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *userId;

@end

@implementation LetvCloudLiveViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    if ([UIScreen mainScreen].bounds.size.height <= 480) {
        self.letvCloudLiveid.constant = 20;
        self.userId.constant = 10;
    }
    CGColorRef whiteColor = [UIColor whiteColor].CGColor;
    self.letvCloudLiveLabel.layer.borderWidth = 1.0;
    self.letvCloudLiveLabel.layer.borderColor = whiteColor;
    self.letvCloudLiveLabel.textColor = [UIColor blackColor];
    self.letvCloudLiveLabel.backgroundColor = [UIColor whiteColor];
    self.letvCloudLiveLabel.layer.masksToBounds = YES;
    self.letvCloudLiveLabel.layer.cornerRadius = 6;
    self.textUserID.layer.borderColor = whiteColor;
    self.textActivityID.layer.borderColor = whiteColor;
    self.textAppKey_cloud.layer.borderColor = whiteColor;
}

- (IBAction)onViewTapped:(id)sender {
    [self.textUserID resignFirstResponder];
    [self.textActivityID resignFirstResponder];
    [self.textAppKey_cloud resignFirstResponder];
}

- (IBAction)onSwipeDownOnView:(id)sender {
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (UIInterfaceOrientationMask)supportedInterfaceOrientations {
    return UIInterfaceOrientationMaskPortrait;
}

- (UIInterfaceOrientation)preferredInterfaceOrientationForPresentation {
    return UIInterfaceOrientationPortrait;
}

#pragma mark - third view actions
- (IBAction)onStartWithActivityIdClicked:(id)sender {
    CaptureStreamingViewControllerOrientation orientation;
    if (self.switchThirdView.on) {
        orientation = CaptureStreamingViewControllerOrientationLandscape;
    }
    else {
        orientation = CaptureStreamingViewControllerOrientationPortrait;
    }
    
    NSBundle *bundle = [NSBundle bundleWithURL:[[NSBundle mainBundle] URLForResource:@"LCStreamingBundle" withExtension:@"bundle"]];
    
    [[LCStreamingManager sharedManager] enableAutoUploadLog:YES];
    CaptureStreamingViewController * viewController = [[CaptureStreamingViewController alloc] initWithNibName:@"CaptureStreamingViewController"
                                                                                                       bundle:bundle
                                                                                                        title:self.textActivityID.text
                                                                                                   activityId:self.textActivityID.text
                                                                                                       userId:self.textUserID.text
                                                                                                    secretKey:self.textAppKey_cloud.text
                                                                                                  orientation:orientation];
    viewController.bitRate = 1024 * 1024;
    viewController.frameRate = 24;
    //由于iPhone4/4s性能问题，使用较低分辨率
    CGSize screenSize = [[UIScreen mainScreen] bounds].size;
    if (CGSizeEqualToSize(screenSize, CGSizeMake(320, 480)) || CGSizeEqualToSize(screenSize, CGSizeMake(480, 320))) {
        viewController.preset = CaptureStreamingViewControllerPreset320x240;
    }
    [self presentViewController:viewController animated:YES completion:nil];
    
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
