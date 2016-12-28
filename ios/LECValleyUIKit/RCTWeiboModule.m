//
//  RCTWeiboModule.m
//  LECValleyUIKit
//
//  Created by RaoJia on 2016/12/27.
//  Copyright © 2016年 leCloud. All rights reserved.
//

#import "LECValley.h"

#import "RCTWeiboModule.h"
#import "WeiboSDK.h"
#import "RCTBridge.h"
#import "RCTEventDispatcher.h"

#import "../Libraries/Image/RCTImageLoader.h"


BOOL gRegister = NO;

@interface RCTWeiboModule()<WeiboSDKDelegate>

@end


@implementation RCTWeiboModule

@synthesize bridge = _bridge;

RCT_EXPORT_MODULE();

- (NSDictionary *)constantsToExport
{
    return @{ @"SHARE_TYPE_NEWS"        : SHARE_TYPE_NEWS,
              @"SHARE_TYPE_IMAGE"       : SHARE_TYPE_IMAGE,
              @"SHARE_TYPE_TEXT"        : SHARE_TYPE_TEXT,
              @"SHARE_TYPE_VIDEO"       : SHARE_TYPE_VIDEO,
              @"SHARE_TYPE_AUDIO"       : SHARE_TYPE_AUDIO,
              @"SHARE_TYPE_VOICE"       : SHARE_TYPE_VOICE};
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

- (instancetype)init
{
    self = [super init];
    if (self) {
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(handleOpenURL:)
                                                     name:@"RCTOpenURLNotification"
                                                   object:nil];        
        [self _autoRegisterAPI];
    }
    return self;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

RCT_EXPORT_METHOD(getApiVersion:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    if( !gRegister ){
        reject(CODE_NOT_REGISTERED, NOT_REGISTERED,nil);
        return;
    }
    resolve( [WeiboSDK getSDKVersion]);
}

RCT_EXPORT_METHOD(isAppInstalled:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    if( !gRegister ){
        reject(CODE_NOT_REGISTERED, NOT_REGISTERED,nil);
        return;
    }
    resolve( @([WeiboSDK isWeiboAppInstalled]));
}

RCT_EXPORT_METHOD(isAppSupportApi:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    if( !gRegister ){
        reject(CODE_NOT_REGISTERED, NOT_REGISTERED,nil);
        return;
    }
    resolve( @([WeiboSDK isCanSSOInWeiboApp]));
}


RCT_EXPORT_METHOD(login:(NSDictionary *)config
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    if( !gRegister ){
        reject(CODE_NOT_REGISTERED, NOT_REGISTERED,nil);
        return;
    }
    WBAuthorizeRequest *request = [self _genAuthRequest:config];
    BOOL success = [WeiboSDK sendRequest:request];
    
    if (success) {
        resolve(@[[NSNull null]]);
    }else {
        reject(CODE_INVOKE_FAILED,INVOKE_FAILED,nil);
    }
}

RCT_EXPORT_METHOD(logout)
{
    [WeiboSDK logOutWithToken:nil delegate:nil withTag:nil];
}

RCT_EXPORT_METHOD(shareToWeibo:(NSDictionary *)aData
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    if( !gRegister ){
        reject(CODE_NOT_REGISTERED, NOT_REGISTERED,nil);
        return;
    }
    
    NSString *imageUrl = aData[SHARE_PROP_THUMB_IMAGE];
    if (imageUrl.length && _bridge.imageLoader) {
        CGSize size = CGSizeZero;
        if (![aData[SHARE_PROP_TYPE] isEqualToString:SHARE_TYPE_IMAGE]) {
            size = CGSizeMake(80,80);
        }
        [_bridge.imageLoader loadImageWithURLRequest:[RCTConvert NSURLRequest:imageUrl]
                                                size:size scale:1
                                             clipped:FALSE
                                          resizeMode:UIViewContentModeScaleToFill
                                       progressBlock:nil
                                    partialLoadBlock:nil
                                     completionBlock:^(NSError *error, UIImage *image) {
                                         
            [self _shareWithData:aData image:image];
        }];
        
    }else {
        [self _shareWithData:aData image:nil];
    }
    resolve(@[[NSNull null]]);
}


- (void)handleOpenURL:(NSNotification *)note
{
    NSDictionary *userInfo = note.userInfo;
    NSString *url = userInfo[@"url"];
    [WeiboSDK handleOpenURL:[NSURL URLWithString:url]
                   delegate:self];
}


#pragma mark - sina delegate
- (void)didReceiveWeiboRequest:(WBBaseRequest *)request
{
    if ([request isKindOfClass:WBProvideMessageForWeiboRequest.class])
    {
        
    }
}

- (void)didReceiveWeiboResponse:(WBBaseResponse *)response
{
    NSMutableDictionary *body = [NSMutableDictionary new];
    body[@"wbCode"] = @(response.statusCode);
    body[@"wbMsg"] = [self _getErrMsg:response.statusCode];
    // 分享
    if ([response isKindOfClass:WBSendMessageToWeiboResponse.class]){
        
        body[SHARE_PROP_TYPE] = @"WBSendMessageToWeiboResponse";
        if (response.statusCode == WeiboSDKResponseStatusCodeSuccess){
            WBSendMessageToWeiboResponse *sendResponse = (WBSendMessageToWeiboResponse *)response;
            WBAuthorizeResponse *authorizeResponse = sendResponse.authResponse;
            if (sendResponse.authResponse != nil) {
                body[@"openid"]         = authorizeResponse.userID;
                body[@"access_token"]   = authorizeResponse.accessToken;
                body[@"expires_in"]     = @([authorizeResponse.expirationDate timeIntervalSince1970]*1000);
                body[@"refresh_token"]  = authorizeResponse.refreshToken;
            }            
            body[EVENT_PROP_SOCIAL_CODE] = @(SHARE_RESULT_CODE_SUCCESSFUL);
            body[EVENT_PROP_SOCIAL_MSG]  = SHARE_RESULT_MSG_SUCCESSFUL;
            
        } else if( response.statusCode == WeiboSDKResponseStatusCodeUserCancel
                  || response.statusCode == WeiboSDKResponseStatusCodeUserCancel
                  || response.statusCode == WeiboSDKResponseStatusCodeAuthDeny ){
            body[EVENT_PROP_SOCIAL_CODE] = @(SHARE_RESULT_CODE_CANCEL);
            body[EVENT_PROP_SOCIAL_MSG]  = SHARE_RESULT_MSG_CANCEL;
        }else {
            body[EVENT_PROP_SOCIAL_CODE] = @(SHARE_RESULT_CODE_FAILED);
            body[EVENT_PROP_SOCIAL_MSG]  = SHARE_RESULT_MSG_FAILED;
        }
        
    } else if ([response isKindOfClass:WBAuthorizeResponse.class]){ // 认证
        
        body[SHARE_PROP_TYPE] = @"WBAuthorizeResponse";
        if (response.statusCode == WeiboSDKResponseStatusCodeSuccess){
            WBAuthorizeResponse *authorizeResponse = (WBAuthorizeResponse *)response;
            body[@"openid"]         = authorizeResponse.userID;
            body[@"access_token"]   = authorizeResponse.accessToken;
            body[@"expires_in"]     = @([authorizeResponse.expirationDate timeIntervalSince1970]*1000);
            body[@"refresh_token"]  = authorizeResponse.refreshToken;
            
            body[EVENT_PROP_SOCIAL_CODE] = @(SHARE_RESULT_CODE_SUCCESSFUL);
            body[EVENT_PROP_SOCIAL_MSG]  = SHARE_RESULT_MSG_SUCCESSFUL;

        } else if( response.statusCode == WeiboSDKResponseStatusCodeUserCancel
                  || response.statusCode == WeiboSDKResponseStatusCodeUserCancel
                  || response.statusCode == WeiboSDKResponseStatusCodeAuthDeny ){
            
            body[EVENT_PROP_SOCIAL_CODE] = @(SHARE_RESULT_CODE_CANCEL);
            body[EVENT_PROP_SOCIAL_MSG]  = SHARE_RESULT_MSG_CANCEL;
        }else{
            body[EVENT_PROP_SOCIAL_CODE] = @(SHARE_RESULT_CODE_FAILED);
            body[EVENT_PROP_SOCIAL_MSG]  = SHARE_RESULT_MSG_FAILED;
        }
//        } else {
//            body[EVENT_PROP_SOCIAL_MSG] = [self _getErrMsg:response.statusCode];
//        }
    }
    [self.bridge.eventDispatcher sendAppEventWithName:EVENT_WEIBO_RESP
                                                 body:body];
}

#pragma mark - private

// 如果js没有调用registerApp，自动从plist中读取appId
- (void)_autoRegisterAPI
{
    if (gRegister) {
        return;
    }
    
    NSArray *list = [[[NSBundle mainBundle] infoDictionary] valueForKey:@"CFBundleURLTypes"];
    for (NSDictionary *item in list) {
        NSString *name = item[@"CFBundleURLName"];
        if ([name isEqualToString:@"sina"]) {
            NSArray *schemes = item[@"CFBundleURLSchemes"];
            if (schemes.count > 0)
            {
                NSString *appId = [schemes[0] substringFromIndex:@"wb".length];
                if ([WeiboSDK registerApp:appId]) {
                    gRegister = YES;
                }
#ifdef DEBUG
                [WeiboSDK enableDebugMode:YES];
#endif
                break;
            }
        }
    }
}

- (NSString *)_getErrMsg:(NSInteger)errCode
{
    NSString *errMsg = @"微博认证失败";
    switch (errCode) {
        case WeiboSDKResponseStatusCodeUserCancel:
            errMsg = @"用户取消发送";
            break;
        case WeiboSDKResponseStatusCodeSentFail:
            errMsg = @"发送失败";
            break;
        case WeiboSDKResponseStatusCodeAuthDeny:
            errMsg = @"授权失败";
            break;
        case WeiboSDKResponseStatusCodeUserCancelInstall:
            errMsg = @"用户取消安装微博客户端";
            break;
        case WeiboSDKResponseStatusCodePayFail:
            errMsg = @"支付失败";
            break;
        case WeiboSDKResponseStatusCodeShareInSDKFailed:
            errMsg = @"分享失败";
            break;
        case WeiboSDKResponseStatusCodeUnsupport:
            errMsg = @"不支持的请求";
            break;
        case WeiboSDKResponseStatusCodeSuccess:
            errMsg = @"成功";
            break;
        default:
            errMsg = @"未知错误";
            break;
    }
    return errMsg;
}

- (void)_shareWithData:(NSDictionary *)aData
                 image:(UIImage *)aImage
{
    WBMessageObject *message = [WBMessageObject message];
    NSString *text = aData[SHARE_PROP_TEXT];
    message.text = text;
    
    if (aData[SHARE_PROP_TEXT]) {
        message.text = aData[SHARE_PROP_TEXT];
    }
    
//    if (aImage) { //设置缩略图 注意：大小不能超过10M
//        WBImageObject *imageObject = [WBImageObject object];
//        imageObject.imageData = UIImageJPEGRepresentation(aImage, 0.7);
//        message.imageObject = imageObject;
//    }
    
    NSString *type = aData[SHARE_PROP_TYPE];
    if ([type isEqualToString:SHARE_TYPE_NEWS]) {
        WBWebpageObject *webpageObject  = [WBWebpageObject object];
        webpageObject.webpageUrl        = aData[SHARE_PROP_TARGET];
        if (aImage) webpageObject.thumbnailData = UIImageJPEGRepresentation(aImage, 0.7);
        message.mediaObject             = webpageObject;
        
    } else if ([type isEqualToString:SHARE_TYPE_VIDEO]) {
        WBVideoObject *videoObject  = [WBVideoObject object];
        videoObject.videoUrl        = aData[SHARE_PROP_VIDEO];
        if (aImage) videoObject.thumbnailData  = UIImageJPEGRepresentation(aImage, 0.7);
        message.mediaObject         = videoObject;
        
    } else if ([type isEqualToString:SHARE_TYPE_IMAGE]) {
        WBImageObject *imageObject  = [WBImageObject object];
        imageObject.imageData       = UIImageJPEGRepresentation(aImage, 0.7);
        message.imageObject         = imageObject;
        
    } else if ([type isEqualToString:SHARE_TYPE_AUDIO]) {
        WBMusicObject *musicObject  = [WBMusicObject object];
        musicObject.musicUrl        = aData[SHARE_PROP_AUDIO];
        if (aImage) musicObject.thumbnailData  = UIImageJPEGRepresentation(aImage, 0.7);
        message.mediaObject         = musicObject;
    }
    
    message.mediaObject.objectID    = [NSDate date].description;
    message.mediaObject.description = aData[SHARE_PROP_DESP];
    message.mediaObject.title       = aData[SHARE_PROP_TITLE];
    

    WBSendMessageToWeiboRequest *request = [WBSendMessageToWeiboRequest requestWithMessage:message];
// 使用新的token提交的情况
//    WBAuthorizeRequest *authRequest = [self _genAuthRequest:aData];
//    NSString *accessToken = @"";//aData[RCTWBShareAccessToken];
//    WBSendMessageToWeiboRequest *request = [WBSendMessageToWeiboRequest requestWithMessage:message
//                                                                                  authInfo:authRequest
//                                                                              access_token:accessToken];
    BOOL success = [WeiboSDK sendRequest:request];
    if (!success) {
        NSMutableDictionary *body       = [NSMutableDictionary new];
        body[EVENT_PROP_SOCIAL_CODE]    = CODE_INVOKE_FAILED;
        body[EVENT_PROP_SOCIAL_MSG]     = INVOKE_FAILED;
        body[EVENT_PROP_SOCIAL_TYPE]    = @"WBSendMessageToWeiboResponse";
        [_bridge.eventDispatcher sendAppEventWithName:EVENT_WEIBO_RESP
                                                 body:body];
    }
}

- (WBAuthorizeRequest *)_genAuthRequest:(NSDictionary *)config
{
    NSString *redirectURI   = config&&config[@"redirectURI"]?config[@"redirectURI"]:@"https://api.weibo.com/oauth2/default.html";
    NSString *scope         = config&&config[@"scope"]?config[@"scope"]:@"all";
    
    WBAuthorizeRequest *authRequest = [WBAuthorizeRequest request];
    authRequest.redirectURI         = redirectURI;
    authRequest.scope               = scope;
    
    return authRequest;
}

@end
