/*************************************************************************
 * Description: 乐视直播推流组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2017-2-5
 ************************************************************************/
package com.lecloud.valley.leecoSdk;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.lecloud.valley.common.Events;
import com.lecloud.valley.utils.LogUtils;
import com.letv.recorder.bean.AudioParams;
import com.letv.recorder.bean.CameraParams;
import com.letv.recorder.bean.LivesInfo;
import com.letv.recorder.callback.ISurfaceCreatedListener;
import com.letv.recorder.callback.LetvRecorderCallback;
import com.letv.recorder.callback.PublishListener;
import com.letv.recorder.controller.CameraSurfaceView;
import com.letv.recorder.controller.LetvPublisher;
import com.letv.recorder.controller.Publisher;
import com.letv.recorder.ui.logic.RecorderConstance;
import com.letv.recorder.util.MD5Utls;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.lecloud.valley.common.Constants.EVENT_PROP_ERROR_CODE;
import static com.lecloud.valley.common.Constants.EVENT_PROP_ERROR_MSG;
import static com.lecloud.valley.common.Constants.EVENT_PROP_PUSH_CAMERA_DIRECTION;
import static com.lecloud.valley.common.Constants.EVENT_PROP_PUSH_CAMERA_FLAG;
import static com.lecloud.valley.common.Constants.EVENT_PROP_PUSH_FLASH_FLAG;
import static com.lecloud.valley.common.Constants.EVENT_PROP_PUSH_PLAY_URL;
import static com.lecloud.valley.common.Constants.EVENT_PROP_PUSH_PUSH_URL;
import static com.lecloud.valley.common.Constants.EVENT_PROP_PUSH_STATE;
import static com.lecloud.valley.common.Constants.EVENT_PROP_PUSH_TIME;
import static com.lecloud.valley.common.Constants.EVENT_PROP_PUSH_TIME_FLAG;
import static com.lecloud.valley.common.Constants.PROP_PUSH_PARA;
import static com.lecloud.valley.common.Constants.PROP_PUSH_TYPE;
import static com.lecloud.valley.common.Constants.PUSH_TYPE_LECLOUD;
import static com.lecloud.valley.common.Constants.PUSH_TYPE_MOBILE;
import static com.lecloud.valley.common.Constants.PUSH_TYPE_MOBILE_URI;
import static com.lecloud.valley.utils.LogUtils.TAG;
import static com.lecloud.valley.utils.LogUtils.getTraceInfo;

/**
 * Created by RaoJia on 2017/2/5.
 */

public class LeReactPushView extends CameraSurfaceView implements ISurfaceCreatedListener {

    private ThemedReactContext mThemedReactContext;
    private RCTEventEmitter mEventEmitter;

    private Publisher mPublisher; //移动直播
    private LetvPublisher mLECPublisher; //乐视云

    private CameraParams cameraParams;
    private AudioParams audioParams;

    private int mPushType;  //当前推流类型
    private boolean mLePushValid; //是否初始化成功
    private boolean mPushFlag = false;  //是否开始推流
    private int mPushState = 0;  //PUSH操作状态：0：closed，1：opening，2：opened，3:closing, 4:error

    // 定义PUSH状态常量
    private static final int PUSH_STATE_CLOSED = 0;
    private static final int PUSH_STATE_CONNECTING = 1;
    private static final int PUSH_STATE_CONNECTED = 2;
    private static final int PUSH_STATE_OPENED = 3;
    private static final int PUSH_STATE_DISCONNECTING = 4;
    private static final int PUSH_STATE_ERROR = 5;
    private static final int PUSH_STATE_WARNING = 6;

    private Bundle mPushPara; //推流参数

    private String mPushUrl; //推流地址
    private String mPlayUrl; //播放地址

    private int mTime = 0; //推流计时
    private boolean mTimeFlag = false; //推流计时是否开始

    boolean mFlashFlag = false; //是否打开闪光灯
    boolean mSwitchFlag = false; //是否正在切换摄像

    private boolean isBack = false;//后台标志,在进入后台之前正在推流设置为true。判断是否在后台回来时继续推流


/*============================= 推流View构造 ===================================*/

