//
//  RCTImagePickerModule.h
//  LECValleyUIKit
//
//  Created by RaoJia on 2017/2/18.
//  Copyright © 2017年 LeCloud. All rights reserved.
//

#import <React/RCTBridgeModule.h>
#import <UIKit/UIKit.h>

typedef NS_ENUM(NSInteger, RNImagePickerTarget) {
    RNImagePickerTargetCamera = 1,
    RNImagePickerTargetLibrarySingleImage,
};

@interface RCTImagePickerModule : NSObject <RCTBridgeModule, UINavigationControllerDelegate, UIActionSheetDelegate, UIImagePickerControllerDelegate>

@end
