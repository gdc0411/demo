//
//  VolumeModule.m
//  LeDemo
//
//  Created by RaoJia on 2016/12/13.
//  Copyright © 2016年 LeCloud. All rights reserved.
//

#import "VolumeModule.h"

#import <MediaPlayer/MediaPlayer.h>
#import <AVFoundation/AVFoundation.h>

@implementation VolumeModule

+ (float) getVolumeValue
{
    
//    MPVolumeView *slide = [MPVolumeView new];
//    UISlider *volumeViewSlider;
//    
//    for (UIView *view in [slide subviews]){
//        if ([[[view class] description] isEqualToString:@"MPVolumeSlider"]){
//            volumeViewSlider = (UISlider *) view;
//        }
//    }
//    
//    // retrieve system volume
//    float systemVolume = volumeViewSlider.value;

    float systemVolume = [[MPMusicPlayerController applicationMusicPlayer] volume];
    return systemVolume;
}

+ (void) setVolumeValue:(float)volume{
    
//    MPVolumeView *slide = [MPVolumeView new];
//    UISlider *volumeViewSlider;
//    
//    for (UIView *view in [slide subviews]){
//        if ([[[view class] description] isEqualToString:@"MPVolumeSlider"]){
//            volumeViewSlider = (UISlider *) view;
//        }
//    }
//    
//    // change system volume, the value is between 0.0f and 1.0f
//    [volumeViewSlider setValue:volume animated:NO];
//    
//    // send UI control event to make the change effect right now.
//    [volumeViewSlider sendActionsForControlEvents:UIControlEventTouchUpInside];
    
    [[MPMusicPlayerController applicationMusicPlayer] setVolume:volume];
}

@end
