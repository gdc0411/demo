//
//  LECActivityLiveItem.h
//  LECLivePlayerSDK
//
//  Created by CC on 16/5/11.
//  Copyright © 2016年 CC. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LECActivityItem.h"

typedef NS_ENUM(NSInteger, LCActivityLiveStatus) {
    LCActivityLiveStatusUnknown = -1,
    LCActivityLiveStatusUnused = 0,
    LCActivityLiveStatusUsing = 1,
    LCActivityLiveStatusEnd = 3
};

@interface LECActivityLiveItem : NSObject

@property (nonatomic, assign) NSInteger livePositionNumber;
@property (nonatomic, assign) LCActivityLiveStatus status;
@property (nonatomic, strong) NSString *liveId;
@property (nonatomic, strong) NSString *previewStreamId;
@property (nonatomic, strong) NSString *previewRTMPUrl;
@property (nonatomic, assign, readonly) LECPlayerMediaType mediaType;

- (id) initWithDict:(NSDictionary *) dict;

@end
