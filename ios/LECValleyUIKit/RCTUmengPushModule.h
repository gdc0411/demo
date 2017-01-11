//
//  RCTUmengPushModule.h
//  LECValleyUIKit
//
//  Created by RaoJia on 2017/1/11.
//  Copyright © 2017年 leCloud. All rights reserved.
//

#import <React/RCTBridgeModule.h>

@interface RCTUmengPushModule : NSObject<RCTBridgeModule>

+ (void)registerWithAppkey:(NSString *)appkey launchOptions:(NSDictionary *)launchOptions;
+ (void)application:(UIApplication *)application didRegisterDeviceToken:(NSData *)deviceToken;
+ (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo;

@end
