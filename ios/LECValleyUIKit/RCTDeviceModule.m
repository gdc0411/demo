//
//  DeviceModule.m
//  DemoProject
//
//  Created by RaoJia on 2016/12/5.
//  Copyright © 2016年 LeCloud. All rights reserved.
//

#import "RCTDeviceModule.h"
#import "KeyChainStore.h"
//#import <SSKeychain.h>
//#import <SSKeychainQuery.h>

#import <UIKit/UIKit.h>


@implementation RCTDeviceModule

RCT_EXPORT_MODULE();

RCT_REMAP_METHOD(getDeviceIdentifier,
                 resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject) {
  
  NSDictionary* dict = @{@"DeviceId": [[self class]getUUID],
                         @"PhoneType": [NSNumber numberWithInt:1],
                         @"DeviceModel":[[UIDevice currentDevice] model],
                         @"DeviceManufacture":@"Apple",
                         @"DeviceSoftwareVersion": [NSString stringWithFormat:@"%@ %@",
                                                    [[UIDevice currentDevice] systemName],
                                                    [[UIDevice currentDevice] systemVersion]],
                         @"VersionSdk":@"4.3.1",
                         @"VersionRelease":[[[NSBundle mainBundle] infoDictionary]objectForKey:@"CFBundleShortVersionString"],
                         @"PackageName":[[NSBundle mainBundle] bundleIdentifier],
                         @"Language":[[NSLocale preferredLanguages] objectAtIndex:0],
                         @"Country":[[NSLocale currentLocale] localeIdentifier]};
  resolve(dict);
  
  //reject(@"-1001", @"not respond this method", nil);
}


+(NSString *)getUUID
{
  NSString * strUUID = (NSString *)[KeyChainStore load:KEY_USERNAME_PASSWORD];
  
  //首次执行该方法时，uuid为空
  if ([strUUID isEqualToString:@""] || !strUUID){
    //生成一个uuid的方法
    CFUUIDRef uuidRef = CFUUIDCreate(kCFAllocatorDefault);
    
    strUUID = (NSString *)CFBridgingRelease(CFUUIDCreateString (kCFAllocatorDefault,uuidRef));
    
    //将该uuid保存到keychain
    [KeyChainStore save:KEY_USERNAME_PASSWORD data:strUUID];
    
  }
  return strUUID;
}

//+ (NSString *)getDeviceId
//{
//  NSString * currentDeviceUUIDStr = [SSKeychain passwordForService:@" "account:@"uuid"];
//  if (currentDeviceUUIDStr == nil || [currentDeviceUUIDStr isEqualToString:@""])
//  {
//    NSUUID * currentDeviceUUID  = [UIDevice currentDevice].identifierForVendor;
//    currentDeviceUUIDStr = currentDeviceUUID.UUIDString;
//    currentDeviceUUIDStr = [currentDeviceUUIDStr stringByReplacingOccurrencesOfString:@"-" withString:@""];
//    currentDeviceUUIDStr = [currentDeviceUUIDStr lowercaseString];
//    [SSKeychain setPassword: currentDeviceUUIDStr forService:@" "account:@"uuid"];
//  }
//  return currentDeviceUUIDStr;
//}


//+ (NSString *)getUniqueDeviceIdentifierAsString
//{
//  NSString *appName=[[[NSBundle mainBundle] infoDictionary] objectForKey:(NSString*)kCFBundleNameKey];
//  
//  NSString *strApplicationUUID =  [SAMKeychain passwordForService:appName account:@"incoding"];
//  if (strApplicationUUID == nil)
//  {
//    strApplicationUUID  = [[[UIDevice currentDevice] identifierForVendor] UUIDString];
//    
//    NSError *error = nil;
//    SAMKeychainQuery *query = [[SAMKeychainQuery alloc] init];
//    query.service = appName;
//    query.account = @"incoding";
//    query.password = strApplicationUUID;
//    query.synchronizationMode = SAMKeychainQuerySynchronizationModeNo;
//    [query save:&error];
//    
//  }
//  
//  return strApplicationUUID;
//}

@end