    public LeReactPushView(ThemedReactContext context) {
        super(context);
        mThemedReactContext = context;
        mEventEmitter = mThemedReactContext.getJSModule(RCTEventEmitter.class);

        ((Activity) mThemedReactContext.getBaseContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        ((Activity) mThemedReactContext.getBaseContext()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void initMobilePush(ThemedReactContext context, boolean isLandscape) {

        mPublisher = Publisher.getInstance();
        mPublisher.initPublisher((Activity) (context.getBaseContext()));
        mPublisher.getRecorderContext().setUseLanscape(isLandscape);//告诉推流器使用横屏推流还是竖屏推流
        cameraParams = mPublisher.getCameraParams();
        audioParams = mPublisher.getAudioParams();

        //设置推流状态监听器
        mPublisher.setPublishListener(listener);

        //绑定Camera显示View,要求必须是CameraSurfaceView
        mPublisher.getVideoRecordDevice().bindingGLView(this);

        //设置CameraSurfaceView 监听器,当CameraSurfaceView 创建成功的时候回回调onGLSurfaceCreatedListener,这个时候才能开启摄像头
        mPublisher.getVideoRecordDevice().setSurfaceCreatedListener(this);

        //////////////////以下设置必须在在推流之前设置,也可以不设置////////////////////////////////////////
        if (isLandscape) {//设置流分辨率。要求宽度必须是16的整倍数,高度没有要求
            cameraParams.setWidth(640);
            cameraParams.setHeight(368);
        } else {
            cameraParams.setWidth(368);
            cameraParams.setHeight(640);
        }

        cameraParams.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT); //开启默认前置摄像头
        cameraParams.setVideoBitrate(1000 * 1000); //设置码率
        audioParams.setEnableVolumeGain(true);//开启音量调节,注意,这一点会影响性能,如果没有必要,设置为false
        cameraParams.setFocusOnTouch(false);//关闭对焦功能
        cameraParams.setFocusOnAnimation(false);//关闭对焦动画
        cameraParams.setOpenGestureZoom(true);//开启拉近拉远手势
        cameraParams.setFrontCameraMirror(true);//开启镜像
        mPublisher.getVideoRecordDevice().setFocusView(new View(getContext()));//设置对焦图片。如果需要对焦功能和对焦动画,请打开上边两个设置,并且在这里传入一个合适的View
        mPublisher.getRecorderContext().setAutoUpdateLogFile(false); //是否开启日志文件自动上报

        /////////////////////////////////////////////////////////////////////////////////////////////
    }

    public void initLeCloudPush(ThemedReactContext context, String activityId, String userId, String secretKey, boolean isLandscape) {

        LetvPublisher.init(activityId, userId, secretKey);

        mLECPublisher = LetvPublisher.getInstance();
        mLECPublisher.initPublisher((Activity) (context.getBaseContext()));
        mLECPublisher.getRecorderContext().setUseLanscape(isLandscape);//告诉推流器使用横屏推流还是竖屏推流
        cameraParams = mLECPublisher.getCameraParams();
        audioParams = mLECPublisher.getAudioParams();
        mLECPublisher.setPublishListener(listener);//设置推流状态监听器
        //绑定Camera显示View,要求必须是CameraSurfaceView
        mLECPublisher.getVideoRecordDevice().bindingGLView(this);
        //设置CameraSurfaceView 监听器,当CameraSurfaceView 创建成功的时候回回调onGLSurfaceCreatedListener,这个时候才能开启摄像头
        mLECPublisher.getVideoRecordDevice().setSurfaceCreatedListener(this);
        //////////////////以下设置必须在在推流之前设置,也可以不设置////////////////////////////////////////
        if (isLandscape) {//设置流分辨率。要求宽度必须是16的整倍数,高度没有要求
            cameraParams.setWidth(640);
            cameraParams.setHeight(368);
        } else {
            cameraParams.setWidth(368);
            cameraParams.setHeight(640);
        }
        cameraParams.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT); //开启默认前置摄像头
        cameraParams.setVideoBitrate(1000 * 1000); //设置码率
        audioParams.setEnableVolumeGain(true);//开启音量调节,注意,这一点会影响性能,如果没有必要,设置为false
        cameraParams.setFocusOnTouch(false);//关闭对焦功能
        cameraParams.setFocusOnAnimation(false);//关闭对焦动画
        cameraParams.setOpenGestureZoom(true);//开启拉近拉远手势
        cameraParams.setFrontCameraMirror(true);//开启镜像
        mLECPublisher.getVideoRecordDevice().setFocusView(new View(getContext()));//设置对焦图片。如果需要对焦功能和对焦动画,请打开上边两个设置,并且在这里传入一个合适的View
        /////////////////////////////////////////////////////////////////////////////////////////////
    }

/*============================= 外部接口 ===================================*/

    /**
     * 设置推流参数
     *
     * @param bundle 推流参数
     */
    protected void setTarget(final Bundle bundle) {
        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 推流参数 bundle:" + bundle);

        mPushPara = bundle;

        // 推流类型切换
        int newPushType = mPushPara.getInt(PROP_PUSH_TYPE);

        mPushType = newPushType;

        boolean isLandscape = mPushPara.containsKey("landscape") && mPushPara.getBoolean("landscape");

        //初始化状态变量
        initFieldParaStates();

        switch (mPushType) {
            case PUSH_TYPE_MOBILE_URI:
                initMobilePush(mThemedReactContext, isLandscape);
                mPushUrl = mPlayUrl = mPushPara.containsKey("url") ? mPushPara.getString("url") : "";
                break;

            case PUSH_TYPE_MOBILE:
                initMobilePush(mThemedReactContext, isLandscape);
                mPushUrl = createStreamUrl(true);
                mPlayUrl = createStreamUrl(false);
                break;

            case PUSH_TYPE_LECLOUD:
                String activityId = mPushPara.containsKey("activityId") ? mPushPara.getString("activityId") : "";
                String userId = mPushPara.containsKey("userId") ? mPushPara.getString("userId") : "";
                String secretKey = mPushPara.containsKey("secretKey") ? mPushPara.getString("secretKey") : "";

                initLeCloudPush(mThemedReactContext, activityId, userId, secretKey, isLandscape);
                break;
        }

        WritableMap event = Arguments.createMap();
        event.putString(PROP_PUSH_PARA, bundle.toString());

        event.putString(EVENT_PROP_PUSH_PUSH_URL, mPushUrl);
        event.putString(EVENT_PROP_PUSH_PLAY_URL, mPlayUrl);

        mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_LOAD_TARGET.toString(), event);
    }

