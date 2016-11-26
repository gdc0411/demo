//
//  RCTVideoPlayerViewController.h
//  RCTVideo
//
//  Created by Stanisław Chmiela on 31.03.2016.
//  Copyright © 2016 Facebook. All rights reserved.
//

#import <AVKit/AVKit.h>
#import "RCTLeVideo.h"
#import "RCTLeVideoPlayerViewControllerDelegate.h"

@interface RCTLeVideoPlayerViewController : AVPlayerViewController
@property (nonatomic, weak) id<RCTLeVideoPlayerViewControllerDelegate> rctDelegate;
@end
