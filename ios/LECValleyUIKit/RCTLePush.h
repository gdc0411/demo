//
//  RCTLePush.h
//  LECValleyUIKit
//
//  Created by RaoJia on 2017/2/6.
//  Copyright © 2017年 LeCloud. All rights reserved.
//

#import <React/RCTView.h>

@class RCTBridge;
@class RCTEventDispatcher;

typedef NS_ENUM (int,LCPushType){
    PUSH_TYPE_MOBILE_URI = 0,
    PUSH_TYPE_MOBILE = 1,
    PUSH_TYPE_LECLOUD = 2,
    PUSH_TYPE_NONE = -1
};

@interface RCTLePush : UIView

- (instancetype)initWithBridge:(RCTBridge *)bridge NS_DESIGNATED_INITIALIZER;


@end