    /**
     * **移动直播 **
     * 生成一个 推流地址/播放地址 。这两个地址生成规则特别像
     *
     * @param isPush 当前需要生成的是推流地址还是播放地址，true 推流地址 ，false 播放地址
     * @return 返回生成的地址
     */
    private String createStreamUrl(boolean isPush) {

        // 格式化，获取时间参数 。注意，当你在创建移动直播应用时，如果开启推流防盗链或播放防盗链 。那么你必须保证这个时间和中国网络时间一致
        String tm = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        // 获取无推流地址时 流名称，推流域名，签名密钥 三个参数
        String streamName = mPushPara.containsKey("streamName") ? mPushPara.getString("streamName") : "";
        String domainName = mPushPara.containsKey("domainName") ? mPushPara.getString("domainName") : "";
        String appkey = mPushPara.containsKey("appkey") ? mPushPara.getString("appkey") : "";

        // 生成 sign值,在播放和推流时生成的sign值不一样
        String sign;
        if (isPush) {
            // 生成推流的 sign 值 。把流名称 ，时间，签名密钥 通过MD5 算法加密
            sign = MD5Utls.stringToMD5(streamName + tm + appkey);
        } else {
            // 生成播放 的sign 值，把流名称，时间，签名密钥，和"lecloud" 通过MD5 算法加密
            sign = MD5Utls.stringToMD5(streamName + tm + appkey + "lecloud");
            // 获取到播放域名。现在播放域名的获取规则是 把推流域名中的 push 替换为pull
            domainName = domainName.replaceAll("push", "pull");
        }
        // 拼接出一个rtmp 的地址
        return "rtmp://" + domainName + "/live/" + streamName + "?tm=" + tm + "&sign=" + sign;
    }

    private void initFieldParaStates() {
        mLePushValid = false;

        mTime = 0;
        mPushFlag = false;
        mPushState = PUSH_STATE_CLOSED;
        mFlashFlag = false;
        mSwitchFlag = false;
    }


    /**
     * 开始/停止推流
     *
     * @param push 是否推流
     */
    public void setPush(final boolean push) {
        if (!mLePushValid || mPushFlag == push || mPushPara == null) {
            return;
        }

        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 开始/停止推流:" + push);
        setPushedModifier(push);
    }

