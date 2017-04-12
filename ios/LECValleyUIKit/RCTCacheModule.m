//
//  RCTCacheModule.m
//  LECValleyUIKit
//
//  Created by RaoJia on 2017/1/27.
//  Copyright © 2017年 LeCloud. All rights reserved.
//

#import "RCTCacheModule.h"

#import <React/RCTConvert.h>
#import <React/RCTEventDispatcher.h>

#import "UIImageView+WebCache.h"

#define EVENT_CACHE_UPDATE_MESSAGE          @"onCacheUpdateMessage"
#define EVENT_CALC_PROGRESS                 0 //计算缓存中
#define EVENT_CALC_SUCCESS                  1 //计算缓存成功
#define EVENT_CALC_FAILED                   2 //计算缓存失败
#define EVENT_CLEAR_PROGRESS                3 //清除缓存中
#define EVENT_CLEAR_SUCCESS                 4 //清除缓存成功
#define EVENT_CLEAR_FAILED                  5 //清除缓存失败


@implementation RCTCacheModule

@synthesize bridge = _bridge;

RCT_EXPORT_MODULE();

- (NSDictionary *)constantsToExport
{
    return @{ @"EVENT_CACHE_UPDATE_MESSAGE" : EVENT_CACHE_UPDATE_MESSAGE,
              @"EVENT_CALC_PROGRESS"        : @(EVENT_CALC_PROGRESS),
              @"EVENT_CALC_SUCCESS"         : @(EVENT_CALC_SUCCESS),
              @"EVENT_CALC_FAILED"          : @(EVENT_CALC_FAILED),
              @"EVENT_CLEAR_PROGRESS"       : @(EVENT_CLEAR_PROGRESS),
              @"EVENT_CLEAR_SUCCESS"        : @(EVENT_CLEAR_SUCCESS),
              @"EVENT_CLEAR_FAILED"         : @(EVENT_CLEAR_FAILED)};
}

- (NSArray<NSString *> *)supportedEvents
{
    return @[EVENT_CACHE_UPDATE_MESSAGE];
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}


RCT_EXPORT_METHOD(calc:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    NSLog(@"外部控制——— 计算缓存大小！");
    
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_async(queue, ^{
        [self calcCachesWithComplete: ^(int eventType, NSString *cacheText){
            
            NSMutableDictionary* eventPara = [NSMutableDictionary dictionaryWithCapacity:2];
            if(eventType == EVENT_CALC_SUCCESS ){
                [eventPara setValue:[NSNumber numberWithInt:EVENT_CALC_SUCCESS] forKey:@"eventType"];
                [eventPara setValue:cacheText forKey:@"cacheSize"];
            }else if(eventType == EVENT_CALC_FAILED){
                [eventPara setValue:[NSNumber numberWithInt:EVENT_CALC_FAILED] forKey:@"eventType"];
                [eventPara setValue:@"无法计算" forKey:@"cacheSize"];
            }
            [self sendEventWithName:EVENT_CACHE_UPDATE_MESSAGE body:eventPara];
            
        }];
    });
    
    NSMutableDictionary* eventPara = [NSMutableDictionary dictionaryWithCapacity:19];
    [eventPara setValue:[NSNumber numberWithInt:EVENT_CALC_PROGRESS] forKey:@"eventType"];
    [eventPara setValue:@"正在计算" forKey:@"cacheSize"];
    [self sendEventWithName:EVENT_CACHE_UPDATE_MESSAGE body:eventPara];
}


RCT_EXPORT_METHOD(clear:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    NSLog(@"外部控制——— 清理缓存！");
    
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_async(queue, ^{
        [self clearCachesWithComplete: ^(int eventType, NSString *cacheText){
            
            NSMutableDictionary* eventPara = [NSMutableDictionary dictionaryWithCapacity:2];
            if(eventType == EVENT_CLEAR_SUCCESS ){
                [eventPara setValue:[NSNumber numberWithInt:EVENT_CLEAR_SUCCESS] forKey:@"eventType"];
                [eventPara setValue:cacheText forKey:@"cacheSize"];
            }else if(eventType == EVENT_CLEAR_FAILED){
                [eventPara setValue:[NSNumber numberWithInt:EVENT_CLEAR_FAILED] forKey:@"eventType"];
                [eventPara setValue:@"清理缓存失败" forKey:@"cacheSize"];
            }
            [self sendEventWithName:EVENT_CACHE_UPDATE_MESSAGE body:eventPara];
            
        }];
    });
    
    NSMutableDictionary* eventPara = [NSMutableDictionary dictionaryWithCapacity:2];
    [eventPara setValue:[NSNumber numberWithInt:EVENT_CLEAR_PROGRESS] forKey:@"eventType"];
    [eventPara setValue:@"正在清理缓存" forKey:@"cacheSize"];
    [self sendEventWithName:EVENT_CACHE_UPDATE_MESSAGE body:eventPara];
}


