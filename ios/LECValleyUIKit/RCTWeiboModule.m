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

RCT_EXPORT_METHOD(isWBInstalled:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    if( !gRegister ){
        reject(CODE_NOT_REGISTERED, NOT_REGISTERED,nil);
        return;
    }
    resolve( @([WeiboSDK isWeiboAppInstalled]));
}

RCT_EXPORT_METHOD(isWBSupportApi:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    if( !gRegister ){
        reject(CODE_NOT_REGISTERED, NOT_REGISTERED,nil);
        return;
    }
    resolve( @([WeiboSDK isCanSSOInWeiboApp]));
}


RCT_EXPORT_METHOD(login:(NSDictionary *)config:(RCTResponseSenderBlock)callback)
{
    WBAuthorizeRequest *request = [self _genAuthRequest:config];
    BOOL success = [WeiboSDK sendRequest:request];
    callback(@[success?[NSNull null]:INVOKE_FAILED]);
}

RCT_EXPORT_METHOD(logout)
{
    [WeiboSDK logOutWithToken:nil delegate:nil withTag:nil];
}

RCT_EXPORT_METHOD(shareToWeibo:(NSDictionary *)aData:(RCTResponseSenderBlock)callback)
{
    NSString *imageUrl = aData[@"imageUrl"];
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
        
        [self _shareWithData:aData
                       image:nil];
        
    }
    callback(@[[NSNull null]]);
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
    body[EVENT_PROP_SOCIAL_CODE] = @(response.statusCode);
    // 分享
    if ([response isKindOfClass:WBSendMessageToWeiboResponse.class]){
        
        body[SHARE_PROP_TYPE] = @"WBSendMessageToWeiboResponse";
        if (response.statusCode == WeiboSDKResponseStatusCodeSuccess){
            WBSendMessageToWeiboResponse *sendResponse = (WBSendMessageToWeiboResponse *)response;
            WBAuthorizeResponse *authorizeResponse = sendResponse.authResponse;
            if (sendResponse.authResponse != nil) {
                body[@"userID"] = authorizeResponse.userID;
                body[@"accessToken"] = authorizeResponse.accessToken;
                body[@"expirationDate"] = @([authorizeResponse.expirationDate timeIntervalSince1970]);
                body[@"refreshToken"] = authorizeResponse.refreshToken;
            }
        }else{
            body[EVENT_PROP_SOCIAL_MSG] = [self _getErrMsg:response.statusCode];
        }
        
    } else if ([response isKindOfClass:WBAuthorizeResponse.class]){ // 认证
        
        body[SHARE_PROP_TYPE] = @"WBAuthorizeResponse";
        if (response.statusCode == WeiboSDKResponseStatusCodeSuccess){
            WBAuthorizeResponse *authorizeResponse = (WBAuthorizeResponse *)response;
            body[@"userID"] = authorizeResponse.userID;
            body[@"accessToken"] = authorizeResponse.accessToken;
            body[@"expirationDate"] = @([authorizeResponse.expirationDate timeIntervalSince1970]*1000);
            body[@"refreshToken"] = authorizeResponse.refreshToken;
        }
        else
        {
            body[EVENT_PROP_SOCIAL_MSG] = [self _getErrMsg:response.statusCode];
        }
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
        default:
            errMsg = @"位置";
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
    
    NSString *type = aData[SHARE_PROP_TYPE];
    if ([type isEqualToString:SHARE_TYPE_TEXT]) {
    }
    else if ([type isEqualToString:SHARE_TYPE_IMAGE]) {
        //        大小不能超过10M
        WBImageObject *imageObject = [WBImageObject new];
        if (aImage) {
            imageObject.imageData = UIImageJPEGRepresentation(aImage, 0.7);
        }
        message.imageObject = imageObject;
    }
    else {
        if ([type isEqualToString:SHARE_TYPE_VIDEO]) {
            WBVideoObject *videoObject = [WBVideoObject new];
            videoObject.videoUrl = aData[SHARE_PROP_VIDEO];
            message.mediaObject = videoObject;
        }
        else if ([type isEqualToString:SHARE_TYPE_AUDIO]) {
            WBMusicObject *musicObject = [WBMusicObject new];
            musicObject.musicUrl = aData[SHARE_PROP_AUDIO];
            message.mediaObject = musicObject;
        }
        else {
            WBWebpageObject *webpageObject = [WBWebpageObject new];
            webpageObject.webpageUrl = aData[SHARE_PROP_TARGET];
            message.mediaObject = webpageObject;
        }
        message.mediaObject.objectID = [NSDate date].description;
        message.mediaObject.description = aData[SHARE_PROP_DESP];
        message.mediaObject.title = aData[SHARE_PROP_TITLE];
        if (aImage) {
            //            @warning 大小小于32k
            message.mediaObject.thumbnailData = UIImageJPEGRepresentation(aImage, 0.7);
        }
    }
    
    WBAuthorizeRequest *authRequest = [self _genAuthRequest:aData];
    NSString *accessToken = @"";//aData[RCTWBShareAccessToken];
    WBSendMessageToWeiboRequest *request = [WBSendMessageToWeiboRequest requestWithMessage:message
                                                                                  authInfo:authRequest
                                                                              access_token:accessToken];
    
    BOOL success = [WeiboSDK sendRequest:request];
    if (!success) {
        NSMutableDictionary *body = [NSMutableDictionary new];
        body[EVENT_PROP_SOCIAL_CODE] = @(-1);
        body[EVENT_PROP_SOCIAL_MSG] = INVOKE_FAILED;
        body[EVENT_PROP_SOCIAL_TYPE] = @"WBSendMessageToWeiboResponse";
        [_bridge.eventDispatcher sendAppEventWithName:EVENT_WEIBO_RESP
                                                 body:body];
    }
}

- (WBAuthorizeRequest *)_genAuthRequest:(NSDictionary *)config
{
    NSString *redirectURI = config[@"redirectURI"];
    NSString *scope = config[@"scope"];
    
    WBAuthorizeRequest *authRequest = [WBAuthorizeRequest request];
    authRequest.redirectURI = redirectURI;
    authRequest.scope = scope;
    
    return authRequest;
}

@end