    /**
     * 设置推送开始或停止
     *
     * @param pushed pushed
     */
    private void setPushedModifier(final boolean pushed) {
        mPushFlag = pushed;

        if (!mLePushValid) {
            return;
        }

        if (mPushFlag) { //启动推流
            if (mPushType == PUSH_TYPE_MOBILE_URI || mPushType == PUSH_TYPE_MOBILE) { //移动直播
                if (!mPublisher.isRecording() && mLePushValid) { //未初始化完毕，未推送状态，开启推送
                    Log.d(TAG, getTraceInfo() + "初始化完毕，开始推流！");
                    mTime = 0;
                    mPublisher.setUrl(mPushUrl);//设置推流地址
                    mPublisher.publish();//在摄像头打开以后才能开始推流

                    WritableMap event = Arguments.createMap();
                    event.putInt(EVENT_PROP_PUSH_STATE, PUSH_STATE_CONNECTING);
                    event.putInt(EVENT_PROP_ERROR_CODE, 0);
                    event.putString(EVENT_PROP_ERROR_MSG, "初始化完毕，开始推流");
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_STATE_UPDATE.toString(), event);

                } else if (mPublisher.isRecording()) { //正在推送，不做处理
                    Log.d(TAG, getTraceInfo() + "无需重复推流！");

                    WritableMap event = Arguments.createMap();
                    event.putInt(EVENT_PROP_PUSH_STATE, PUSH_STATE_OPENED);
                    event.putInt(EVENT_PROP_ERROR_CODE, 0);
                    event.putString(EVENT_PROP_ERROR_MSG, "无需重复推流");
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_STATE_UPDATE.toString(), event);

                } else if (!mLePushValid) {
                    Log.d(TAG, getTraceInfo() + "初始化未完成，无法推流！");

                    WritableMap event = Arguments.createMap();
                    event.putInt(EVENT_PROP_PUSH_STATE, PUSH_STATE_ERROR);
                    event.putInt(EVENT_PROP_ERROR_CODE, -1);
                    event.putString(EVENT_PROP_ERROR_MSG, "初始化未完成，无法推流");
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_STATE_UPDATE.toString(), event);
                    //todo 增加处理
                }

            } else if (mPushType == PUSH_TYPE_LECLOUD) {

                if (!mLECPublisher.isRecording() && mLePushValid) { //未初始化完毕，未推送状态，开启推送
                    Log.d(TAG, getTraceInfo() + "初始化完毕，开始推流！");
                    mTime = 0;
                    mLECPublisher.handleMachine(callback);

                    WritableMap event = Arguments.createMap();
                    event.putInt(EVENT_PROP_PUSH_STATE, PUSH_STATE_CONNECTING);
                    event.putInt(EVENT_PROP_ERROR_CODE, 0);
                    event.putString(EVENT_PROP_ERROR_MSG, "初始化完毕，开始推流");
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_STATE_UPDATE.toString(), event);

                } else if (mPublisher.isRecording()) { //正在推送，不做处理
                    Log.d(TAG, getTraceInfo() + "无需重复推流！");

                    WritableMap event = Arguments.createMap();
                    event.putInt(EVENT_PROP_PUSH_STATE, PUSH_STATE_OPENED);
                    event.putInt(EVENT_PROP_ERROR_CODE, 0);
                    event.putString(EVENT_PROP_ERROR_MSG, "无需重复推流");
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_STATE_UPDATE.toString(), event);

                } else if (!mLePushValid) {
                    Log.d(TAG, getTraceInfo() + "初始化未完成，无法推流！");

                    WritableMap event = Arguments.createMap();
                    event.putInt(EVENT_PROP_PUSH_STATE, PUSH_STATE_ERROR);
                    event.putInt(EVENT_PROP_ERROR_CODE, -1);
                    event.putString(EVENT_PROP_ERROR_MSG, "初始化未完成，无法推流");
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_STATE_UPDATE.toString(), event);
                    //todo 增加处理
                }
            }

        } else { //关闭推流

            if (mPushType == PUSH_TYPE_MOBILE_URI || mPushType == PUSH_TYPE_MOBILE) { //移动直播

                if (mPublisher.isRecording()) { //正在推流，停止推流
                    Log.d(TAG, getTraceInfo() + "结束当前推流！");
                    mPublisher.stopPublish();//结束推流

                    WritableMap event = Arguments.createMap();
                    event.putInt(EVENT_PROP_PUSH_STATE, PUSH_STATE_DISCONNECTING);
                    event.putInt(EVENT_PROP_ERROR_CODE, 0);
                    event.putString(EVENT_PROP_ERROR_MSG, "结束当前推流");
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_STATE_UPDATE.toString(), event);

                } else { //未推送，不做处理
                    Log.d(TAG, getTraceInfo() + "无推流，无需关闭！");

                    WritableMap event = Arguments.createMap();
                    event.putInt(EVENT_PROP_PUSH_STATE, PUSH_STATE_CLOSED);
                    event.putInt(EVENT_PROP_ERROR_CODE, 0);
                    event.putString(EVENT_PROP_ERROR_MSG, "无需重复推流");
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_STATE_UPDATE.toString(), event);
                }

            } else if (mPushType == PUSH_TYPE_LECLOUD) {

                if (mLECPublisher.isRecording()) { //正在推流，停止推流
                    Log.d(TAG, getTraceInfo() + "结束当前推流！");
                    mLECPublisher.stopPublish();//结束推流

                    WritableMap event = Arguments.createMap();
                    event.putInt(EVENT_PROP_PUSH_STATE, PUSH_STATE_DISCONNECTING);
                    event.putInt(EVENT_PROP_ERROR_CODE, 0);
                    event.putString(EVENT_PROP_ERROR_MSG, "结束当前推流");
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_STATE_UPDATE.toString(), event);

                } else { //未推送，不做处理
                    Log.d(TAG, getTraceInfo() + "无推流，无需关闭！");

                    WritableMap event = Arguments.createMap();
                    event.putInt(EVENT_PROP_PUSH_STATE, PUSH_STATE_CLOSED);
                    event.putInt(EVENT_PROP_ERROR_CODE, 0);
                    event.putString(EVENT_PROP_ERROR_MSG, "无需重复推流");
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_STATE_UPDATE.toString(), event);
                }
            }
        }
    }

    /**
     * 切换摄像头,需要注意,切换摄像头不能太频繁,如果太频繁会导致应用程序崩溃。建议最快10秒一次
     */
    private Handler tenHandler = new Handler();

    public void setCamera(final int times) {
        if (!mLePushValid || times == 0) {
            return;
        }
        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 切换摄像头方向:" + times);

        WritableMap event = Arguments.createMap();

        if (mSwitchFlag) {
//            Toast.makeText(getContext(), "切换摄像头不能太频繁,等待10秒后在切换吧", Toast.LENGTH_SHORT).show();
            event.putBoolean(EVENT_PROP_PUSH_CAMERA_FLAG, true);
            event.putInt(EVENT_PROP_PUSH_CAMERA_DIRECTION, cameraParams.getCameraId());
            event.putString(EVENT_PROP_ERROR_MSG, "切换摄像头不能太频繁,等待10秒后在切换吧");
            mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_CAMERA_UPDATE.toString(), event);

            return;
        }
        tenHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(getContext(), "十秒已过,可以继续切换摄像头", Toast.LENGTH_SHORT).show();
                mSwitchFlag = false;

                WritableMap camera = Arguments.createMap();
                camera.putBoolean(EVENT_PROP_PUSH_CAMERA_FLAG, mSwitchFlag);
                camera.putInt(EVENT_PROP_PUSH_CAMERA_DIRECTION, cameraParams.getCameraId());
                camera.putInt(EVENT_PROP_ERROR_CODE, 0);
                camera.putString(EVENT_PROP_ERROR_MSG, "十秒已过,可以继续切换摄像头");
                mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_CAMERA_UPDATE.toString(), camera);

            }
        }, 10 * 1000);

        mSwitchFlag = true;
        int cameraID;
        if (cameraParams.getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {

            cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;

        } else {

            cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
            if (mFlashFlag) {//切换前置摄像头会自动关闭闪光灯
                mFlashFlag = false;

                WritableMap flash = Arguments.createMap();
                flash.putBoolean(EVENT_PROP_PUSH_FLASH_FLAG, mFlashFlag);
                flash.putInt(EVENT_PROP_ERROR_CODE, 0);
                flash.putString(EVENT_PROP_ERROR_MSG, "前置摄像头不能打开闪关灯");
                mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_FLASH_UPDATE.toString(), flash);
            }
        }
        if (mPushType == PUSH_TYPE_MOBILE_URI || mPushType == PUSH_TYPE_MOBILE) { //移动直播
            mPublisher.getVideoRecordDevice().switchCamera(cameraID);//切换摄像头
        } else if (mPushType == PUSH_TYPE_LECLOUD) { //云直播
            mLECPublisher.getVideoRecordDevice().switchCamera(cameraID);//切换摄像头
        }

        event.putInt(EVENT_PROP_PUSH_CAMERA_DIRECTION, cameraID);
        event.putBoolean(EVENT_PROP_PUSH_CAMERA_FLAG, mSwitchFlag);
        event.putInt(EVENT_PROP_ERROR_CODE, 0);
        event.putString(EVENT_PROP_ERROR_MSG, "摄像头切换完毕");
        mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_CAMERA_UPDATE.toString(), event);
    }

    /**
     * 开启闪光灯
     */
    public void setFlash(final boolean flash) {
        if (!mLePushValid || mFlashFlag == flash || mPushPara == null) {
            return;
        }
        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 开始/关闭闪光灯:" + flash);

        WritableMap event = Arguments.createMap();
        if (cameraParams.getCameraId() != Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mFlashFlag = flash;

            if (mPushType == PUSH_TYPE_MOBILE_URI || mPushType == PUSH_TYPE_MOBILE) { //移动直播
                mPublisher.getVideoRecordDevice().setFlashFlag(mFlashFlag);
            } else if (mPushType == PUSH_TYPE_LECLOUD) { //云直播
                mLECPublisher.getVideoRecordDevice().setFlashFlag(mFlashFlag);
            }

            if (mFlashFlag)
                event.putString(EVENT_PROP_ERROR_MSG, "闪关灯已打开");
            else
                event.putString(EVENT_PROP_ERROR_MSG, "闪光灯已关闭");

        } else {
//            Toast.makeText(getContext(), "前置摄像头不能打开闪关灯", Toast.LENGTH_LONG).show();
            event.putString(EVENT_PROP_ERROR_MSG, "前置摄像头不能打开闪关灯");
        }

        event.putBoolean(EVENT_PROP_PUSH_FLASH_FLAG, mFlashFlag);
        event.putInt(EVENT_PROP_ERROR_CODE, 0);
        mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_FLASH_UPDATE.toString(), event);
    }

    /**
     * 切换滤镜,设置为0为关闭滤镜
     */
    int model = CameraParams.FILTER_VIDEO_NONE;//无效果

    public void switchFilter() {
        if (model == CameraParams.FILTER_VIDEO_NONE) {
            model = CameraParams.FILTER_VIDEO_DEFAULT; //默认的美颜效果
        } else {
            model = CameraParams.FILTER_VIDEO_NONE;//无效果
        }
        if (mPushType == PUSH_TYPE_MOBILE_URI || mPushType == PUSH_TYPE_MOBILE) { //移动直播
            mPublisher.getVideoRecordDevice().setFilterModel(model);//切换滤镜
        } else if (mPushType == PUSH_TYPE_LECLOUD) { //云直播
            mLECPublisher.getVideoRecordDevice().setFilterModel(model);//切换滤镜
        }

    }

    /**
     * 设置声音大小,必须对setEnableVolumeGain设置为true
     *
     * @param volume 0-1为缩小音量,1为正常音量,大于1为放大音量
     */
    int volume = 1;

    public void setVolume() {
        if (volume == 1) {
            volume = 0;
        } else {
            volume = 1;
        }
        if (mPushType == PUSH_TYPE_MOBILE_URI || mPushType == PUSH_TYPE_MOBILE) { //移动直播
            mPublisher.setVolumeGain(volume);//设置声音大小
        } else if (mPushType == PUSH_TYPE_LECLOUD) { //云直播
            mLECPublisher.setVolumeGain(volume);//设置声音大小
        }
    }

