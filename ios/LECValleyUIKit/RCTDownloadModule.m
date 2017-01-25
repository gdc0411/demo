//
//  RCTDownloadModule.m
//  LECValleyUIKit
//
//  Created by RaoJia on 2017/1/26.
//  Copyright © 2017年 leCloud. All rights reserved.
//

#import "RCTDownloadModule.h"
#import <React/RCTEventDispatcher.h>

#define EVENT_DOWNLOAD_ITEM_UPDATE          @"onDownloadItemUpdate"
#define EVENT_TYPE_SUCCESS                  0
#define EVENT_TYPE_START                    3
#define EVENT_TYPE_FAILED                   5

#define EVENT_DOWNLOAD_LIST_UPDATE          @"onDownloadListUpdate"
#define DOWLOAD_STATE_WAITING               0
#define DOWLOAD_STATE_DOWNLOADING           1
#define DOWLOAD_STATE_STOP                  2
#define DOWLOAD_STATE_SUCCESS               3
#define DOWLOAD_STATE_FAILED                4
#define DOWLOAD_STATE_NO_DISPATCH           5
#define DOWLOAD_STATE_NO_PERMISSION         6
#define DOWLOAD_STATE_URL_REQUEST_FAILED    7
#define DOWLOAD_STATE_DISPATCHING           8

@implementation RCTDownloadModule

@synthesize bridge = _bridge;


- (NSDictionary *)constantsToExport
{
    return @{ @"EVENT_DOWNLOAD_ITEM_UPDATE"     : EVENT_DOWNLOAD_ITEM_UPDATE,
              @"EVENT_TYPE_SUCCESS"             : @(EVENT_TYPE_SUCCESS),
              @"EVENT_TYPE_START"               : @(EVENT_TYPE_START),
              @"EVENT_TYPE_FAILED"              : @(EVENT_TYPE_FAILED),
              @"EVENT_DOWNLOAD_LIST_UPDATE"     : EVENT_DOWNLOAD_LIST_UPDATE,
              @"DOWLOAD_STATE_WAITING"          : @(DOWLOAD_STATE_WAITING),
              @"DOWLOAD_STATE_DOWNLOADING"      : @(DOWLOAD_STATE_DOWNLOADING),
              @"DOWLOAD_STATE_STOP"             : @(DOWLOAD_STATE_STOP),
              @"DOWLOAD_STATE_SUCCESS"          : @(DOWLOAD_STATE_SUCCESS),
              @"DOWLOAD_STATE_FAILED"           : @(DOWLOAD_STATE_FAILED),
              @"DOWLOAD_STATE_NO_DISPATCH"      : @(DOWLOAD_STATE_NO_DISPATCH),
              @"DOWLOAD_STATE_NO_PERMISSION"    : @(DOWLOAD_STATE_NO_PERMISSION),
              @"DOWLOAD_STATE_URL_REQUEST_FAILED": @(DOWLOAD_STATE_URL_REQUEST_FAILED),
              @"DOWLOAD_STATE_DISPATCHING"      : @(DOWLOAD_STATE_DISPATCHING)};
}

- (NSArray<NSString *> *)supportedEvents
{
    return @[EVENT_DOWNLOAD_ITEM_UPDATE, EVENT_DOWNLOAD_LIST_UPDATE];
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(download:(NSDictionary *)src
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{

}

RCT_EXPORT_METHOD(list:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    
}

RCT_EXPORT_METHOD(pause:(NSDictionary *)src
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    
}

RCT_EXPORT_METHOD(resume:(NSDictionary *)src
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    
}

RCT_EXPORT_METHOD(retry:(NSDictionary *)src
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    
}

RCT_EXPORT_METHOD(delete:(NSDictionary *)src
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    
}

RCT_EXPORT_METHOD(clear:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    
}


@end
