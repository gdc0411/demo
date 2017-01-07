/**
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

#import "AppDelegate.h"
#import "CodePush.h"

#import "UMessage.h"

#import <React/RCTBundleURLProvider.h>
#import <React/RCTRootView.h>
#import <React/RCTLinkingManager.h>

#import "LECPlayerFoundation.h"
#import "RCTOrientationModule.h"


#define kLCTestBundleID   @"com.lecloud.sdkTest"


@implementation AppDelegate



- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation {
  return [RCTLinkingManager application:application openURL:url sourceApplication:sourceApplication annotation:annotation];
}

- (UIInterfaceOrientationMask)application:(UIApplication *)application supportedInterfaceOrientationsForWindow:(UIWindow *)window {
  return [RCTOrientationModule getOrientation];
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
  
  NSURL *jsCodeLocation;
  
#ifdef DEBUG
//    jsCodeLocation = [[NSBundle mainBundle] URLForResource:@"index.ios" withExtension:@"jsbundle"];
  jsCodeLocation = [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:@"index.ios" fallbackResource:nil];
#else
  jsCodeLocation = [CodePush bundleURL];
#endif
  
  /*
   确保App启动的屏幕方向
   */
  [[UIApplication sharedApplication] setStatusBarOrientation:(UIInterfaceOrientationPortrait) animated:NO];
  
  /*
   *******************************************************************
   *******************************************************************
   友盟功能导入:
   */
  //初始化方法,也可以使用(void)startWithAppkey:(NSString *)appKey launchOptions:(NSDictionary * )launchOptions httpsenable:(BOOL)value;这个方法，方便设置https请求。
  [UMessage startWithAppkey:@"586781631c5dd0359f001286" launchOptions:launchOptions];
  
  //注册通知，如果要使用category的自定义策略，可以参考demo中的代码。
  [UMessage registerForRemoteNotifications];
  
  //iOS10必须加下面这段代码。
  UNUserNotificationCenter *center = [UNUserNotificationCenter currentNotificationCenter];
  center.delegate = self;
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
  //打开日志，方便调试
  [UMessage setLogEnabled:YES];
  
  
  /*
   *******************************************************************
   *******************************************************************
   SDK-Demo使用前注意:
   */
  /*
   1.BundleID设置提醒
   */
  NSString *bundleID = [[NSBundle mainBundle] bundleIdentifier];
  if ([bundleID isEqualToString:kLCTestBundleID])
  {
    NSLog(@"请先设置您的BunldeID");
  }
  //    NSAssert(![bundleID isEqualToString:kLCTestBundleID], @"请先设置您的BunldeID");
  
  /*
   2.请在LCBaseViewController.h中设置直播、点播、活动播放的ID等相关属性参数
   *******************************************************************
   *******************************************************************
   */
  
  /*
   预加载播放器配置信息
   */
  [[LECPlayerFoundation sharedFoundation] startService:LECServiceForChina];
  
  // yes:打开日志 no:关闭日志 （下面分别是控制台和写入文件log）
  [[LECPlayerFoundation sharedFoundation]fileLogEnable:NO];
  [[LECPlayerFoundation sharedFoundation] consoleLogEnable:YES];
  
  
  RCTRootView *rootView = [[RCTRootView alloc] initWithBundleURL:jsCodeLocation
                                                      moduleName:@"LeDemo"
                                               initialProperties:nil
                                                   launchOptions:launchOptions];
  rootView.backgroundColor = [[UIColor alloc] initWithRed:1.0f green:1.0f blue:1.0f alpha:1];
  
  self.window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
  UIViewController *rootViewController = [UIViewController new];
  rootViewController.view = rootView;
  
  self.window.rootViewController = rootViewController;
  [self.window makeKeyAndVisible];
  
  return YES;
}


//iOS10以下使用这个方法接收通知
- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo
{
  //关闭友盟自带的弹出框
  [UMessage setAutoAlert:NO];
  [UMessage didReceiveRemoteNotification:userInfo];
  
  //    self.userInfo = userInfo;
  //    //定制自定的的弹出框
  //    if([UIApplication sharedApplication].applicationState == UIApplicationStateActive)
  //    {
  //        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"标题"
  //                                                            message:@"Test On ApplicationStateActive"
  //                                                           delegate:self
  //                                                  cancelButtonTitle:@"确定"
  //                                                  otherButtonTitles:nil];
  //
  //        [alertView show];
  //
  //    }
}


//iOS10新增：处理前台收到通知的代理方法
-(void)userNotificationCenter:(UNUserNotificationCenter *)center
      willPresentNotification:(UNNotification *)notification
        withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler{
  
  NSDictionary * userInfo = notification.request.content.userInfo;
  if([notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
    //应用处于前台时的远程推送接受
    //关闭友盟自带的弹出框
    [UMessage setAutoAlert:NO];
    //必须加这句代码
    [UMessage didReceiveRemoteNotification:userInfo];
    
  }else{
    //应用处于前台时的本地推送接受
  }
  //当应用处于前台时提示设置，需要哪个可以设置哪一个
  completionHandler(UNNotificationPresentationOptionSound|UNNotificationPresentationOptionBadge|UNNotificationPresentationOptionAlert);
}

//iOS10新增：处理后台点击通知的代理方法
-(void)userNotificationCenter:(UNUserNotificationCenter *)center
didReceiveNotificationResponse:(UNNotificationResponse *)response
        withCompletionHandler:(void (^)())completionHandler{
  
  NSDictionary * userInfo = response.notification.request.content.userInfo;
  if([response.notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
    //应用处于后台时的远程推送接受
    //必须加这句代码
    [UMessage didReceiveRemoteNotification:userInfo];
    
  }else{
    //应用处于后台时的本地推送接受
  }
}


//-(UIInterfaceOrientationMask)application:(UIApplication *)application supportedInterfaceOrientationsForWindow:(UIWindow *)window {
//  NSLog(@"0000000---------%@",NSStringFromClass([[self topViewController] class]));
//  if ([NSStringFromClass([[self topViewController] class]) isEqualToString:@"RCTLeVideoPlayerViewController"]) {
//    //横屏
//    return UIInterfaceOrientationMaskLandscapeRight;
//  }
//  //竖屏
//  return UIInterfaceOrientationMaskPortrait;
//}

//获取界面最上层的控制器
- (UIViewController*)topViewController {
  return [self topViewControllerWithRootViewController:[UIApplication sharedApplication].keyWindow.rootViewController];
}

//一层一层的进行查找判断
- (UIViewController*)topViewControllerWithRootViewController:(UIViewController*)rootViewController {
  if ([rootViewController isKindOfClass:[UITabBarController class]]) {
    UITabBarController* tabBarController = (UITabBarController*)rootViewController;
    return [self topViewControllerWithRootViewController:tabBarController.selectedViewController];
  } else if ([rootViewController isKindOfClass:[UINavigationController class]]) {
    UINavigationController* nav = (UINavigationController*)rootViewController;
    return [self topViewControllerWithRootViewController:nav.visibleViewController];
  } else if (rootViewController.presentedViewController) {
    UIViewController* presentedViewController = rootViewController.presentedViewController;
    return [self topViewControllerWithRootViewController:presentedViewController];
  } else {
    return rootViewController;
  }
}


- (void)applicationWillResignActive:(UIApplication *)application {
  // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
  // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
  // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
  // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
  // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
  // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application {
  // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

@end
