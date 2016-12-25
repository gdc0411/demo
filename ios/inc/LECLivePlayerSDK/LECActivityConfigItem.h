//
//  LECActivityConfigItem.h
//  LECLivePlayerSDK
//
//  Created by CC on 16/5/11.
//  Copyright © 2016年 CC. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LECGlobalDefine.h"



@interface LECActivityConfigItem : NSObject

@property (nonatomic, assign) LECWaterMarkPosition waterMarkPosition;
@property (nonatomic, strong) NSString *waterMarkUrl;
@property (nonatomic, assign) BOOL enableLogoDisplay;
@property (nonatomic, assign) BOOL enableShareOptionDisplay;
@property (nonatomic, assign) BOOL enableOnlineAudienceNumberDisplay;
@property (nonatomic, strong) NSString *logoUrl;
@property (nonatomic, assign) BOOL enableLoadingIcon;
@property (nonatomic, strong) NSString *loadingIconUrl;

- (id) initWithDict:(NSDictionary *) dict;

@end