#pragma mark 计算缓存
- (void) calcCachesWithComplete: (void (^)( int, NSString* )) handler
{
    long long totalSize = 0;
    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
    for(NSString *path in paths){
        NSLog(@"path: %@",path);
        totalSize += [RCTCacheModule folderSizeAtPath:path];
        NSLog(@"size: %lld", totalSize);
    }
    NSString* sizeText = [RCTCacheModule getFormatSize:totalSize];
    NSLog(@"sizeText: %@", sizeText);
    
//    sleep(5);
    handler? handler(EVENT_CALC_SUCCESS, sizeText):nil;
}

#pragma mark 清理缓存
- (void) clearCachesWithComplete: (void (^)( int, NSString* )) handler
{
    long long totalSize = 0;
    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
    for(NSString *path in paths){
        NSLog(@"path: %@",path);
        [RCTCacheModule clearCache:path];
        
        totalSize += [RCTCacheModule folderSizeAtPath:path];
        NSLog(@"size: %lld", totalSize);
    }
    NSString* sizeText = [RCTCacheModule getFormatSize:totalSize];
    NSLog(@"sizeText: %@", sizeText);
    
    //    sleep(5);
    handler? handler(EVENT_CLEAR_SUCCESS, sizeText):nil;
}



+ (long long) fileSizeAtPath:(NSString *)path
{
    NSFileManager *fileManager=[NSFileManager defaultManager];
    if([fileManager fileExistsAtPath:path]){
        long long size=[fileManager attributesOfItemAtPath:path error:nil].fileSize;
        return size;
    }
    return 0;
}

+ (long long) folderSizeAtPath:(NSString *)path
{
    NSFileManager *fileManager=[NSFileManager defaultManager];
    float folderSize = 0.0;
    if ([fileManager fileExistsAtPath:path]) {
        NSArray *childerFiles=[fileManager subpathsAtPath:path];
        for (NSString *fileName in childerFiles) {
            NSString *absolutePath=[path stringByAppendingPathComponent:fileName];
            folderSize +=[self fileSizeAtPath:absolutePath];
        }
        //SDWebImage框架自身计算缓存的实现
        folderSize+=[[SDImageCache sharedImageCache] getSize];
        return folderSize;
    }
    return 0;
}

+ (void) clearCache:(NSString *)path
{
    NSFileManager *fileManager=[NSFileManager defaultManager];
    if ([fileManager fileExistsAtPath:path]) {
        NSArray *childerFiles=[fileManager subpathsAtPath:path];
        for (NSString *fileName in childerFiles) {
            //如有需要，加入条件，过滤掉不想删除的文件
            NSString *absolutePath=[path stringByAppendingPathComponent:fileName];
            [fileManager removeItemAtPath:absolutePath error:nil];
        }
    }
    [[SDImageCache sharedImageCache] cleanDisk];
}

+ (NSString*) getFormatSize: (long long) size
{
    //设置文件大小格式
    NSString *sizeText = nil;
    if (size >= pow(10, 9)) {
        sizeText = [NSString stringWithFormat:@"%.2fGB", size / pow(10, 9)];
    }else if (size >= pow(10, 6)) {
        sizeText = [NSString stringWithFormat:@"%.2fMB", size / pow(10, 6)];
    }else if (size >= pow(10, 3)) {
        sizeText = [NSString stringWithFormat:@"%.2fKB", size / pow(10, 3)];
    }else {
        sizeText = [NSString stringWithFormat:@"%zdB", size];
    }
    return sizeText;
}


@end