//    /**
//     * ZOOM 操作.必须保证摄像头已经成功打开
//     * @return
//     */
//    private void getZoom(){
//         mPublisher.getVideoRecordDevice().getZoom();
//        mPublisher.getVideoRecordDevice().setZoom(1);
//        mPublisher.getVideoRecordDevice().getMaxZoom();
//    }

    /**
     * 出现问题了,主动上报日志文件
     */
    public void sendErrorLogFile() {
        mPublisher.sendLogFile(new LetvRecorderCallback() {
            @Override
            public void onFailed(int code, String msg) {

            }

            @Override
            public void onSucess(Object data) {

            }
        });
    }


/*============================= 事件回调处理 ===================================*/

    private PublishListener listener = new PublishListener() {
        @Override
        public void onPublish(int code, String msg, Object... obj) {
            Message message = mHandler.obtainMessage(code);
            message.obj = msg;
            mHandler.sendMessage(message);
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {

            WritableMap event = Arguments.createMap();

            switch (msg.what) {
                case RecorderConstance.RECORDER_OPEN_URL_SUCESS:
                    Log.d(TAG, "推流连接成功:只有当连接成功以后才能开始推流");
//                    Toast.makeText(getContext(), "推流连接成功:只有当连接成功以后才能开始推流", Toast.LENGTH_SHORT).show();
//                    timeView.setText("连接成功");

                    event.putInt(EVENT_PROP_PUSH_STATE, PUSH_STATE_CONNECTED);
                    event.putInt(EVENT_PROP_ERROR_CODE, 0);
                    event.putString(EVENT_PROP_ERROR_MSG, "推流连接成功:只有当连接成功以后才能开始推流");
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_STATE_UPDATE.toString(), event);

                    break;
                case RecorderConstance.RECORDER_OPEN_URL_FAILED:
                    Log.d(TAG, "推流连接失败:如果失败,大多是推流地址不可用或者网络问题");
//                    Toast.makeText(getContext(), "推流连接失败:如果失败,大多是推流地址不可用或者网络问题", Toast.LENGTH_SHORT).show();
//                    timeView.setText("连接失败");

                    event.putInt(EVENT_PROP_PUSH_STATE, PUSH_STATE_ERROR);
                    event.putInt(EVENT_PROP_ERROR_CODE, 0);
                    event.putString(EVENT_PROP_ERROR_MSG, "推流连接失败:如果失败,大多是推流地址不可用或者网络问题");
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_STATE_UPDATE.toString(), event);

                    break;
                case RecorderConstance.RECORDER_PUSH_FIRST_SIZE:
                    Log.d(TAG, "第一针画面推流成功,代表成功的开始推流了:推流成功的标志回调");
//                    Toast.makeText(getContext(), "第一针画面推流成功,代表成功的开始推流了:推流成功的标志回调", Toast.LENGTH_SHORT).show();
                    if (!mTimeFlag) {
                        timerHandler.postDelayed(timerRunnable, 1000);
                    }
                    mTimeFlag = true;

                    event.putInt(EVENT_PROP_PUSH_STATE, PUSH_STATE_OPENED);
                    event.putInt(EVENT_PROP_ERROR_CODE, 0);
                    event.putString(EVENT_PROP_ERROR_MSG, "第一针画面推流成功,代表成功的开始推流了:推流成功的标志回调");
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_STATE_UPDATE.toString(), event);
                    break;

                case RecorderConstance.RECORDER_PUSH_AUDIO_PACKET_LOSS_RATE:
                    Log.d(TAG, "音频出现丢帧现象。如果一分钟丢帧次数大于5次,导致声音跳动:可以对网络进行判定");

                    event.putInt(EVENT_PROP_PUSH_STATE, PUSH_STATE_WARNING);
                    event.putInt(EVENT_PROP_ERROR_CODE, 0);
                    event.putString(EVENT_PROP_ERROR_MSG, "音频出现丢帧现象。如果一分钟丢帧次数大于5次,导致声音跳动:可以对网络进行判定");
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_STATE_UPDATE.toString(), event);
                    break;

                case RecorderConstance.RECORDER_PUSH_VIDEO_PACKET_LOSS_RATE:
                    Log.d(TAG, "视频出现丢帧现象,如果一分钟丢帧次数大于5次,导致画面跳动:可以对网络进行判定");

                    event.putInt(EVENT_PROP_PUSH_STATE, PUSH_STATE_WARNING);
                    event.putInt(EVENT_PROP_ERROR_CODE, 0);
                    event.putString(EVENT_PROP_ERROR_MSG, "视频出现丢帧现象,如果一分钟丢帧次数大于5次,导致画面跳动:可以对网络进行判定");
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_STATE_UPDATE.toString(), event);
                    break;

                case RecorderConstance.RECORDER_PUSH_ERROR:
                    Log.d(TAG, "推流失败,原因:网络较差,编码出错,推流崩溃,第一针数据发送失败...等等各种原因导致");
