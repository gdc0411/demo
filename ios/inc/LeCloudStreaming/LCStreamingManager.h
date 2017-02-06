//
//  LCStreamingManager.h
//  LeCloudStreaming
//
//  Modified by neareast on 16/9/6.
//  Copyright (c) 2016年 LeEco. All rights reserved.
//

/**
 * @file	LCStreamingManager.h
 * @brief	推流SDK头文件（无皮肤版本的）.
 * @author	leCloud_iOSTeam
 * @version	2.0
 * @date	2016-09-06
 */


#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "LCVidiconItem.h"

typedef NS_ENUM(NSInteger, LCStreamingSessionState) {
    LCStreamingSessionStateNone,
    LCStreamingSessionStatePreviewStarted,
    LCStreamingSessionStateStarting,
    LCStreamingSessionStateStarted,
    LCStreamingSessionStateEnded,
    LCStreamingSessionStateError
};

typedef NS_ENUM(NSInteger, LCStreamingManagerStatus) {
    LCStreamingManagerStatusAudioPemission,
    LCStreamingManagerStatusCameraPemission,
    LCStreamingManagerStatusWarning,
    LCStreamingManagerStatusError,
    LCStreamingManagerStatusWeakNetwork
};

typedef NS_ENUM(NSInteger, LCCamareOrientationState) {
    LCCamareOrientationStateFront,
    LCCamareOrientationStateBack
};

typedef NS_ENUM(NSInteger, LCVideoFilter) {
    LCVideoFilterNone,
    LCVideoFilterBeautyFace,     //美颜滤镜
    LCVideoFilterWarm,           //温暖滤镜
    LCVideoFilterCalm,           //平静滤镜
    LCVideoFilterRomantic        //浪漫滤镜
};


@protocol LCStreamingManagerDelegate <NSObject>

@required
//推流状态变化通知
- (void)connectionStatusChanged:(LCStreamingSessionState)sessionState;

@optional
//推流管理器状态通知，主要用于错误信息的通知
- (void)notifyManagerStatus:(LCStreamingManagerStatus)managerStatus withMessage:(NSString *)msg;
//视频帧处理回调，参数为原始视频帧，需返回处理后的视频帧
- (CVPixelBufferRef)newPixelBufferFromPixelBuffer:(const CVPixelBufferRef)pixelBuffer;
@end


/*!
 *  audio data callback where you can apply custom processes on
 *
 *  @param data         audio data; token from ioData->mBuffers[0].mData in corresponding AURenderCallback
 *  @param size         audio size, token from ioData->mBuffers[0].mDataByteSize in corresponding AURenderCallback
 *  @param numberFrames The number of sample frames that will be represented in the audio data in the provided data parameter.
 */
typedef void(^AudioProcessBlock)(const void* const data, size_t size, int inNumberFrames);

/// 推流SDK头文件.
@interface LCStreamingManager : NSObject
/*! 完整版本号信息 */
@property (nonatomic, readonly) NSString *fullVersionString;
/*! 委托 */
@property (nonatomic, weak) id<LCStreamingManagerDelegate> delegate;
/*! !important 此属性设置已废弃，默认锁定方向 */
@property (nonatomic, assign, readonly) BOOL lockOrientation;
/*! 推流视频的正方向 */
@property (nonatomic, assign) UIInterfaceOrientation pushOrientation;

/*! 设置视频的大小（下次推流时生效）
 建议设置成常用的标准分辨率，这样播放器支持的也比较好，例如：
 普屏4:3  320*240 640*480
 宽屏16:9  480*272 640*360 672*378 720*480 1024*600 1280*720等
 */
@property (nonatomic, assign) CGSize videoSize;
/*! 自定义音频处理回调，需要在LCStreamingManager初始化完成之后再设置，参数含义详见typedef的注释 */
@property (nonatomic, copy) AudioProcessBlock audioProcessBlock;

/*!
 音量增益，大于0时生效，大于1时声音相对于原始声音音量增大；未初始化时返回-1
 增益需要在LCStreamingManager初始化完成之后再设置
 可以选择在connectionStatusChanged回调的LCStreamingSessionStateStarted状态来设置
 */
