//
//  DeviceModule.m
//  LeDemo
//
//  Created by RaoJia on 2016/12/5.
//  Copyright © 2016年 LeCloud. All rights reserved.
//

#import "RCTDeviceModule.h"
#import "KeyChainStore.h"
//#import <SSKeychain.h>
//#import <SSKeychainQuery.h>

#import <UIKit/UIKit.h>

#include <ifaddrs.h>
#include <arpa/inet.h>


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
                         @"Country":[[NSLocale currentLocale] localeIdentifier],
                         @"IPAddress":[[self class] getDeviceIPAdress]};
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


#pragma mark 获取设备IP
+ (NSString *)getDeviceIPAdress {
    NSString *address = @"an error occurred when obtaining ip address";
    struct ifaddrs *interfaces = NULL;
    struct ifaddrs *temp_addr = NULL;
    int success = 0;
    
    success = getifaddrs(&interfaces);
    
    if (success == 0) { // 0 表示获取成功
        
        temp_addr = interfaces;
        while (temp_addr != NULL) {
            if( temp_addr->ifa_addr->sa_family == AF_INET) {
                // Check if interface is en0 which is the wifi connection on the iPhone
                if ([[NSString stringWithUTF8String:temp_addr->ifa_name] isEqualToString:@"en0"]) {
                    // Get NSString from C String
                    address = [NSString stringWithUTF8String:inet_ntoa(((struct sockaddr_in *)temp_addr->ifa_addr)->sin_addr)];
                }
            }
            
            temp_addr = temp_addr->ifa_next;
        }
    }
    
    freeifaddrs(interfaces);  
    
    NSLog(@"手机的IP是：%@", address);  
    return address;  
}

@end