//                    Toast.makeText(getContext(), "推流失败,原因:网络较差,编码出错,推流崩溃,第一针数据发送失败...等等各种原因导致", Toast.LENGTH_SHORT).show();
//                    timeView.setText("推流出错");
                    event.putInt(EVENT_PROP_PUSH_STATE, PUSH_STATE_ERROR);
                    event.putInt(EVENT_PROP_ERROR_CODE, 0);
                    event.putString(EVENT_PROP_ERROR_MSG, "推流失败,原因:网络较差,编码出错,推流崩溃,第一针数据发送失败...等等各种原因导致");
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_STATE_UPDATE.toString(), event);

                    break;
                case RecorderConstance.RECORDER_PUSH_STOP_SUCCESS:
                    Log.d(TAG, "成功的关闭了底层推流,可以进行下次推流了:保证推流成功关闭");
//                    Toast.makeText(getContext(), "成功的关闭了底层推流,可以进行下次推流了:保证推流成功关闭", Toast.LENGTH_SHORT).show();
//                    timeView.setText("成功关闭推流服务");
                    mTimeFlag = false;

                    event.putInt(EVENT_PROP_PUSH_STATE, PUSH_STATE_CLOSED);
                    event.putInt(EVENT_PROP_ERROR_CODE, 0);
                    event.putString(EVENT_PROP_ERROR_MSG, "成功的关闭了底层推流,可以进行下次推流了:保证推流成功关闭");
                    mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_STATE_UPDATE.toString(), event);
                    break;

                //*********************乐视云直播有一部分属于自己的回调事件*****************
                case RecorderConstance.LIVE_STATE_END_ERROR:
                    Log.d(TAG, "直播已结束");
                    Toast.makeText(getContext(), "直播已结束", Toast.LENGTH_SHORT).show();
                    break;
                case RecorderConstance.LIVE_STATE_CANCEL_ERROR:
                    Log.d(TAG, "直播已取消");
                    Toast.makeText(getContext(), "直播已取消", Toast.LENGTH_SHORT).show();
                    break;
                case RecorderConstance.LIVE_STATE_NEED_RECORD:
                    Log.d(TAG, "直播开启转点播功能");
                    Toast.makeText(getContext(), "直播开启转点播功能", Toast.LENGTH_SHORT).show();
                    break;
                case RecorderConstance.LIVE_STATE_NOT_STARTED_ERROR:
                    Log.d(TAG, "直播时间未到");
                    Toast.makeText(getContext(), "直播时间未到", Toast.LENGTH_SHORT).show();
                    break;
                case RecorderConstance.LIVE_STATE_OTHER_ERROR:
                    Log.d(TAG, "其他直播错误");
                    Toast.makeText(getContext(), "其他直播错误", Toast.LENGTH_SHORT).show();
                    break;
                case RecorderConstance.LIVE_STATE_PUSH_COMPLETE:
                    Log.d(TAG, "推流完成");
                    Toast.makeText(getContext(), "推流完成", Toast.LENGTH_SHORT).show();
                    break;
                case RecorderConstance.LIVE_STATE_TIME_REMAINING:
                    Log.d(TAG, "直播剩余时间:在剩余5分钟和30分钟时都会回调");
                    Toast.makeText(getContext(), "直播剩余时间:在剩余5分钟和30分钟时都会回调", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    private LetvRecorderCallback<ArrayList<LivesInfo>> callback = new LetvRecorderCallback<ArrayList<LivesInfo>>() {
        @Override
        public void onFailed(int code, String msg) {

        }

        @Override
        public void onSucess(ArrayList<LivesInfo> data) {
            //*********************在收到请求成功消息时,需要选择自己的推流机位。并且开始推流******************
            if (data != null && data.size() > 0) {
                mLECPublisher.selectMachine(0);//相当于设置推流地址
                mLECPublisher.publish();
            }
        }
    };


    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (((mPushType == PUSH_TYPE_MOBILE_URI || mPushType == PUSH_TYPE_MOBILE) && mPublisher.isRecording()) ||
                    (mPushType == PUSH_TYPE_LECLOUD && mLECPublisher.isRecording())) {
                mTime++;
                WritableMap event = Arguments.createMap();
                event.putBoolean(EVENT_PROP_PUSH_TIME_FLAG, mTimeFlag);
                event.putInt(EVENT_PROP_PUSH_TIME, mTime);
                mEventEmitter.receiveEvent(getId(), Events.EVENT_PUSH_TIME_UPDATE.toString(), event);
                timerHandler.postDelayed(timerRunnable, 1000);
            }

        }
    };



