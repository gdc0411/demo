//
//  LECValley.h
//  LECValleyUIKit
//
//  Created by RaoJia on 2016/12/18.
//  Copyright © 2016年 RaoJia. All rights reserved.
//

#ifndef LECValley_h
#define LECValley_h

#define iPhone4s ([UIScreen instancesRespondToSelector:@selector(currentMode)] ? CGSizeEqualToSize(CGSizeMake(640, 960), [[UIScreen mainScreen] currentMode].size) : NO)
// 监听TableView的contentOffset
#define kPlayerViewContentOffset          @"contentOffset"
// player的单例
#define PlayerShared                      [BrightnessView sharedBrightnessView]
// 屏幕的宽
#define ScreenWidth                       [[UIScreen mainScreen] bounds].size.width
// 屏幕的高
#define ScreenHeight                      [[UIScreen mainScreen] bounds].size.height
// 颜色值RGB
#define RGBA(r,g,b,a)                     [UIColor colorWithRed:r/255.0f green:g/255.0f blue:b/255.0f alpha:a]
// 图片路径
#define PlayerSrcName(file)               [@"LECValleyUIResource.bundle" stringByAppendingPathComponent:file]

#define PlayerFrameworkSrcName(file)      [@"Frameworks/Player.framework/LECValleyUIResource.bundle" stringByAppendingPathComponent:file]

#define PlayerImage(file)                 [UIImage imageNamed:PlayerSrcName(file)] ? :[UIImage imageNamed:PlayerFrameworkSrcName(file)]

//第三方登录的共享参数

#define EVENT_WECHAT_RESP   @"WeChat_Resp"
#define EVENT_QQ_RESP       @"QQ_Resp"
#define EVENT_WEIBO_RESP    @"Weibo_Resp"

// 定义分享类型常量
#define SHARE_TYPE_NEWS         @"news"
#define SHARE_TYPE_IMAGE        @"image"
#define SHARE_TYPE_IMAGE_FILE   @"imageFile"
#define SHARE_TYPE_TEXT         @"text"
#define SHARE_TYPE_VIDEO        @"video"
#define SHARE_TYPE_AUDIO        @"audio"
#define SHARE_TYPE_VOICE        @"voice"
#define SHARE_TYPE_FILE         @"file"
#define SHARE_TYPE_APP          @"app"

// 定义分享共性字段名
#define SHARE_PROP_TYPE         @"type"
#define SHARE_PROP_TITLE        @"title"
#define SHARE_PROP_TEXT         @"text"
#define SHARE_PROP_DESP         @"description"
#define SHARE_PROP_TARGET       @"webpageUrl"
#define SHARE_PROP_THUMB_IMAGE  @"thumbImage"
#define SHARE_PROP_IMAGE        @"imageUrl"
#define SHARE_PROP_VIDEO        @"videoUrl"
#define SHARE_PROP_AUDIO        @"audioUrl"
#define SHARE_PROP_APPNAME      @"appName"


//事件字段定义
#define EVENT_PROP_SOCIAL_CODE  @"errCode"
#define EVENT_PROP_SOCIAL_MSG   @"errMsg"
#define EVENT_PROP_SOCIAL_TYPE  @"type"

//事件返回码
#define CODE_NOT_REGISTERED     @"-1"
#define CODE_NULL_ACTIVITY      @"-2"
#define CODE_INVOKE_FAILED      @"-3"
#define CODE_INVALID_ARGUMENT   @"-4"

//授权和分享返回码和消息体
#define AUTH_RESULT_CODE_SUCCESSFUL 0
#define AUTH_RESULT_CODE_FAILED     1
#define AUTH_RESULT_CODE_CANCEL     2

#define AUTH_RESULT_MSG_SUCCESSFUL @"登录成功"
#define AUTH_RESULT_MSG_FAILED     @"授权失败，请稍后重试"
#define AUTH_RESULT_MSG_CANCEL     @"授权失败，用户取消"

#define SHARE_RESULT_CODE_SUCCESSFUL 0
#define SHARE_RESULT_CODE_FAILED     1
#define SHARE_RESULT_CODE_CANCEL     2

#define SHARE_RESULT_MSG_SUCCESSFUL @"分享成功"
#define SHARE_RESULT_MSG_FAILED     @"分享失败，请稍后重试"
#define SHARE_RESULT_MSG_CANCEL     @"分享失败，用户取消"


//事件处理响应
#define NOT_REGISTERED      @"NOT_REGISTERED"
#define INVOKE_FAILED       @"INVOKE_FAILED"
#define INVALID_ARGUMENT    @"INVALID_ARGUMENT"


#endif /* LECValley_h */
