//
//  RCTQQModule.m
//  LECValleyUIKit
//
//  Created by RaoJia on 2016/12/25.
//  Copyright © 2016年 leCloud. All rights reserved.
//

#import "Social.h"

#import "RCTQQModule.h"

#import <TencentOpenAPI/TencentOAuth.h>
#import <TencentOpenAPI/TencentOAuthObject.h>
#import <TencentOpenAPI/QQApiInterface.h>
#import <TencentOpenAPI/QQApiInterfaceObject.h>

#import <React/RCTImageLoader.h>
#import <React/RCTEventDispatcher.h>


@interface RCTQQModule()<QQApiInterfaceDelegate, TencentSessionDelegate> {
    TencentOAuth* _qqapi;
}

@end

@implementation RCTQQModule

@synthesize bridge = _bridge;

RCT_EXPORT_MODULE();

- (NSDictionary *)constantsToExport
{
    return @{ @"SHARE_TYPE_NEWS"  : SHARE_TYPE_NEWS,
              @"SHARE_TYPE_IMAGE" : SHARE_TYPE_IMAGE,
              @"SHARE_TYPE_TEXT"  : SHARE_TYPE_TEXT,
              @"SHARE_TYPE_VIDEO" : SHARE_TYPE_VIDEO,
              @"SHARE_TYPE_AUDIO" : SHARE_TYPE_AUDIO,
              @"SHARE_TYPE_APP"   : SHARE_TYPE_APP};
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

- (void)handleOpenURL:(NSNotification *)note
{
    NSDictionary *userInfo = note.userInfo;
    NSString *url = userInfo[@"url"];
    if ([TencentOAuth HandleOpenURL:[NSURL URLWithString:url]]) {
    }
    else {
        [QQApiInterface handleOpenURL:[NSURL URLWithString:url] delegate:self];
    }
}

RCT_EXPORT_METHOD(getApiVersion:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    if( _qqapi == nil ){
        reject(CODE_NOT_REGISTERED, NOT_REGISTERED,nil);
        return;
    }
    resolve( [NSString stringWithFormat:@"%@.%@",[TencentOAuth sdkVersion],[TencentOAuth sdkSubVersion]]);
}

RCT_EXPORT_METHOD(isAppInstalled:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    if( _qqapi == nil ){
        reject(CODE_NOT_REGISTERED, NOT_REGISTERED,nil);
        return;
    }
    resolve(@([QQApiInterface isQQInstalled]));
}

RCT_EXPORT_METHOD(isAppSupportApi:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    if( _qqapi == nil ){
        reject(@"-1", NOT_REGISTERED,nil);
        return;
    }
    resolve(@([QQApiInterface isQQSupportApi]));
}

RCT_EXPORT_METHOD(login:(NSString *)scopes
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    if( _qqapi == nil ){
        reject(CODE_NOT_REGISTERED, NOT_REGISTERED,nil);
        return;
    }
    
    NSArray *scopeArray = nil;
    if (scopes && scopes.length) {
        scopeArray = [scopes componentsSeparatedByString:@","];
    }
    if (scopeArray == nil) {
        scopeArray = @[@"get_user_info", @"get_simple_userinfo"];
    }
    BOOL success = [_qqapi authorize:scopeArray];
    if (success) {
        resolve(@[[NSNull null]]);
    }else {
        reject(CODE_INVOKE_FAILED,INVOKE_FAILED,nil);
    }
}

RCT_EXPORT_METHOD(shareToQQ:(NSDictionary *)data
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    if( _qqapi == nil ){
        reject(CODE_NOT_REGISTERED, NOT_REGISTERED,nil);
        return;
    }
    
    [self _shareToQQWithData:data scene:0 resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(shareToQzone:(NSDictionary *)data
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    if( _qqapi == nil ){
        reject(CODE_NOT_REGISTERED, NOT_REGISTERED,nil);
        return;
    }
    
    [self _shareToQQWithData:data scene:1 resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(logout)
{
    
    if( _qqapi != nil && [_qqapi isSessionValid] ){
        [_qqapi logout:nil];
    }
}

- (void)_shareToQQWithData:(NSDictionary *)aData
                     scene:(int)aScene
                   resolve:(RCTPromiseResolveBlock)resolve
                    reject:(RCTPromiseRejectBlock)reject{
    
    NSString *imageUrl = aData[SHARE_PROP_IMAGE];    
    if (imageUrl.length && _bridge.imageLoader) {
        
        CGSize size = CGSizeZero;
        if ([aData[SHARE_PROP_TYPE] isEqualToString:SHARE_PROP_IMAGE]) {
            CGFloat thumbImageSize = 80;
            size = CGSizeMake(thumbImageSize,thumbImageSize);
        }
        
        [_bridge.imageLoader loadImageWithURLRequest:[RCTConvert NSURLRequest:imageUrl]
                                            callback:^(NSError *error, UIImage *image) {
                                                
            dispatch_async(dispatch_get_main_queue(), ^{
                [self _shareToQQWithData:aData
                                   image:image
                                   scene:aScene
                                 resolve:resolve
                                  reject:reject];
            });
        }];
    }else {
        [self _shareToQQWithData:aData
                           image:nil
                           scene:aScene
                         resolve:resolve
                          reject:reject];
    }
}


- (void)_shareToQQWithData:(NSDictionary *)aData
                     image:(UIImage*) image
                     scene:(int)aScene
                   resolve:(RCTPromiseResolveBlock)resolve
                    reject:(RCTPromiseRejectBlock)reject {
    
    NSString* type = aData[SHARE_PROP_TYPE];
    
    NSString *title = aData[SHARE_PROP_TITLE];
    NSString *description= aData[SHARE_PROP_DESP];
    NSString *imgPath = aData[SHARE_PROP_THUMB_IMAGE];
    NSString *webpageUrl = aData[SHARE_PROP_TARGET]? :@"";
    NSString *flashUrl = aData[@"flashUrl"];
    
    QQApiObject *message = nil;
    
    if ([type isEqualToString:SHARE_TYPE_NEWS]) { //图文
        
        message = [QQApiNewsObject
                   objectWithURL:[NSURL URLWithString:webpageUrl]
                   title:title
                   description:description
                   previewImageURL:[NSURL URLWithString:imgPath]];
        
    }else if ([type isEqualToString:SHARE_TYPE_TEXT]) { //纯文字
        
        message = [QQApiTextObject objectWithText:description];
        
    }else if ([type isEqualToString:SHARE_TYPE_IMAGE]) { //纯图
        
        NSData *imgData = UIImageJPEGRepresentation(image, 1);
        message = [QQApiImageObject objectWithData:imgData
                                  previewImageData:imgData
                                             title:title
                                       description:description];
        
    }else if ([type isEqualToString:SHARE_TYPE_AUDIO]) { //音乐
        QQApiAudioObject *audioObj = [QQApiAudioObject objectWithURL:[NSURL URLWithString:webpageUrl]
                                                               title:title
                                                         description:description
                                                     previewImageURL:[NSURL URLWithString:imgPath]];
        if (flashUrl) {
            [audioObj setFlashURL:[NSURL URLWithString:flashUrl]];
        }
        message = audioObj;
        
    }else if ([type isEqualToString:SHARE_TYPE_VIDEO] ) { //视频
        QQApiVideoObject *videoObj = [QQApiVideoObject objectWithURL:[NSURL URLWithString:webpageUrl]
                                                               title:title
                                                         description:description
                                                     previewImageURL:[NSURL URLWithString:imgPath]];
        if (flashUrl) {
            [videoObj setFlashURL:[NSURL URLWithString:flashUrl]];
        }
        message = videoObj;
    }
    
    QQApiSendResultCode sent = EQQAPISENDFAILD;
    
    if (message != nil) {
        SendMessageToQQReq *req = [SendMessageToQQReq reqWithContent:message];
        if (aScene == 0) {
            sent = [QQApiInterface sendReq:req];
        }else {
            sent = [QQApiInterface SendReqToQZone:req];
        }
    }
    
    if (sent == EQQAPISENDSUCESS) {
        resolve(@[[NSNull null]]);
    }else if (sent == EQQAPIAPPSHAREASYNC) {
        resolve(@[[NSNull null]]);
    }else {
        reject(CODE_INVOKE_FAILED,INVOKE_FAILED,nil);
    }
}


- (void)_autoRegisterAPI
{
    NSString *appId = nil;
    NSArray *list = [[[NSBundle mainBundle] infoDictionary] valueForKey:@"CFBundleURLTypes"];
    for (NSDictionary *item in list) {
        NSString *name = item[@"CFBundleURLName"];
        if ([name isEqualToString:@"qq_appid"]) {
            NSArray *schemes = item[@"CFBundleURLSchemes"];
            if (schemes.count > 0){
                appId = [schemes[0] substringFromIndex:@"tencent".length];
                break;
            }
        }
    }
    _qqapi = [[TencentOAuth alloc] initWithAppId:appId andDelegate:self];
    
}

#pragma mark - qq delegate
- (void)onReq:(QQBaseReq *)req
{
    
}

+ (BOOL)isPureInt:(NSString *)string{
    NSScanner* scan = [NSScanner scannerWithString:string];
    int val;
    return [scan scanInt:&val] && [scan isAtEnd];
}

- (void)onResp:(QQBaseResp *)resp
{
    if( resp.type == ESENDMESSAGETOQQRESPTYPE ){
        
        NSMutableDictionary *body = @{EVENT_PROP_SOCIAL_TYPE:@"QQShareResponse"}.mutableCopy;
        int errCode = -5; //非法返回
        
//        int val;
        if([[self class] isPureInt:resp.result]){
            errCode = [resp.result intValue];
        }
        
        body[@"description"] = resp.errorDescription;
        body[@"extendInfo"] = resp.extendInfo;
        
        if(errCode == 0){
            body[EVENT_PROP_SOCIAL_CODE] = @(SHARE_RESULT_CODE_SUCCESSFUL);
            body[EVENT_PROP_SOCIAL_MSG] = SHARE_RESULT_MSG_SUCCESSFUL;
        }else if(-4 == errCode){
            body[EVENT_PROP_SOCIAL_MSG] = SHARE_RESULT_MSG_CANCEL;
        }else {
            body[EVENT_PROP_SOCIAL_MSG] = [NSString stringWithFormat:@"%@：%@", SHARE_RESULT_MSG_FAILED, resp.errorDescription] ;
        }
        
        [self.bridge.eventDispatcher sendAppEventWithName:EVENT_QQ_RESP
                                                     body:body];
    }
}

- (void)isOnlineResponse:(NSDictionary *)response
{
    
}

#pragma mark - oauth delegate
- (void)tencentDidLogin
{
    NSMutableDictionary *body = @{EVENT_PROP_SOCIAL_TYPE:@"QQAuthorizeResponse"}.mutableCopy;
    body[@"openid"] = _qqapi.openId?_qqapi.openId:[NSNull null];
    body[@"access_token"] = _qqapi.accessToken?_qqapi.accessToken:[NSNull null];
    body[@"expires_in"] = @([_qqapi.expirationDate timeIntervalSince1970]*1000);
    body[@"unionid"] = _qqapi.unionid?_qqapi.unionid:[NSNull null];
    body[@"appid"] =_qqapi.appId?_qqapi.appId:[NSNull null];
    body[EVENT_PROP_SOCIAL_CODE] = @(AUTH_RESULT_CODE_SUCCESSFUL);
    body[EVENT_PROP_SOCIAL_MSG] = AUTH_RESULT_MSG_SUCCESSFUL;
    
    [self.bridge.eventDispatcher sendAppEventWithName:EVENT_QQ_RESP
                                                 body:body];
}

- (void)tencentDidNotLogin:(BOOL)cancelled
{
    NSMutableDictionary *body = @{EVENT_PROP_SOCIAL_TYPE:@"QQAuthorizeResponse"}.mutableCopy;
    if (cancelled) {
        body[EVENT_PROP_SOCIAL_CODE] = @(AUTH_RESULT_CODE_CANCEL);
        body[EVENT_PROP_SOCIAL_MSG] = AUTH_RESULT_MSG_CANCEL;
    }else {
        body[EVENT_PROP_SOCIAL_CODE] = @(AUTH_RESULT_CODE_FAILED);
        body[EVENT_PROP_SOCIAL_MSG] = AUTH_RESULT_MSG_FAILED;
    }
    [self.bridge.eventDispatcher sendAppEventWithName:EVENT_QQ_RESP
                                                 body:body];
    
}

- (void)tencentDidNotNetWork
{
}

@end
