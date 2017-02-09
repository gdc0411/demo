//
//  LECMobilePlayer.h
//  LECMobilePlayerSDK
//
//  Created by leeco_ma on 16/8/10.
//  Copyright © 2016年 leeco_ma. All rights reserved.
//

#import "LECPlayer.h"

@interface LECMobilePlayer : LECPlayer
/**
 *  移动直播注册接口
 *
 *  @param urlString  注册url
 *  @param completion completion
 *
 *  @return 注册是否成功
 */
- (BOOL) registerMobilePlayerWithURLString:(NSString *) urlString completion:(void (^)(BOOL result))completion;

@end
