//
//  RCTUmengPushModule.h
//  LECValleyUIKit
//
//  Created by RaoJia on 2017/1/11.
//  Copyright © 2017年 leCloud. All rights reserved.
//

#import "UMessage.h"

#import <React/RCTEventEmitter.h>

@interface RCTUmengPushModule : RCTEventEmitter <RCTBridgeModule>

+ (void) registerWithlaunchOptions:(NSDictionary *)launchOptions;

+ (void) didFailToRegisterWithError:(NSError *)error;

+ (void) didRegisterDeviceToken:(NSData *)deviceToken;

+ (void) didReceiveRemoteNotification:(NSDictionary *)userInfo;

+ (void) userNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification
          withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler;

+ (void) userNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response
          withCompletionHandler:(void (^)())completionHandler;

@end