/*============================= 周期方法 ===================================*/

    @Override
    public void onPause() {
        super.onPause();
        if (mLePushValid) {
            if (mPushType == PUSH_TYPE_MOBILE_URI || mPushType == PUSH_TYPE_MOBILE) { //移动直播
                if (mPublisher.isRecording()) { //正在推流
                    isBack = true;
                    mPublisher.stopPublish();//停止推流
                }
                //关闭摄像头
                mPublisher.getVideoRecordDevice().stop();
            } else if (mPushType == PUSH_TYPE_LECLOUD) { //云直播
                if (mLECPublisher.isRecording()) { //正在推流
                    isBack = true;
                    mLECPublisher.stopPublish();//停止推流
                }
                //关闭摄像头
                mLECPublisher.getVideoRecordDevice().stop();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLePushValid) {
            if (mPushType == PUSH_TYPE_MOBILE_URI || mPushType == PUSH_TYPE_MOBILE) { //移动直播
                mPublisher.release();//销毁推流器
                mPublisher = null;
            } else if (mPushType == PUSH_TYPE_LECLOUD) { //云直播
                mLECPublisher.release();//销毁推流器
                mLECPublisher = null;
            }
            mLePushValid = false;
        }
        ((Activity) mThemedReactContext.getBaseContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        ((Activity) mThemedReactContext.getBaseContext()).getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onGLSurfaceCreatedListener() {
        if (mPushType == PUSH_TYPE_MOBILE_URI || mPushType == PUSH_TYPE_MOBILE) { //移动直播
            mPublisher.getVideoRecordDevice().start();//打开摄像头
        } else if (mPushType == PUSH_TYPE_LECLOUD) { //云直播
            mLECPublisher.getVideoRecordDevice().start();//打开摄像头
        }
        mLePushValid = true;
        if (isBack) {
            isBack = false;
            setPush(false);
        }

    }

    @Override
    protected void onAttachedToWindow() {
        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onAttachedToWindow 调起！");
        super.onAttachedToWindow();
        onResume();
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onDetachedFromWindow 调起！");
        super.onDetachedFromWindow();
        onPause();
        onDestroy();
    }

    @Override
    public void zoomOnTouch(int state, int zoom, int maxZoom) {

    }

//    @Override
//    public void onHostResume() {
//        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onHostResume 调起！");
//        if (mLePushValid)
//            onResume();
//    }
//
//    @Override
//    public void onHostPause() {
//        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onHostPause 调起！");
//        if (mLePushValid)
//            onPause();
//    }
//
//    @Override
//    public void onHostDestroy() {
//        Log.d(TAG, LogUtils.getTraceInfo() + "生命周期事件 onHostDestroy 调起！");
//        if (mLePushValid)
//            onDestroy();
//    }
}