@property (nonatomic, assign) CGFloat gain;

/*! 对焦(0,0) is top-left, (1,1) is bottom-right */
@property (nonatomic, assign) CGPoint focusPoint;

/*! 摄像头取景的放大倍数；默认为1.0 */
@property(nonatomic, assign) CGFloat zoomFactor;

/*! 启用手动对焦，默认未启用 */
- (void)enableManulFocus:(BOOL)enable;

/*!
 创建Manager对象
 */
+ (LCStreamingManager *)sharedManager;

/*!
 获取机位信息
 aId为活动ID
 items内对象为LCVidiconItem
 */
- (void)requestVidiconInfoWithID:(NSString *)aId
                          userId:(NSString *)userId
                       secretKey:(NSString *)secretKey
                       completed:(void (^) (BOOL isSuccess,NSArray * items,NSString *errorCode,NSString *errorMsg))block;

/*!
 获得在线用户信息
 */
- (BOOL)startPollOnlineUserNumberWithGotOnlineAudienceNumber:(void (^)(long userNumber)) success;

/*!
 *  停止刷新在线人数
 */
- (void)stopPollOnlineUserNumber;

/*!
 *  创建推流的视频采集Session
 *
 *  @param size                    源视频的分辨率，建议设置成常用的标准分辨率
 *  @param frameRate               推流视频的帧率
 *  @param bitrate                 推流的视频码率
 *  @param useInterfaceOrientation 是否使用用户界面的方向作为视频信息采集的方向，否则将使用硬件方向
 */
- (void)configVCSessionWithVideoSize:(CGSize)size
                           frameRate:(int)frameRate
                             bitrate:(int)bitrate
             useInterfaceOrientation:(BOOL)useInterfaceOrientation;

/*!
 获取视频采集的视图
 */
- (UIView *)videoView;

/*!
 当前视频预览图
 */
- (UIImage *)currentCameraPreviewImage;

/*!
 设置预览视图的Frame
 */
- (void)configVideoViewFrame:(CGRect)frame;

/*!
 * 开始推流
 *
 * @param item  机位信息
 */
- (void)startStreamingWithLCVidiconItem:(LCVidiconItem *)item;

/*!
 * 使用rtmp地址开始推流
 *
 * @param urlString     推流使用的rtmp地址
 */
- (void)startStreamingWithRtmpAdress:(NSString *)urlString;

/*!
 结束推流
 */
- (void)stopStreaming;

/*!
 清理session，在退出推流界面的时候需要调用来释放资源
 */
- (void)cleanSession;

/*!
 * 设置闪光灯
 *
 * @param open  是否开启；默认不开启闪光灯
 */
- (void)setTorchOpenState:(BOOL)open;
- (BOOL)torchOpenState;

/*!
 * 设置静音
 *
 * @param mute  是否静音；默认不静音
 */
- (void)setMute:(BOOL)mute;

/*!
 * 设置镜头方向
 */
- (void)setCamareOrientationState:(LCCamareOrientationState)state;

/*!
 * 设置滤镜
 */
- (void)setFilter:(LCVideoFilter)filter;

/*!
 * 开启debug日志，便于查找问题
 * @param enable 是否开启debug日志，默认不开启
 */
- (void)enableDebugLog:(BOOL)enable;

/*!
 * 开启自动上传推流相关日志，便于查找问题；开启之后日志记录到达1M时将自动上传到乐视云的服务器
 * @param enable 是否开启自动上传日志，默认不开启
 */
- (void)enableAutoUploadLog:(BOOL)enable;

/*!
 * 开启前置摄像头镜像
 * @param enable 是否开启前置摄像头镜像；默认为开启
 */
- (void)enableFrontCameraMirror:(BOOL)enable;

/*!
 * 手动上传推流相关日志到乐视云的服务器，便于分析问题
 * @param dataDic 开发者自定义参数，一般传nil即可
 * @param block 回调函数，参数中包括当前应用的唯一标示uuid，便于查找日志
 */
- (void)logFileReportWithData:(NSDictionary *)dataDic completionBlock:(void (^)(BOOL success, NSString *uuid, NSError *error))block;

@end
