//
//  RCTDownloadModule.m
//  LECValleyUIKit
//
//  Created by RaoJia on 2017/1/26.
//  Copyright © 2017年 leCloud. All rights reserved.
//

#import "RCTDownloadModule.h"

#import <React/RCTConvert.h>
#import <React/RCTEventDispatcher.h>

#import "LECVODDownloadManager.h"

#define maxThreadCount                      5

#define EVENT_DOWNLOAD_ITEM_UPDATE          @"onDownloadItemUpdate"
#define EVENT_TYPE_SUCCESS                  0
#define EVENT_TYPE_START                    3
#define EVENT_TYPE_PROGRESS                 4
#define EVENT_TYPE_FAILED                   5
#define EVENT_TYPE_EXIST                    10

#define EVENT_DOWNLOAD_LIST_UPDATE          @"onDownloadListUpdate"
#define DOWLOAD_STATE_WAITING               1
#define DOWLOAD_STATE_DOWNLOADING           2
#define DOWLOAD_STATE_STOP                  4
#define DOWLOAD_STATE_SUCCESS               3
#define DOWLOAD_STATE_FAILED                5
#define DOWLOAD_STATE_NO_DISPATCH           0
#define DOWLOAD_STATE_NO_PERMISSION         5
#define DOWLOAD_STATE_URL_REQUEST_FAILED    5
#define DOWLOAD_STATE_DISPATCHING           0


@interface RCTDownloadModule () <LECVODDownloadManagerDelegate>

@property (nonatomic, strong) LECVODDownloadManager *sharedManager;
@property (nonatomic, strong) NSArray *downloadList;

@end

@implementation RCTDownloadModule

@synthesize bridge = _bridge;

RCT_EXPORT_MODULE();

