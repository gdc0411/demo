//
//  LECLivingPlayer.h
//  LECPlayerSDK
//
//  Created by 侯迪 on 9/13/15.
//  Copyright (c) 2015 letv. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LECPlayer.h"
#import "LECGlobalDefine.h"

typedef void (^LECLivingPlayerGetCurrentServerTimeBlock) (NSInteger serverBaseTimestamp);

@interface LECLivingPlayer : LECPlayer

- (BOOL) registerWithLiveId:(NSString *) liveId
                  mediaType:(LECPlayerMediaType) mediaType
                 completion:(void (^)(BOOL result))completion;

- (BOOL) registerWithStreamId:(NSString *) streamId
                    mediaType:(LECPlayerMediaType) mediaType
                   completion:(void (^)(BOOL result))completion;

- (BOOL) registerWithLiveId:(NSString *) liveId
                  mediaType:(LECPlayerMediaType) mediaType
                isLetvMedia:(BOOL) isLetvMedia
                 options:(LECPlayerOption *)options//设置业务相关参数以及用户ID等,没有可以为nil
     resumeFromLastRateType:(BOOL) resumeFromLastRateType
                   completion:(void (^)(BOOL result))completion;

- (BOOL) registerWithStreamId:(NSString *) streamId
                    mediaType:(LECPlayerMediaType) mediaType
                  isLetvMedia:(BOOL) isLetvMedia
                   options:(LECPlayerOption *)options//设置业务相关参数以及用户ID等,没有可以为nil
       resumeFromLastRateType:(BOOL)resumeFromLastRateType
                   completion:(void (^)(BOOL result))completion ;

@property (nonatomic, assign) NSInteger relatedTime;            //对当前时间的相对时间，hls时宜时可用
@property (nonatomic, readonly) NSString *videoTitle;//视频标题，注册完成后，该值可获取
@property (nonatomic, readonly) NSString *loadingIconUrl;//启动加载图片的URL地址
//@property (nonatomic, assign,readonly) BOOL isPanorama;//是否是全景

/**
 *  获取直播流状态
 *  需要在注册成功之后才可获取
 *
 *  @return 直播流当前状态
 */
- (LECActivityStatus)livingStatus;

@end
