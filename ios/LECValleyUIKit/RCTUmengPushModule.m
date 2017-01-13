//
//  RCTUmengPushModule.m
//  LECValleyUIKit
//
//  Created by RaoJia on 2017/1/11.
//  Copyright © 2017年 leCloud. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "RCTUmengPushModule.h"

#import "UMessage.h"

#import <React/RCTEventDispatcher.h>

#define UMSYSTEM_VERSION_GREATER_THAN_OR_EQUAL_TO(v)  ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] != NSOrderedAscending)
#define _IPHONE80_ 80000

#define EVENT_UMENG_RECV_MESSAGE          @"EVENT_UMENG_RECV_MESSAGE"
#define EVENT_UMENG_OPEN_MESSAGE          @"EVENT_UMENG_OPEN_MESSAGE"

//static NSString * const EVENT_UMENG_RECV_MESSAGE   = @"EVENT_UMENG_RECV_MESSAGE";
//static NSString * const EVENT_UMENG_OPEN_MESSAGE   = @"EVENT_UMENG_RECV_MESSAGE";

static RCTUmengPushModule *_instance        = nil;

@interface RCTUmengPushModule ()
@property (nonatomic, copy) NSString *deviceToken;
@end


@implementation RCTUmengPushModule

@synthesize bridge = _bridge;

RCT_EXPORT_MODULE()

+ (instancetype)sharedInstance {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        if(_instance == nil) {
            _instance = [[self alloc] init];
        }
    });
    return _instance;
}

+ (instancetype)allocWithZone:(struct _NSZone *)zone {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        if(_instance == nil) {
            _instance = [super allocWithZone:zone];
            [_instance setupUMessage];
        }
    });
    return _instance;
}

+ (dispatch_queue_t)sharedMethodQueue {
    static dispatch_queue_t methodQueue;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        methodQueue = dispatch_get_main_queue();
        //        methodQueue = dispatch_queue_create("com.lecloud.valley.umeng-push", DISPATCH_QUEUE_SERIAL);
    });
    return methodQueue;
}

- (dispatch_queue_t)methodQueue {
    return [RCTUmengPushModule sharedMethodQueue];
}

//- (NSDictionary<NSString *, id> *)constantsToExport {
//    return @{DidReceiveMessage: DidReceiveMessage,DidOpenMessage: DidOpenMessage,};
//}

- (NSDictionary *)constantsToExport
{
    return @{ @"EVENT_UMENG_RECV_MESSAGE" :EVENT_UMENG_RECV_MESSAGE,
              @"EVENT_UMENG_OPEN_MESSAGE" :EVENT_UMENG_OPEN_MESSAGE,};
}

- (NSArray<NSString *> *)supportedEvents
{
    return @[@"EVENT_UMENG_OPEN_MESSAGE"];
}


- (void)didReceiveRemoteNotification:(NSDictionary *)userInfo
{
//    [self.bridge.eventDispatcher sendAppEventWithName:EVENT_UMENG_RECV_MESSAGE
//                                                 body:userInfo];
    [self sendEventWithName:EVENT_UMENG_RECV_MESSAGE
                       body:userInfo];
}

- (void)didOpenRemoteNotification:(NSDictionary *)userInfo
{
//    [self.bridge.eventDispatcher sendAppEventWithName:EVENT_UMENG_OPEN_MESSAGE
//                                                 body:userInfo];
    
    [self sendEventWithName:EVENT_UMENG_OPEN_MESSAGE
                       body:userInfo];
}

RCT_EXPORT_METHOD(setAutoAlert:(BOOL)value)
{
    [UMessage setAutoAlert:value];
}

RCT_EXPORT_METHOD(getDeviceToken:(RCTResponseSenderBlock)callback)
{
    NSString *deviceToken = self.deviceToken;
    if(deviceToken == nil) {
        deviceToken = @"";
    }
    callback(@[deviceToken]);
}

/**
 *  初始化UM的一些配置
 */
- (void)setupUMessage
{
    [UMessage setAutoAlert:NO];
}

