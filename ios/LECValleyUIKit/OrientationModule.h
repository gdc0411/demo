//
//  OrientationModule.h
//  DemoProject
//
//  Created by RaoJia on 2016/12/11.
//  Copyright © 2016年 LeCloud. All rights reserved.
//

#import <Foundation/Foundation.h>

//#import "RCTBridgeModule.h"
#import "RCTEventEmitter.h"


@interface OrientationModule : RCTEventEmitter <RCTBridgeModule>

+ (void)setOrientation: (UIInterfaceOrientationMask)orientation;
+ (UIInterfaceOrientationMask)getOrientation;

@end
