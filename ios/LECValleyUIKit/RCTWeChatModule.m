//
//  RCTWeChatModule.m
//  LECValleyUIKit
//
//  Created by RaoJia on 2016/12/22.
//  Copyright © 2016年 leCloud. All rights reserved.
//

#import "LECValley.h"
#import "RCTWeChatModule.h"

#import "WXApi.h"
#import "WXApiObject.h"

#import "../Libraries/Image/RCTImageLoader.h"

#import "RCTLog.h"
#import "RCTBridge.h"
#import "RCTEventDispatcher.h"


@interface RCTWeChatModule()<WXApiDelegate>

@end

static NSString *gAppID = @"";
static NSString *gSecret = @"";
static BOOL gIsApiRegistered = false;


@implementation RCTWeChatModule

@synthesize bridge = _bridge;

RCT_EXPORT_MODULE()

- (NSDictionary *)constantsToExport
{
    return @{ @"isAppRegistered"        : @(gIsApiRegistered),
              @"APP_ID"                 : gAppID,
              @"APP_SECRET"             : gSecret,
              @"SHARE_TYPE_NEWS"        : SHARE_TYPE_NEWS,
              @"SHARE_TYPE_IMAGE"       : SHARE_TYPE_IMAGE,
              @"SHARE_TYPE_IMAGE_FILE"  : SHARE_TYPE_IMAGE_FILE,
              @"SHARE_TYPE_TEXT"        : SHARE_TYPE_TEXT,
              @"SHARE_TYPE_VIDEO"       : SHARE_TYPE_VIDEO,
              @"SHARE_TYPE_AUDIO"       : SHARE_TYPE_AUDIO,
              @"SHARE_TYPE_FILE"        : SHARE_TYPE_FILE};
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

- (instancetype)init
{
    self = [super init];
    if (self) {
        [self _autoRegisterAPI];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleOpenURL:) name:@"RCTOpenURLNotification" object:nil];
    }
    return self;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

//RCT_EXPORT_METHOD(getWXAppInstallUrl:(RCTResponseSenderBlock)callback)
//{
//    callback(@[[NSNull null], [WXApi getWXAppInstallUrl]]);
//}

RCT_EXPORT_METHOD(getApiVersion:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    if( !gIsApiRegistered){
        reject(CODE_NOT_REGISTERED, NOT_REGISTERED, nil);
        return;
    }
    resolve([WXApi getApiVersion]);
}

RCT_EXPORT_METHOD(isWXAppInstalled:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    if( !gIsApiRegistered ){
        reject(CODE_NOT_REGISTERED, NOT_REGISTERED,nil);
        return;
    }
    resolve(@([WXApi isWXAppInstalled]));
}

RCT_EXPORT_METHOD(isWXAppSupportApi:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    if( !gIsApiRegistered ){
        reject(CODE_NOT_REGISTERED, NOT_REGISTERED,nil);
        return;
    }
    resolve(@([WXApi isWXAppSupportApi]));
}


RCT_EXPORT_METHOD(openWXApp:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    if( !gIsApiRegistered ){
        reject(CODE_NOT_REGISTERED, NOT_REGISTERED,nil);
        return;
    }
    
    BOOL success = [WXApi openWXApp];
    if (success) {
        resolve(@[[NSNull null]]);
    }else {
        reject(CODE_INVOKE_FAILED,INVOKE_FAILED,nil);
    }
}


RCT_EXPORT_METHOD(sendAuth:(NSDictionary *)config
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    SendAuthReq* req = [[SendAuthReq alloc] init];
    req.scope = config[@"scope"];
    req.state = config[@"state"]?:[NSDate date].description;
    
    BOOL success = [WXApi sendReq:req];
    if (success) {
        resolve(@[[NSNull null]]);
    }else {
        reject(CODE_INVOKE_FAILED,INVOKE_FAILED,nil);
    }
}

RCT_EXPORT_METHOD(shareToSession:(NSDictionary *)data
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    [self shareToWeixinWithData:data
                          scene:WXSceneSession
                        resolve:(RCTPromiseResolveBlock)resolve
                         reject:(RCTPromiseRejectBlock)reject];
}

RCT_EXPORT_METHOD(shareToTimeline:(NSDictionary *)data
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    [self shareToWeixinWithData:data
                          scene:WXSceneTimeline
                        resolve:(RCTPromiseResolveBlock)resolve
                         reject:(RCTPromiseRejectBlock)reject];
}

