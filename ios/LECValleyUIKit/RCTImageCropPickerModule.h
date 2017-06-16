//
//  RCTImageCropPickerModule.h
//  LECValleyUIKit
//
//  Created by LizaRao on 2017/2/27.
//  Copyright © 2017年 LeCloud. All rights reserved.
//


#ifndef RN_IMAGE_CROP_PICKER_h
#define RN_IMAGE_CROP_PICKER_h

#import <Foundation/Foundation.h>

#if __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#import "RCTImageLoader.h"
#else
#import <React/RCTBridgeModule.h>
#import <React/RCTImageLoader.h>
#endif

#if __has_include("QBImagePicker.h")
#import "QBImagePicker.h"
#import "RSKImageCropper.h"
#else
#import "QBImagePicker/QBImagePicker.h"
#import "RSKImageCropper/RSKImageCropper.h"
#endif

#import "UIImage-Resize/UIImage+Resize.h"
#import "Compression.h"
#import <math.h>

@interface RCTImageCropPickerModule : NSObject<RCTBridgeModule,QBImagePickerControllerDelegate,RSKImageCropViewControllerDelegate,RSKImageCropViewControllerDataSource>

@property (nonatomic, strong) NSDictionary *defaultOptions;
@property (nonatomic, strong) Compression *compression;
@property (nonatomic, retain) NSMutableDictionary *options;
@property (nonatomic, strong) RCTPromiseResolveBlock resolve;
@property (nonatomic, strong) RCTPromiseRejectBlock reject;
@property BOOL cropOnly;

@end

#endif
