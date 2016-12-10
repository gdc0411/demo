//
//  OrientationModule.h
//  DemoProject
//
//  Created by RaoJia on 2016/12/11.
//  Copyright © 2016年 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "RCTBridgeModule.h"

@interface OrientationModule : NSObject <RCTBridgeModule>

+ (void)setOrientation: (UIInterfaceOrientationMask)orientation;
+ (UIInterfaceOrientationMask)getOrientation;

@end