RCT_EXPORT_METHOD(pay:(NSDictionary *)data
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    PayReq* req             = [PayReq new];
    req.partnerId           = data[@"partnerId"];
    req.prepayId            = data[@"prepayId"];
    req.nonceStr            = data[@"nonceStr"];
    req.timeStamp           = [data[@"timeStamp"] unsignedIntValue];
    req.package             = data[@"package"];
    req.sign                = data[@"sign"];
    BOOL success = [WXApi sendReq:req];
    if (success) {
        resolve(@[[NSNull null]]);
    }else {
        reject(CODE_INVOKE_FAILED,INVOKE_FAILED,nil);
    }
}

- (void)handleOpenURL:(NSNotification *)note
{
    NSDictionary *userInfo = note.userInfo;
    NSString *url = userInfo[@"url"];
    [WXApi handleOpenURL:[NSURL URLWithString:url] delegate:self];
}

- (void)shareToWeixinWithData:(NSDictionary *)aData
                   thumbImage:(UIImage *)aThumbImage
                        scene:(int)aScene
                      resolve:(RCTPromiseResolveBlock)resolve
                       reject:(RCTPromiseRejectBlock)reject
{
    NSString *type = aData[SHARE_PROP_TYPE];
    
    if ([type isEqualToString:SHARE_TYPE_TEXT]) {
        NSString *text = aData[SHARE_PROP_DESP];
        [self shareToWeixinWithTextMessage:aScene
                                      Text:text
                                   resolve:resolve
                                    reject:reject];
    } else {
        NSString * title = aData[SHARE_PROP_TITLE];
        NSString * description = aData[SHARE_PROP_DESP];
        NSString * mediaTagName = aData[@"mediaTagName"];
        NSString * messageAction = aData[@"messageAction"];
        NSString * messageExt = aData[@"messageExt"];
        
        if (type.length <= 0 || [type isEqualToString:SHARE_TYPE_NEWS]) {
            NSString * webpageUrl = aData[SHARE_PROP_TARGET];
            if (webpageUrl.length <= 0) {
                reject(CODE_INVALID_ARGUMENT,@[@"webpageUrl required"],nil);
                return;
            }
            
            WXWebpageObject* webpageObject = [WXWebpageObject object];
            webpageObject.webpageUrl = webpageUrl;
            
            [self shareToWeixinWithMediaMessage:aScene
                                          Title:title
                                    Description:description
                                         Object:webpageObject
                                     MessageExt:messageExt
                                  MessageAction:messageAction
                                     ThumbImage:aThumbImage
                                       MediaTag:mediaTagName
                                        resolve:resolve
                                         reject:reject];
            
        } else if ([type isEqualToString:SHARE_TYPE_AUDIO]) {
            WXMusicObject *musicObject = [WXMusicObject new];
            musicObject.musicUrl = aData[SHARE_PROP_AUDIO];
            musicObject.musicLowBandUrl = aData[@"musicLowBandUrl"];
            musicObject.musicDataUrl = aData[@"musicDataUrl"];
            musicObject.musicLowBandDataUrl = aData[@"musicLowBandDataUrl"];
            
            [self shareToWeixinWithMediaMessage:aScene
                                          Title:title
                                    Description:description
                                         Object:musicObject
                                     MessageExt:messageExt
                                  MessageAction:messageAction
                                     ThumbImage:aThumbImage
                                       MediaTag:mediaTagName
                                        resolve:resolve
                                         reject:reject];
            
        } else if ([type isEqualToString:SHARE_TYPE_VIDEO]) {
            WXVideoObject *videoObject = [WXVideoObject new];
            videoObject.videoUrl = aData[SHARE_PROP_VIDEO];
            videoObject.videoLowBandUrl = aData[@"videoLowBandUrl"];
            
            [self shareToWeixinWithMediaMessage:aScene
                                          Title:title
                                    Description:description
                                         Object:videoObject
                                     MessageExt:messageExt
                                  MessageAction:messageAction
                                     ThumbImage:aThumbImage
                                       MediaTag:mediaTagName
                                        resolve:resolve
                                         reject:reject];
            
        } else if ([type isEqualToString:SHARE_TYPE_IMAGE]) {
            NSURL *url = [NSURL URLWithString:aData[SHARE_PROP_IMAGE]];
            NSURLRequest *imageRequest = [NSURLRequest requestWithURL:url];
            [self.bridge.imageLoader loadImageWithURLRequest:imageRequest callback:^(NSError *error, UIImage *image) {
                if (image == nil){
                    reject(CODE_INVALID_ARGUMENT,@[@"fail to load image resource"],nil);
                } else {
                    WXImageObject *imageObject = [WXImageObject object];
                    imageObject.imageData = UIImagePNGRepresentation(image);
                    
                    [self shareToWeixinWithMediaMessage:aScene
                                                  Title:title
                                            Description:description
                                                 Object:imageObject
                                             MessageExt:messageExt
                                          MessageAction:messageAction
                                             ThumbImage:aThumbImage
                                               MediaTag:mediaTagName
                                                resolve:resolve
                                                 reject:reject];
                    
                }
            }];
        } else if ([type isEqualToString:SHARE_TYPE_IMAGE_FILE]) {
            NSString * filePath = aData[@"filePath"];
            NSString * fileExtension = aData[@"fileExtension"];
            
            WXFileObject *fileObject = [WXFileObject object];
            fileObject.fileData = [NSData dataWithContentsOfFile:filePath];
            fileObject.fileExtension = fileExtension;
            
            [self shareToWeixinWithMediaMessage:aScene
                                          Title:title
                                    Description:description
                                         Object:fileObject
                                     MessageExt:messageExt
                                  MessageAction:messageAction
                                     ThumbImage:aThumbImage
                                       MediaTag:mediaTagName
                                        resolve:resolve
                                         reject:reject];
            
        } else {
            reject(CODE_INVALID_ARGUMENT,INVALID_ARGUMENT, nil);
        }
    }
}

