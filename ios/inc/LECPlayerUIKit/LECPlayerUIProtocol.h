//
//  LECPlayerUIProtocol.h
//  LECPlayerUIKit
//
//  Created by CC on 16/4/27.
//  Copyright © 2016年 CC. All rights reserved.
//


#import <UIKit/UIKit.h>
#import "LECPlayerUIDefine.h"
/*
 播放界面上下工具条事件代理协议
 */
@class LECPlayerControl;
@protocol LECPlayerControlDelegate <NSObject>

@optional
/*
 工具条事件回调
 */
- (void)lecPlayerBar:(UIView *)view
              action:(LECPlayerUIActionEvent)action;

- (void)lecPlayerControl:(LECPlayerControl *)playerControl didChangePlayerFullScreenState:(BOOL)fullScreen;

- (void)lecPlayerControlDidClickBackBtn:(LECPlayerControl *)playerControl;


- (void)lecPlayerControlDidCutView:(LECPlayerControl *)playerControl cutImage:(UIImage *)cutImge;
/*
播放Control事件回调
*/
- (void)lecPlayerControl:(LECPlayerControl *)playerControl
              mediaTitle:(NSString *)mediaTitle
         currentPlayTime:(NSTimeInterval)currentPlayTimestamp
               totalTime:(NSTimeInterval)totalTime;

- (void)lecPlayerControl:(LECPlayerControl *)playerControl
             playerEvent:(LECPlayerControlEvent)event
                   error:(NSError *)error;

- (BOOL)lecPlayerControl:(LECPlayerControl *)playerControl
   showCommonHUDMessage:(NSString *) message;
//如果不实现该方法，或返回YES，则调用SDK内部方法展示提示；否则需开发者在该方法的实现中自行展示HUD

- (void)lecPlayerControl:(LECPlayerControl *)playerControl
                 needBuy:(BOOL)needBuy
             tryLookTime:(int)tryLookTime;

@end






