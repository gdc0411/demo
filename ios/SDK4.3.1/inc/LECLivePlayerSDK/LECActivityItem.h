//
//  LECActivityItem.h
//  LECLivePlayerSDK
//
//  Created by CC on 16/5/11.
//  Copyright © 2016年 CC. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LECGlobalDefine.h"


@interface LECActivityItem : NSObject

@property (nonatomic, strong) NSString *activityId;
@property (nonatomic, strong) NSString *activityName;
@property (nonatomic, strong) NSString *activityWebUrl;
@property (nonatomic, strong) NSString *activityDesc;
@property (nonatomic, strong) NSString *activityCoverImage;
@property (nonatomic, strong) NSArray *activityLiveItemList;    //ActivityLiveItem
@property (nonatomic, strong) NSString *ark;
@property (nonatomic, assign) LECActivityStatus status;
@property (nonatomic, assign) NSInteger beginTime;
@property (nonatomic, assign) NSInteger endTime;
@property (nonatomic, strong) NSString *playMode;
@property (nonatomic, assign) BOOL isPanorama;

- (id) initWithDict:(NSDictionary *) dict;
- (id) initWithStatusDict:(NSDictionary *) dict;

@end
