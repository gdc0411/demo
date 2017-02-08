//
//  MobileLiveViewController.m
//  LECValleyUIKit
//
//  Created by RaoJia on 2017/2/8.
//  Copyright © 2017年 Facebook. All rights reserved.
//

#import "MobileLiveViewController.h"
#import "CaptureStreamingViewController.h"
#import "LCStreamingManager.h"
#import <CommonCrypto/CommonDigest.h>

#define widthScreen [UIScreen mainScreen].bounds.size.width
@interface MobileLiveViewController ()

@property (weak, nonatomic) IBOutlet UIView *viewWrap;

@property (weak, nonatomic) IBOutlet UIView *viewHiddenPanel;
@property (weak, nonatomic) IBOutlet UILabel *labelGeneratedUrl;
@property (weak, nonatomic) IBOutlet UIButton *btnStartLiveByGeneratedURL;
@property (weak, nonatomic) IBOutlet UITextField *textDomainName;
@property (weak, nonatomic) IBOutlet UITextField *textAppKey;
@property (weak, nonatomic) IBOutlet UITextField *textStreamName;
@property (weak, nonatomic) IBOutlet UITextField *textRTMPUrl;

@property (weak, nonatomic) IBOutlet UISwitch *switchFirstView;
@property (weak, nonatomic) IBOutlet UISwitch *switchSecondView;
@property (weak, nonatomic) IBOutlet UILabel *plugFlowLabel;

@end

@implementation MobileLiveViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    if ([UIScreen mainScreen].bounds.size.height <= 480) {
        for (NSLayoutConstraint *cons in [self.viewWrap.superview constraints]) {
            if ([cons.identifier isEqualToString:@"consWrapTopToSuper"]) {
                cons.constant = 10;
                break;
            }
        }
        for (NSLayoutConstraint *cont in [self.plugFlowLabel.superview constraints]) {
            if ([cont.identifier isEqualToString:@"tuiLiuYuMingToSuper"]) {
                cont.constant = 10;
                break;
            }
        }
    }
    
    CGColorRef whiteColor = [UIColor whiteColor].CGColor;
    
    self.textAppKey.layer.borderColor = whiteColor;
    self.textStreamName.layer.borderColor = whiteColor;
    self.textDomainName.layer.borderColor = whiteColor;
    self.textRTMPUrl.layer.borderColor = whiteColor;
    
    NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
    NSString *streamName = [ud valueForKey:@"streamName"];
    if (!streamName) {
        //默认初始化一个基于时间戳的流名称
        streamName = [NSString stringWithFormat:@"iOSDemo%.0f", ([NSDate date].timeIntervalSince1970 * 1000)];
    }
    self.textStreamName.text = streamName;
    [ud setValue:streamName forKey:@"streamName"];
    [ud synchronize];
    
    
#define USEUD 1
#ifdef USEUD
    NSString *appKey = [ud valueForKey:@"appKey"], *domainName = [ud valueForKey:@"domainName"];
    if (appKey) {
        self.textAppKey.text = appKey;
    }
    if (domainName) {
        self.textDomainName.text = domainName;
    }
#endif
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:YES];
}
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)segmentValueChanged:(id)sender {
    
    NSInteger i = [(UISegmentedControl *)sender selectedSegmentIndex];
    if (self.isScroll == 1) {
        [self.scrollView setContentOffset:CGPointMake(2 * widthScreen, 0)];
    }else
    {
        [UIView animateWithDuration:0.3 animations:^{
            [self.scrollView setContentOffset:CGPointMake(i * widthScreen, 0)];
        }];
    }
}