+ (void)application:(UIApplication *)application registerWithAppkey:(NSString *)appkey launchOptions:(NSDictionary *)launchOptions
{
    //set AppKey and LaunchOptions
    [UMessage startWithAppkey:appkey launchOptions:launchOptions];
    
    //注册通知
    [UMessage registerForRemoteNotifications];
    
    //iOS10必须加下面这段代码
    UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];
    center.delegate = application.delegate;
    UNAuthorizationOptions types10 = UNAuthorizationOptionBadge|UNAuthorizationOptionAlert|UNAuthorizationOptionSound;
    [center requestAuthorizationWithOptions:types10
                          completionHandler:^(BOOL granted, NSError * _Nullable error) {
                              if (granted) {
                                  //点击允许
                                  //这里可以添加一些自己的逻辑
                              } else {
                                  //点击不允许
                                  //这里可以添加一些自己的逻辑
                              }
                          }];
    
    //如果你期望使用交互式(只有iOS 8.0及以上有)的通知，请参考下面注释部分的初始化代码
    UIMutableUserNotificationAction *action1 = [[UIMutableUserNotificationAction alloc] init];
    action1.identifier = @"action1_identifier";
    action1.title = @"打开应用";
    action1.activationMode = UIUserNotificationActivationModeForeground;//当点击的时候启动程序
    
    UIMutableUserNotificationAction *action2 = [[UIMutableUserNotificationAction alloc] init];  //第二按钮
    action2.identifier = @"action2_identifier";
    action2.title = @"忽略";
    action2.activationMode = UIUserNotificationActivationModeBackground;//当点击的时候不启动程序，在后台处理
    action2.authenticationRequired = YES;//需要解锁才能处理，如果action.activationMode = UIUserNotificationActivationModeForeground;则这个属性被忽略；
    action2.destructive = YES;
    UIMutableUserNotificationCategory *actionCategory1 = [[UIMutableUserNotificationCategory alloc] init];
    actionCategory1.identifier = @"category1";//这组动作的唯一标示
    [actionCategory1 setActions:@[action1,action2] forContext:(UIUserNotificationActionContextDefault)];
    NSSet *categories = [NSSet setWithObjects:actionCategory1, nil];
    
    //如果要在iOS10显示交互式的通知，必须注意实现以下代码
    if ([[[UIDevice currentDevice] systemVersion]intValue]>=10) {
        UNNotificationAction *action1_ios10 = [UNNotificationAction actionWithIdentifier:@"action1_ios10_identifier" title:@"打开应用" options:UNNotificationActionOptionForeground];
        UNNotificationAction *action2_ios10 = [UNNotificationAction actionWithIdentifier:@"action2_ios10_identifier" title:@"忽略" options:UNNotificationActionOptionForeground];
        
        //UNNotificationCategoryOptionNone
        //UNNotificationCategoryOptionCustomDismissAction  清除通知被触发会走通知的代理方法
        //UNNotificationCategoryOptionAllowInCarPlay       适用于行车模式
        UNNotificationCategory *category1_ios10 = [UNNotificationCategory categoryWithIdentifier:@"category101" actions:@[action1_ios10,action2_ios10]   intentIdentifiers:@[] options:UNNotificationCategoryOptionCustomDismissAction];
        NSSet *categories_ios10 = [NSSet setWithObjects:category1_ios10, nil];
        [center setNotificationCategories:categories_ios10];
    }else{        
        [UMessage registerForRemoteNotifications:categories];
    }
    
    //如果对角标，文字和声音的取舍，请用下面的方法
    //UIRemoteNotificationType types7 = UIRemoteNotificationTypeBadge|UIRemoteNotificationTypeAlert|UIRemoteNotificationTypeSound;
    //UIUserNotificationType types8 = UIUserNotificationTypeAlert|UIUserNotificationTypeSound|UIUserNotificationTypeBadge;
    //[UMessage registerForRemoteNotifications:categories withTypesForIos7:types7 withTypesForIos8:types8];
    
    
    //由推送第一次打开应用时
    if(launchOptions[@"UIApplicationLaunchOptionsRemoteNotificationKey"]) {
        [self didReceiveRemoteNotificationWhenFirstLaunchApp:launchOptions[@"UIApplicationLaunchOptionsRemoteNotificationKey"]];
    }
    
#ifdef DEBUG
    [UMessage setLogEnabled:YES];
#endif
}

+ (void)application:(UIApplication *)application didRegisterDeviceToken:(NSData *)deviceToken
{
    [RCTUmengPushModule sharedInstance].deviceToken = [[[[deviceToken description] stringByReplacingOccurrencesOfString: @"<" withString: @""]
                                                        stringByReplacingOccurrencesOfString: @">" withString: @""]
                                                       stringByReplacingOccurrencesOfString: @" " withString: @""];
    [UMessage registerDeviceToken:deviceToken];
}

+ (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo
{
    [UMessage didReceiveRemoteNotification:userInfo];
    //send event
    if (application.applicationState == UIApplicationStateInactive) {
        [[RCTUmengPushModule sharedInstance] didOpenRemoteNotification:userInfo];
    }else {
        [[RCTUmengPushModule sharedInstance] didReceiveRemoteNotification:userInfo];
    }
}

+ (void)didReceiveRemoteNotificationWhenFirstLaunchApp:(NSDictionary *)launchOptions
{
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), [self sharedMethodQueue], ^{
        //判断当前模块是否正在加载，已经加载成功，则发送事件
        if(![RCTUmengPushModule sharedInstance].bridge.isLoading) {
            [UMessage didReceiveRemoteNotification:launchOptions];
            [[RCTUmengPushModule sharedInstance] didOpenRemoteNotification:launchOptions];
        }else {
            [self didReceiveRemoteNotificationWhenFirstLaunchApp:launchOptions];
        }
    });
}

@end
