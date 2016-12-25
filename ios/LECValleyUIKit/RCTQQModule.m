//
//  RCTQQModule.m
//  LECValleyUIKit
//
//  Created by RaoJia on 2016/12/25.
//  Copyright © 2016年 leCloud. All rights reserved.
//

#import "LECValley.h"

#import "RCTQQModule.h"

#import <TencentOpenAPI/TencentOAuth.h>
#import <TencentOpenAPI/TencentOAuthObject.h>
#import <TencentOpenAPI/QQApiInterface.h>
#import <TencentOpenAPI/QQApiInterfaceObject.h>

#import "../Libraries/Image/RCTImageLoader.h"

#import "RCTBridge.h"
#import "RCTLog.h"
#import "RCTEventDispatcher.h"


#define RCTQQShareTypeNews @"news"
#define RCTQQShareTypeImage @"image"
#define RCTQQShareTypeText @"text"
#define RCTQQShareTypeVideo @"video"
#define RCTQQShareTypeAudio @"audio"

#define RCTQQShareType @"type"
#define RCTQQShareTitle @"title"
#define RCTQQShareDescription @"description"
#define RCTQQShareWebpageUrl @"webpageUrl"
#define RCTQQShareImageUrl @"imageUrl"


@interface RCTQQModule()<QQApiInterfaceDelegate, TencentSessionDelegate> {
    TencentOAuth* _qqapi;
}

@end

@implementation RCTQQModule

@synthesize bridge = _bridge;

RCT_EXPORT_MODULE();

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
        reject(@"-1", NOT_REGISTERED,nil);
        return;
    }
    resolve( [NSString stringWithFormat:@"%@.%@",[TencentOAuth sdkVersion],[TencentOAuth sdkSubVersion]]);
}

RCT_EXPORT_METHOD(isQQInstalled:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    if( _qqapi == nil ){
        reject(@"-1", NOT_REGISTERED,nil);
        return;
    }
    resolve(@([QQApiInterface isQQInstalled]));
}

RCT_EXPORT_METHOD(isQQSupportApi:(RCTPromiseResolveBlock)resolve
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
        reject(@"-1", NOT_REGISTERED,nil);
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
    }
    else {
        reject(@"-3",INVOKE_FAILED,nil);
    }
}

RCT_EXPORT_METHOD(shareToQQ:(NSDictionary *)data
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    [self _shareToQQWithData:data scene:0 resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(shareToQzone:(NSDictionary *)data
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    [self _shareToQQWithData:data scene:1 resolve:resolve reject:reject];
}

RCT_EXPORT_METHOD(logout)
{
    [_qqapi logout:nil];
}

- (void)_shareToQQWithData:(NSDictionary *)aData
                     scene:(int)aScene
                   resolve:(RCTPromiseResolveBlock)resolve
                    reject:(RCTPromiseRejectBlock)reject{
    
    NSString *imageUrl = aData[RCTQQShareImageUrl];
    if (imageUrl.length && _bridge.imageLoader) {
        CGSize size = CGSizeZero;
        if (![aData[RCTQQShareType] isEqualToString:RCTQQShareTypeImage]) {
            CGFloat thumbImageSize = 80;
            size = CGSizeMake(thumbImageSize,thumbImageSize);
        }
        [_bridge.imageLoader loadImageWithURLRequest:[RCTConvert NSURLRequest:imageUrl] callback:^(NSError *error, UIImage *image) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [self _shareToQQWithData:aData image:image scene:aScene resolve:resolve reject:reject];
            });
        }];
    }
    else {
        [self _shareToQQWithData:aData image:nil scene:aScene resolve:resolve reject:reject];
    }
}


