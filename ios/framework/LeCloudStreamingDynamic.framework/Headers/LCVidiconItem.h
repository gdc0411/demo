//
//  LCVidiconItem.h
//  LeCloudStreaming
//
//  Created by CC on 15/7/6.
//  Copyright (c) 2015年 Letv. All rights reserved.
//

#import <Foundation/Foundation.h>

/*
 摄像机位Item
 */
@interface LCVidiconItem : NSObject

@property (nonatomic,strong) NSString * machine;
@property (nonatomic,strong) NSString * liveId;
@property (nonatomic,strong) NSString * streamId;
@property (nonatomic,strong) NSString * pushUrl;
@property (nonatomic,assign) BOOL enable;

+(LCVidiconItem *)itemWithDictionary:(NSDictionary *)dict;

@end
