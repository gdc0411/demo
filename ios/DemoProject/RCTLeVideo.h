#import "RCTView.h"
#import <AVFoundation/AVFoundation.h>
#import "AVKit/AVKit.h"
#import "UIView+FindUIViewController.h"
#import "RCTLeVideoPlayerViewController.h"
#import "RCTLeVideoPlayerViewControllerDelegate.h"

@class RCTEventDispatcher;

@interface RCTLeVideo : UIView <RCTLeVideoPlayerViewControllerDelegate>

- (instancetype)initWithEventDispatcher:(RCTEventDispatcher *)eventDispatcher NS_DESIGNATED_INITIALIZER;

- (AVPlayerViewController*)createPlayerViewController:(AVPlayer*)player withPlayerItem:(AVPlayerItem*)playerItem;

@end