- (void)shareToWeixinWithData:(NSDictionary *)aData
                        scene:(int)aScene
                      resolve:(RCTPromiseResolveBlock)resolve
                       reject:(RCTPromiseRejectBlock)reject
{
    NSString *imageUrl = aData[SHARE_PROP_THUMB_IMAGE];
    if (imageUrl.length && _bridge.imageLoader) {
        NSURL *url = [NSURL URLWithString:imageUrl];
        NSURLRequest *imageRequest = [NSURLRequest requestWithURL:url];
        [_bridge.imageLoader loadImageWithURLRequest:imageRequest size:CGSizeMake(100, 100) scale:1 clipped:FALSE resizeMode:RCTResizeModeStretch progressBlock:nil partialLoadBlock:nil
                                     completionBlock:^(NSError *error, UIImage *image) {
                                         [self shareToWeixinWithData:aData
                                                          thumbImage:image
                                                               scene:aScene
                                                             resolve:resolve
                                                              reject:reject];
                                     }];
    } else {
        [self shareToWeixinWithData:aData
                         thumbImage:nil
                              scene:aScene
                            resolve:resolve
                             reject: reject];
    }
    
}

- (void)shareToWeixinWithTextMessage:(int)aScene
                                Text:(NSString *)text
                             resolve:(RCTPromiseResolveBlock)resolve
                              reject:(RCTPromiseRejectBlock)reject
{
    SendMessageToWXReq* req = [SendMessageToWXReq new];
    req.bText = YES;
    req.scene = aScene;
    req.text = text;
    
    BOOL success = [WXApi sendReq:req];
    if(success)
        resolve([NSNull null]);
    else
        reject(CODE_INVOKE_FAILED,INVOKE_FAILED, nil);
}

- (void)shareToWeixinWithMediaMessage:(int)aScene
                                Title:(NSString *)title
                          Description:(NSString *)description
                               Object:(id)mediaObject
                           MessageExt:(NSString *)messageExt
                        MessageAction:(NSString *)action
                           ThumbImage:(UIImage *)thumbImage
                             MediaTag:(NSString *)tagName
                              resolve:(RCTPromiseResolveBlock)resolve
                               reject:(RCTPromiseRejectBlock)reject
{
    WXMediaMessage *message = [WXMediaMessage message];
    message.title           = title;
    message.description     = description;
    message.mediaObject     = mediaObject;
    message.messageExt      = messageExt;
    message.messageAction   = action;
    message.mediaTagName    = tagName;
    [message setThumbImage:thumbImage];
    
    SendMessageToWXReq* req = [SendMessageToWXReq new];
    req.bText = NO;
    req.scene = aScene;
    req.message = message;
    
    BOOL success = [WXApi sendReq:req];
    if(success)
        resolve([NSNull null]);
    else
        reject(CODE_INVOKE_FAILED,INVOKE_FAILED, nil);
}

#pragma mark - wx callback

-(void) onReq:(BaseReq*)req
{
    // TODO(Yorkie)
}