- (NSDictionary *)constantsToExport
{
    return @{ @"EVENT_DOWNLOAD_ITEM_UPDATE"     : EVENT_DOWNLOAD_ITEM_UPDATE,
              @"EVENT_TYPE_SUCCESS"             : @(EVENT_TYPE_SUCCESS),
              @"EVENT_TYPE_START"               : @(EVENT_TYPE_START),
              @"EVENT_TYPE_FAILED"              : @(EVENT_TYPE_FAILED),
              @"EVENT_TYPE_EXIST"               : @(EVENT_TYPE_EXIST),
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


- (instancetype)init
{
    self = [super init];
    if (self) {
        _sharedManager                           = [LECVODDownloadManager sharedManager];
        _sharedManager.delegate                  = self;
        _sharedManager.defaultCodeSelectType     = LECVODDownloadManagerDefaultCodeSelectTypeHighest;
        _sharedManager.maxParallelDownloadNumber = maxThreadCount;
    }
    return self;
}


RCT_EXPORT_METHOD(download:(NSDictionary *)source
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    NSLog(@"外部控制——— 下载视频: %@", source);
    
    NSString *uuid          = [source objectForKey:@"uuid"];
    NSString *vuid          = [source objectForKey:@"vuid"];
    NSString *buinessline   = [source objectForKey:@"businessline"];
    bool saas               = [RCTConvert BOOL:[source objectForKey:@"saas"]];
    NSString *rate          = [source objectForKey:@"rate"];
    NSDictionary *videoInfo = [source objectForKey:@"videoInfo"];
    
    if (uuid.length != 0 && vuid.length != 0 && buinessline.length != 0 && _sharedManager!= nil ) {
        
        self.downloadList = _sharedManager.vodItemsList;
        if(self.downloadList && self.downloadList.count > 0){
            for (LECVODDownloadItem *info in _downloadList)
                if([info.vu isEqualToString:[source objectForKey:@"vuid"]]){
                    [self notifyItemEvent:EVENT_TYPE_EXIST withDownloadItem:info andMsg:nil];
                    return;
                }
        }
        
        LECPlayerOption *option= [[LECPlayerOption alloc]init]; //创建选项
        option.p               = buinessline;
        option.businessLine    = (saas)?LECBusinessLineSaas:LECBusinessLineCloud;
        
        LECVODDownloadItem *downloadItem = [_sharedManager createVODDownloadItemWithUu:uuid
                                                                                    vu:vuid
                                                                              userInfo:videoInfo
                                                                   expectVideoCodeType:rate
                                                                          payCheckCode:nil
                                                                           payUserName:nil
                                                                               options:option];
        [_sharedManager startDownloadWithVODItem:downloadItem];
        
    }
}


RCT_EXPORT_METHOD(list:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    NSLog(@"外部控制——— 获取下载列表！");
    _sharedManager?[self notifyListEvent:_sharedManager]:nil;
}

RCT_EXPORT_METHOD(pause:(NSDictionary *)source
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    NSLog(@"外部控制——— 暂停下载:%@", source );
    if (source == nil || _sharedManager == nil || self.downloadList == nil || [source objectForKey:@"vuid"] == nil)
        return;
    
    for (LECVODDownloadItem *info in _downloadList)
        [info.vu isEqualToString:[source objectForKey:@"vuid"]]?[_sharedManager pauseDownloadWithVODItem:info]:nil;
    
    _sharedManager?[self notifyListEvent:_sharedManager]:nil;

}

RCT_EXPORT_METHOD(resume:(NSDictionary *)source
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    NSLog(@"外部控制——— 恢复下载:%@", source );
    if (source == nil || _sharedManager == nil || self.downloadList == nil || [source objectForKey:@"vuid"] == nil)
        return;
    
    for (LECVODDownloadItem *info in _downloadList)
        [info.vu isEqualToString:[source objectForKey:@"vuid"]]?[_sharedManager startDownloadWithVODItem:info]:nil;
    
    _sharedManager?[self notifyListEvent:_sharedManager]:nil;
}

RCT_EXPORT_METHOD(retry:(NSDictionary *)source
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    NSLog(@"外部控制——— 重新下载:%@", source );
    if (source == nil || _sharedManager == nil || self.downloadList == nil || [source objectForKey:@"vuid"] == nil)
        return;
    
    for (LECVODDownloadItem *info in _downloadList)
        [info.vu isEqualToString:[source objectForKey:@"vuid"]]?[_sharedManager startDownloadWithVODItem:info]:nil;
    
    _sharedManager?[self notifyListEvent:_sharedManager]:nil;
}

RCT_EXPORT_METHOD(delete:(NSDictionary *)source
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    NSLog(@"外部控制——— 删除下载:%@", source );
    if (source == nil || _sharedManager == nil || self.downloadList == nil || [source objectForKey:@"vuid"] == nil)
        return;
    
    for (LECVODDownloadItem *info in _downloadList)
        [info.vu isEqualToString:[source objectForKey:@"vuid"]]?[_sharedManager removeDownloadWithVODItem:info]:nil;
    
    _sharedManager?[self notifyListEvent:_sharedManager]:nil;
}

RCT_EXPORT_METHOD(clear:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    NSLog(@"外部控制——— 清空全部下载" );
    if ( _sharedManager == nil || self.downloadList == nil)
        return;
    
    for (LECVODDownloadItem *info in _downloadList)
        [_sharedManager removeDownloadWithVODItem:info];
    
    _sharedManager?[self notifyListEvent:_sharedManager]:nil;
}


- (void) notifyItemEvent:(int)eventType withDownloadItem:(LECVODDownloadItem *) vodDownloadItem andMsg:(NSString*) msg
{
    if (eventType == -1 || vodDownloadItem == nil) return;
    
    NSMutableDictionary* eventPara = [NSMutableDictionary dictionaryWithCapacity:19];
    [eventPara setValue:[NSNumber numberWithInt:eventType] forKey:@"eventType"];
    [eventPara setValue:vodDownloadItem.vu forKey:@"id"];
    [eventPara setValue:vodDownloadItem.videoName forKey:@"fileName"];
    [eventPara setValue:[NSNumber numberWithInt:vodDownloadItem.downloadedSize] forKey:@"progress"];
    [eventPara setValue:[NSNumber numberWithInt:vodDownloadItem.totalSize] forKey:@"fileLength"];
    [eventPara setValue:[NSNumber numberWithFloat:vodDownloadItem.percent] forKey:@"percent"];
    [eventPara setValue:[NSNumber numberWithFloat:vodDownloadItem.speed] forKey:@"speed"];
    [eventPara setValue:vodDownloadItem.rateType forKey:@"rateText"];
    [eventPara setValue:vodDownloadItem.uu forKey:@"uuid"];
    [eventPara setValue:vodDownloadItem.vu forKey:@"vuid"];
    [eventPara setValue:vodDownloadItem.playerOption.p forKey:@"businessline"];
    [eventPara setValue:[NSNumber numberWithBool:vodDownloadItem.isPanorama] forKey:@"pano"];
    [eventPara setValue:[NSNumber numberWithBool:vodDownloadItem.isInterrupted] forKey:@"isInterrupted"];
    [eventPara setValue:vodDownloadItem.payUserName forKey:@"payUserName"];
    [eventPara setValue:vodDownloadItem.payCheckCode forKey:@"payCheckCode"];
    [eventPara setValue:vodDownloadItem.errorCode forKey:@"errorCode"];
    [eventPara setValue:vodDownloadItem.errorDesc forKey:@"errorDesc"];
    [eventPara setValue:[NSNumber numberWithInt:vodDownloadItem.status] forKey:@"downloadState"];
    [eventPara setValue:vodDownloadItem.userInfo forKey:@"videoInfo"];
    [eventPara setValue:vodDownloadItem.errorDesc forKey:@"msg"];
    
    [self sendEventWithName:EVENT_DOWNLOAD_ITEM_UPDATE body:eventPara];
    
    NSLog(@"下载事件——— Item更新事件 event:%d info:%@", eventType, vodDownloadItem.videoName);
}


- (void) notifyListEvent:(LECVODDownloadManager *) downloadManager
{
    self.downloadList = downloadManager.vodItemsList;
    
    NSMutableArray *eventList = [NSMutableArray arrayWithCapacity:self.downloadList?self.downloadList.count:0];
    
    if (self.downloadList != nil && self.downloadList.count > 0) {
        for(LECVODDownloadItem *vodDownloadItem in self.downloadList){
            NSMutableDictionary* eventPara = [NSMutableDictionary dictionaryWithCapacity:19];
            [eventPara setValue:vodDownloadItem.vu forKey:@"id"];
            [eventPara setValue:vodDownloadItem.videoName forKey:@"fileName"];
            [eventPara setValue:[NSNumber numberWithInt:vodDownloadItem.downloadedSize] forKey:@"progress"];
            [eventPara setValue:[NSNumber numberWithInt:vodDownloadItem.totalSize] forKey:@"fileLength"];
            [eventPara setValue:[NSNumber numberWithFloat:vodDownloadItem.percent] forKey:@"percent"];
            [eventPara setValue:[NSNumber numberWithFloat:vodDownloadItem.speed] forKey:@"speed"];
            [eventPara setValue:vodDownloadItem.rateType forKey:@"rateText"];
            [eventPara setValue:vodDownloadItem.uu forKey:@"uuid"];
            [eventPara setValue:vodDownloadItem.vu forKey:@"vuid"];
            [eventPara setValue:vodDownloadItem.playerOption.p forKey:@"businessline"];
            [eventPara setValue:[NSNumber numberWithBool:vodDownloadItem.isPanorama] forKey:@"pano"];
            [eventPara setValue:[NSNumber numberWithBool:vodDownloadItem.isInterrupted] forKey:@"isInterrupted"];
            [eventPara setValue:vodDownloadItem.payUserName forKey:@"payUserName"];
            [eventPara setValue:vodDownloadItem.payCheckCode forKey:@"payCheckCode"];
            [eventPara setValue:vodDownloadItem.errorCode forKey:@"errorCode"];
            [eventPara setValue:vodDownloadItem.errorDesc forKey:@"errorDesc"];
            [eventPara setValue:[NSNumber numberWithInt:vodDownloadItem.status] forKey:@"downloadState"];
            [eventPara setValue:vodDownloadItem.userInfo forKey:@"videoInfo"];
            [eventPara setValue:vodDownloadItem.errorDesc forKey:@"msg"];
            
            [eventList addObject:eventPara];
        }
    }
    
    [self sendEventWithName:EVENT_DOWNLOAD_LIST_UPDATE body:eventList];
    
    NSLog(@"下载事件——— List更新事件 :%d",self.downloadList?self.downloadList.count:0);
}


//开始下载事件
- (void) vodDownloadManager:(LECVODDownloadManager *) downloadManager didBeginDownloadVODDownloadItem:(LECVODDownloadItem *) vodDownloadItem
{
    [self notifyItemEvent:EVENT_TYPE_START withDownloadItem:vodDownloadItem andMsg:nil];
    [self notifyListEvent:downloadManager];

    
}

//下载中的下载状态返回
- (void) vodDownloadManager:(LECVODDownloadManager *) downloadManager
 downloadingVODDownloadItem:(LECVODDownloadItem *) vodDownloadItem
            downloadedBytes:(long long)downloadedBytes
                 totalBytes:(long long)totalBytes
                      speed:(float)speed
{
    //    NSLog(@"downloaded: %lld / %lld", downloadedBytes, totalBytes);
    //    NSLog(@"speed: %f", speed);
    [self notifyItemEvent:EVENT_TYPE_PROGRESS withDownloadItem:vodDownloadItem andMsg:nil];
    [self notifyListEvent:downloadManager];
}


//完成下载事件
- (void) vodDownloadManager:(LECVODDownloadManager *) downloadManager didFinishDownloadVODDownloadItem:(LECVODDownloadItem *) vodDownloadItem
{
    [self notifyItemEvent:EVENT_TYPE_SUCCESS withDownloadItem:vodDownloadItem andMsg:nil];
    [self notifyListEvent:downloadManager];
}

//下载出错
- (void) vodDownloadManager:(LECVODDownloadManager *) downloadManager didFailDownloadVODDownloadItem:(LECVODDownloadItem *) vodDownloadItem
              withErrorCode:(NSString *) errorCode
              withErrorDesc:(NSString *) errorDesc
{
    [self notifyItemEvent:EVENT_TYPE_FAILED withDownloadItem:vodDownloadItem andMsg:errorDesc];
    [self notifyListEvent:downloadManager];
}


@end
