//
//  RCTUmengPushModule.h
//  LECValleyUIKit
//
//  Created by RaoJia on 2017/1/11.
//  Copyright © 2017年 leCloud. All rights reserved.
//

//#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCTUmengPushModule : RCTEventEmitter <RCTBridgeModule>

+ (void)application:(UIApplication *)application registerWithAppkey:(NSString *)appkey launchOptions:(NSDictionary *)launchOptions;

+ (void)application:(UIApplication *)application didRegisterDeviceToken:(NSData *)deviceToken;

+ (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo;

@end