-(void) onResp:(BaseResp*)resp
{
    NSMutableDictionary *body = @{EVENT_PROP_SOCIAL_CODE:@(resp.errCode)}.mutableCopy;
    body[@"wxCode"] = @(resp.errCode);
    body[@"wxStr"] = resp.errStr;
    
    if([resp isKindOfClass:[SendMessageToWXResp class]]){
        SendMessageToWXResp *r = (SendMessageToWXResp *)resp;
        body[@"lang"]    = r.lang;
        body[@"country"] = r.country;
        body[EVENT_PROP_SOCIAL_TYPE]    = @"SendMessageToWX.Resp";
        
        if (resp.errCode == WXSuccess) {
            body[EVENT_PROP_SOCIAL_CODE]  = @(AUTH_RESULT_CODE_SUCCESSFUL);
            body[EVENT_PROP_SOCIAL_MSG]   = AUTH_RESULT_MSG_SUCCESSFUL;
        } else if (resp.errCode == WXErrCodeUserCancel || resp.errCode == WXErrCodeAuthDeny) {
            body[EVENT_PROP_SOCIAL_CODE]  = @(AUTH_RESULT_CODE_CANCEL);
            body[EVENT_PROP_SOCIAL_MSG]   = AUTH_RESULT_MSG_CANCEL;
        } else {
            body[EVENT_PROP_SOCIAL_CODE]  = @(AUTH_RESULT_CODE_FAILED);
            body[EVENT_PROP_SOCIAL_MSG]   = AUTH_RESULT_MSG_FAILED;
        }
    }
    else if ([resp isKindOfClass:[SendAuthResp class]]) {
        SendAuthResp *r  = (SendAuthResp *)resp;
        body[@"state"]   = r.state;
        body[@"lang"]    = r.lang;
        body[@"country"] = r.country;
        body[EVENT_PROP_SOCIAL_TYPE]    = @"SendAuth.Resp";
        body[EVENT_PROP_SOCIAL_CODE]    = r.code;
        
        if (resp.errCode == WXSuccess) {
            body[EVENT_PROP_SOCIAL_CODE]  = @(SHARE_RESULT_CODE_SUCCESSFUL);
            body[EVENT_PROP_SOCIAL_MSG]   = SHARE_RESULT_MSG_SUCCESSFUL;
        } else if (resp.errCode == WXErrCodeUserCancel || resp.errCode == WXErrCodeAuthDeny) {
            body[EVENT_PROP_SOCIAL_CODE]  = @(SHARE_RESULT_CODE_CANCEL);
            body[EVENT_PROP_SOCIAL_MSG]   = SHARE_RESULT_MSG_CANCEL;
        } else {
            body[EVENT_PROP_SOCIAL_CODE]  = @(SHARE_RESULT_CODE_FAILED);
            body[EVENT_PROP_SOCIAL_MSG]   = SHARE_RESULT_MSG_FAILED;
        }
    }
    else if([resp isKindOfClass:[PayResp class]]) {
        PayResp *r          = (PayResp *)resp;
        body[@"returnKey"]  = r.returnKey;
        body[EVENT_PROP_SOCIAL_TYPE]       = @"Pay.Resp";
    }
    
    [self.bridge.eventDispatcher sendAppEventWithName:EVENT_WECHAT_RESP
                                                 body:body];
    
}


- (void)_autoRegisterAPI
{
    if (gAppID.length > 0 && gIsApiRegistered) {
        return;
    }
    
    NSArray *list = [[[NSBundle mainBundle] infoDictionary] valueForKey:@"CFBundleURLTypes"];
    for (NSDictionary *item in list) {
        NSString *name = item[@"CFBundleURLName"];
        if ([name isEqualToString:@"wx_appid"]) {
            NSArray *schemes = item[@"CFBundleURLSchemes"];
            if (schemes.count > 0){
                gAppID = schemes[0];
                if(![gAppID isEqualToString:@""] && ![gSecret isEqualToString:@""]) break;
            }
        }else if ([name isEqualToString:@"wx_secret"]) {
            NSArray *schemes = item[@"CFBundleURLSchemes"];
            if (schemes.count > 0){
                gSecret = schemes[0];
                if(![gAppID isEqualToString:@""] && ![gSecret isEqualToString:@""]) break;
            }
        }
    }
    gIsApiRegistered = [WXApi registerApp:gAppID];
}


- (NSString *)_getErrorMsg:(int)code {
    switch (code) {
        case WXSuccess:
            return @"成功";
        case WXErrCodeCommon:
            return @"普通错误类型";
        case WXErrCodeUserCancel:
            return @"授权失败，用户取消";
        case WXErrCodeSentFail:
            return @"发送失败";
        case WXErrCodeAuthDeny:
            return @"授权失败，用户拒绝";
        case WXErrCodeUnsupport:
            return @"微信不支持";
        default:
            return @"失败";
    }
}


@end