- (void)_shareToQQWithData:(NSDictionary *)aData
                     image:(UIImage*) image
                     scene:(int)aScene
                   resolve:(RCTPromiseResolveBlock)resolve
                    reject:(RCTPromiseRejectBlock)reject {
    
    NSString *type = aData[RCTQQShareType];
    
    NSString *title = aData[RCTQQShareTitle];
    
    NSString *description= aData[RCTQQShareDescription];
    NSString *imgPath = aData[RCTQQShareImageUrl];
    NSString *webpageUrl = aData[RCTQQShareWebpageUrl]? :@"";
    NSString *flashUrl = aData[@"flashUrl"];
    
    QQApiObject *message = nil;
    
    if (type.length <=0 || [type isEqualToString: RCTQQShareTypeNews]) {
        message = [QQApiNewsObject
                   objectWithURL:[NSURL URLWithString:webpageUrl]
                   title:title
                   description:description
                   previewImageURL:[NSURL URLWithString:imgPath]];
    }else if ([type isEqualToString: RCTQQShareTypeText]) {
        message = [QQApiTextObject objectWithText:description];
    }else if ([type isEqualToString: RCTQQShareTypeImage]) {
        NSData *imgData = UIImageJPEGRepresentation(image, 1);
        message = [QQApiImageObject objectWithData:imgData
                                  previewImageData:imgData
                                             title:title
                                       description:description];
        
    }else if ([type isEqualToString: RCTQQShareTypeAudio]) {
        QQApiAudioObject *audioObj = [QQApiAudioObject objectWithURL:[NSURL URLWithString:webpageUrl]
                                                               title:title
                                                         description:description
                                                     previewImageURL:[NSURL URLWithString:imgPath]];
        if (flashUrl) {
            [audioObj setFlashURL:[NSURL URLWithString:flashUrl]];
        }
        message = audioObj;
    }
    else if ([type isEqualToString: RCTQQShareTypeVideo]) {
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
        reject(@"-3",INVOKE_FAILED,nil);
    }
}


- (void)_autoRegisterAPI
{
    NSString *appId = nil;
    NSArray *list = [[[NSBundle mainBundle] infoDictionary] valueForKey:@"CFBundleURLTypes"];
    for (NSDictionary *item in list) {
        NSString *name = item[@"CFBundleURLName"];
        if ([name isEqualToString:@"qq"]) {
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

- (void)onResp:(QQBaseResp *)resp
{
    if ([resp isKindOfClass:[SendMessageToQQResp class]]) {
        
    }
    NSMutableDictionary *body = @{@"type":@"QQShareResponse"}.mutableCopy;
    body[@"errStr"] = resp.errorDescription;
    if (resp.errorDescription) {
        body[@"errCode"] = @(-1);
    }else {
        body[@"errCode"] = @(0);
    }
    body[@"result"]     = resp.result;
    body[@"extendInfo"] = resp.extendInfo;
    
    [self.bridge.eventDispatcher sendAppEventWithName:EVENT_QQ_RESP
                                                 body:body];
}

- (void)isOnlineResponse:(NSDictionary *)response
{
    
}

#pragma mark - oauth delegate
- (void)tencentDidLogin
{
    NSMutableDictionary *body = @{@"type":@"QQAuthorizeResponse"}.mutableCopy;
    body[@"errCode"] = @(0);
    body[@"openid"] = _qqapi.openId?_qqapi.openId:[NSNull null];
    body[@"access_token"] = _qqapi.accessToken?_qqapi.accessToken:[NSNull null];
    body[@"expires_in"] = @([_qqapi.expirationDate timeIntervalSinceNow]*1000);
    body[@"unionid"] = _qqapi.unionid?_qqapi.unionid:[NSNull null];
    body[@"appid"] =_qqapi.appId?_qqapi.appId:[NSNull null];
    
    [self.bridge.eventDispatcher sendAppEventWithName:EVENT_QQ_RESP
                                                 body:body];
}

- (void)tencentDidNotLogin:(BOOL)cancelled
{
    NSMutableDictionary *body = @{@"type":@"QQAuthorizeResponse"}.mutableCopy;
    if (cancelled) {
        body[@"errCode"] = @(2);
        body[@"errStr"] = @"login canceled";
    }else {
        body[@"errCode"] = @(1);
        body[@"errStr"] = @"login failed";
    }
    [self.bridge.eventDispatcher sendAppEventWithName:EVENT_QQ_RESP
                                                 body:body];
    
}

- (void)tencentDidNotNetWork
{
}

@end
