//
//  LECLivePlayInfoItem.h
//  LECLivePlayerSDK
//
//  Created by CC on 16/5/11.
//  Copyright © 2016年 CC. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LECActivityItem.h"

@interface LECLivePlayInfoItem : NSObject


@property (nonatomic, assign) BOOL supportTimeShift;
@property (nonatomic, strong) NSDate *liveBeginDate;
@property (nonatomic, strong) NSString *liveId;
@property (nonatomic, strong) NSArray *streamItemsList;     //LCLiveStreamPlayInfoItem
@property (nonatomic, strong) NSString *videoTitle;
@property (nonatomic, strong) NSString *userId;
@property (nonatomic, assign) long long baseServerTimestamp;
@property (nonatomic, strong) NSString * p;//业务ID
@property (nonatomic, assign) LECActivityStatus status;//时控新增属性,标识直播流状态
@property (nonatomic, assign, readonly) LECPlayerMediaType mediaType;

- (id) initWithLivePlayInfoDict:(NSDictionary *) dict;


@end