- (IBAction)onScrollViewTapped:(id)sender {
    
    [self.textAppKey resignFirstResponder];
    [self.textStreamName resignFirstResponder];
    [self.textDomainName resignFirstResponder];
    [self.textRTMPUrl resignFirstResponder];
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

#pragma mark - util methods
/*
 推流url规则：
 rtmp://推流域名/live/流名称?tm=yyyyMMddHHmmss&sign=xxx
 播放sign规则 ：
 sign参数=MD5(流名称+ tm参数 + 安全)
 其中流名称可以是任意数字、字母的组合
 示例rtmp://400438.mpush.live.lecloud.com/live/mytest1?tm=20160406154640&sign=c445f98bed147e4463185efa4a639978
 
 播放url规则：
 rtmp://播放域名/live/流名称?tm=yyyyMMddHHmmss&sign=xxx
 播放sign规则 ：
 sign参数=MD5(流名称+ tm参数 + 安全码 + “lecloud”)
 其中流名称可以是任意数字、字母的组合
 示例：rtmp://400438.mpull.live.lecloud.com/live/mytest1?tm=20160406154640&sign=7922d30aefbe2740c55bc6b032736208
 */
- (NSString *)rtmpAddressWithDomain:(NSString *)domain streamName:(NSString *)stream appKey:(NSString *)appKey {
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyyMMddHHmmss"];
    NSString *currentDateStr = [dateFormatter stringFromDate:[NSDate date]];
    
    NSString *sign = [self md5:[NSString stringWithFormat:@"%@%@%@", stream, currentDateStr, appKey]];
    NSString *ret = [NSString stringWithFormat:@"rtmp://%@/live/%@?&tm=%@&sign=%@", domain, stream, currentDateStr, sign];
    
    return ret;
}

- (NSString *)md5:(NSString *)str {
    const char *cStr = [str UTF8String];
    unsigned char result[16];
    CC_MD5(cStr, strlen(cStr), result); // This is the md5 call
    return [NSString stringWithFormat:
            @"%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x",
            result[0], result[1], result[2], result[3],
            result[4], result[5], result[6], result[7],
            result[8], result[9], result[10], result[11],
            result[12], result[13], result[14], result[15]
            ];
}

#pragma mark - first view actions
- (IBAction)onGenerateURLClicked:(id)sender {
    [UIView animateWithDuration:0.5 animations:^{
        if (self.viewHiddenPanel.hidden) {
            self.viewHiddenPanel.hidden = NO;
            for (NSLayoutConstraint *cons in [self.btnStartLiveByGeneratedURL.superview constraints]) {
                if ([cons.identifier isEqualToString:@"consBtnStartLiveTopToGenerate"]) {
                    cons.constant = 55;
                    break;
                }
            }
        }
        
        NSString *pullDomain = [self.textDomainName.text stringByReplacingOccurrencesOfString:@"push" withString:@"pull"];
        self.labelGeneratedUrl.text = [self rtmpAddressWithDomain:[pullDomain stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] streamName:[self.textStreamName.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] appKey:[NSString stringWithFormat:@"%@lecloud", [self.textAppKey.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]]]];
    }];
}

- (IBAction)onCopyGeneratedURLClicked:(id)sender {
    UIPasteboard *pb = [UIPasteboard generalPasteboard];
    pb.string = self.labelGeneratedUrl.text;
    
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"复制成功" message:nil delegate:self cancelButtonTitle:@"知道了" otherButtonTitles:nil];
    [alert show];
}

- (IBAction)onStartVodByGenerateRtmpClicked:(id)sender {
    if (self.textDomainName.text.length != 0 && self.textStreamName.text.length != 0) {
        NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
        [ud setValue:self.textAppKey.text forKey:@"appKey"];
        [ud setValue:self.textDomainName.text forKey:@"domainName"];
        [ud synchronize];
        
        NSString *rtmpURL = [self rtmpAddressWithDomain:[self.textDomainName.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] streamName:[self.textStreamName.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] appKey:[self.textAppKey.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]]];
        CaptureStreamingViewControllerOrientation orientation;
        if (self.switchFirstView.on) {
            orientation = CaptureStreamingViewControllerOrientationLandscape;
        }
        else {
            orientation = CaptureStreamingViewControllerOrientationPortrait;
        }
        
        //        rtmpURL = @"rtmp://ossrs.net:1935/live...vhost...players/demo.1471412850523";
        
        CaptureStreamingViewController *viewController = [[CaptureStreamingViewController alloc] initWithRTMPURL:rtmpURL title:self.textStreamName.text orientation:orientation];
        NSLog(@"push rtmp url = %@",rtmpURL);
        //由于iPhone4/4s性能问题，使用较低分辨率
        CGSize screenSize = [[UIScreen mainScreen] bounds].size;
        if (CGSizeEqualToSize(screenSize, CGSizeMake(320, 480)) || CGSizeEqualToSize(screenSize, CGSizeMake(480, 320))) {
            viewController.preset = CaptureStreamingViewControllerPreset320x240;
        }
        [self presentViewController:viewController animated:YES completion:nil];
    }else
    {
        return;
    }
}


#pragma mark - second view actions
- (IBAction)onStartWithRtmpAddressClicked:(id)sender {
    if (self.textRTMPUrl.text.length != 0) {
        CaptureStreamingViewControllerOrientation orientation;
        if (self.switchSecondView.on) {
            orientation = CaptureStreamingViewControllerOrientationLandscape;
        }
        else {
            orientation = CaptureStreamingViewControllerOrientationPortrait;
        }
        CaptureStreamingViewController * viewController = [[CaptureStreamingViewController alloc] initWithRTMPURL:[self.textRTMPUrl.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] title:nil orientation:orientation];
        [[LCStreamingManager sharedManager] enableAutoUploadLog:YES];
        
        NSLog(@"push rtmp url = %@",self.textRTMPUrl.text);
        viewController.bitRate = 1024 * 1024;
        viewController.frameRate = 24;
        //由于iPhone4/4s性能问题，使用较低分辨率
        CGSize screenSize = [[UIScreen mainScreen] bounds].size;
        if (CGSizeEqualToSize(screenSize, CGSizeMake(320, 480)) || CGSizeEqualToSize(screenSize, CGSizeMake(480, 320))) {
            viewController.preset = CaptureStreamingViewControllerPreset320x240;
        }
        [self presentViewController:viewController animated:YES completion:nil];
    }else
    {
        return;
    }
}

@end

