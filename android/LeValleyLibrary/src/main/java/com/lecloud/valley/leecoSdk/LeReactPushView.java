/*************************************************************************
 * Description: 乐视直播推流组件
 * Author: raojia
 * Mail: raojia@le.com
 * Created Time: 2017-2-5
 ************************************************************************/
package com.lecloud.valley.leecoSdk;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.lecloud.valley.utils.LogUtils;
import com.letv.recorder.bean.AudioParams;
import com.letv.recorder.bean.CameraParams;
import com.letv.recorder.callback.ISurfaceCreatedListener;
import com.letv.recorder.callback.LetvRecorderCallback;
import com.letv.recorder.callback.PublishListener;
import com.letv.recorder.controller.CameraSurfaceView;
import com.letv.recorder.controller.Publisher;
import com.letv.recorder.ui.logic.RecorderConstance;

import static com.lecloud.valley.common.Constants.PROP_PUSH_TYPE;
import static com.lecloud.valley.common.Constants.PUSH_TYPE_LECLOUD;
import static com.lecloud.valley.common.Constants.PUSH_TYPE_MOBILE;
import static com.lecloud.valley.common.Constants.PUSH_TYPE_MOBILE_URI;
import static com.lecloud.valley.utils.LogUtils.TAG;

/**
 * Created by RaoJia on 2017/2/5.
 */

public class LeReactPushView extends CameraSurfaceView implements ISurfaceCreatedListener {

    private ThemedReactContext mThemedReactContext;
    private RCTEventEmitter mEventEmitter;

    private Context mContext;

    private Publisher publisher;
    private CameraParams cameraParams;
    private AudioParams audioParams;

    //    private final static String TAG = "CameraView";
    private boolean isBack = false;//后台标志,在进入后台之前正在推流设置为true。判断是否在后台回来时继续推流

    private Bundle mPushPara;

//    private TextView timeView;//推流时间显示

    private int time = 0;
    private boolean timeFlag = false;


/*============================= 推流View构造 ===================================*/

    public LeReactPushView(ThemedReactContext context) {
        super(context);
        mThemedReactContext = context;

        init(mThemedReactContext, false);
    }

    public void init(ThemedReactContext context, boolean isLandscape) {

        publisher = Publisher.getInstance();
        publisher.initPublisher((Activity) context.getBaseContext());
        publisher.getRecorderContext().setUseLanscape(isLandscape);//告诉推流器使用横屏推流还是竖屏推流
        cameraParams = publisher.getCameraParams();
        audioParams = publisher.getAudioParams();

        publisher.setPublishListener(listener);//设置推流状态监听器
        //绑定Camera显示View,要求必须是CameraSurfaceView
        publisher.getVideoRecordDevice().bindingGLView(this);
        //设置CameraSurfaceView 监听器,当CameraSurfaceView 创建成功的时候回回调onGLSurfaceCreatedListener,这个时候才能开启摄像头
        publisher.getVideoRecordDevice().setSurfaceCreatedListener(this);

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
        publisher.getVideoRecordDevice().setFocusView(new View(getContext()));//设置对焦图片。如果需要对焦功能和对焦动画,请打开上边两个设置,并且在这里传入一个合适的View
        publisher.getRecorderContext().setAutoUpdateLogFile(false); //是否开启日志文件自动上报

        /////////////////////////////////////////////////////////////////////////////////////////////
    }

/*============================= 外部接口 ===================================*/

    /**
     * 开始推流
     *
     * @param bundle 推流参数
     */
    public void publish(Bundle bundle) {
        Log.d(TAG, LogUtils.getTraceInfo() + "外部控制——— 推流参数 bundle:" + bundle);
        if (bundle == null) return;

        mPushPara = bundle;

        // 推流类型切换
        int newPushType = mPushPara.containsKey(PROP_PUSH_TYPE) ? mPushPara.getInt(PROP_PUSH_TYPE) : -1;

        switch (newPushType) {
            case PUSH_TYPE_MOBILE_URI:
                time = 0;
                String url = bundle.containsKey("url") ? bundle.getString("url") : "";
                if (!publisher.isRecording() && mPushPara != null) {
//                    timeView.setText("请求推流");
                    publisher.setUrl(url);//设置推流地址
                    publisher.publish();//在摄像头打开以后才能开始推流
                } else {
//                    timeView.setText("关闭推流");
                    publisher.stopPublish(); //结束推流
                }
                break;

            case PUSH_TYPE_MOBILE:
                break;

            case PUSH_TYPE_LECLOUD:
                break;
        }
    }

    /**
     * 切换摄像头,需要注意,切换摄像头不能太频繁,如果太频繁会导致应用程序崩溃。建议最快10秒一次
     */
    boolean isSwitch = false;

    public void switchCamera() {
        if (isSwitch) {
            Toast.makeText(getContext(), "切换摄像头不能太频繁哦,等待10秒后在切换吧", Toast.LENGTH_SHORT).show();
            return;
        }
        tenHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), "十秒已过,可以继续切换摄像头", Toast.LENGTH_SHORT).show();
                isSwitch = false;
            }
        }, 10 * 1000);
        isSwitch = true;
        int cameraID;
        if (cameraParams.getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
        } else {
            cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
            if (flag) flag = !flag;//切换前置摄像头会自动关闭闪光灯
        }
        publisher.getVideoRecordDevice().switchCamera(cameraID);//切换摄像头
    }

    /**
     * 开启闪光灯。注意,当使用前置摄像头时不能打开闪光灯
     */
    boolean flag = false;

    public void changeFlash() {
        if (cameraParams.getCameraId() != Camera.CameraInfo.CAMERA_FACING_FRONT) {
            flag = !flag;
            publisher.getVideoRecordDevice().setFlashFlag(flag);//切换闪关灯
        } else {
            Toast.makeText(getContext(), "前置摄像头不能打开闪关灯", Toast.LENGTH_LONG).show();
        }
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
        publisher.getVideoRecordDevice().setFilterModel(model);//切换滤镜
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
        publisher.setVolumeGain(volume);//设置声音大小
    }

//    /**
//     * ZOOM 操作.必须保证摄像头已经成功打开
//     * @return
//     */
//    private void getZoom(){
//         publisher.getVideoRecordDevice().getZoom();
//        publisher.getVideoRecordDevice().setZoom(1);
//        publisher.getVideoRecordDevice().getMaxZoom();
//    }

    /**
     * 出现问题了,主动上报日志文件
     */
    public void sendErrorLogFile() {
        publisher.sendLogFile(new LetvRecorderCallback() {
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
            switch (msg.what) {
                case RecorderConstance.RECORDER_OPEN_URL_SUCESS:
                    Log.d(TAG, "推流连接成功:只有当连接成功以后才能开始推流");
                    Toast.makeText(getContext(), "推流连接成功:只有当连接成功以后才能开始推流", Toast.LENGTH_SHORT).show();
//                    timeView.setText("连接成功");
                    break;
                case RecorderConstance.RECORDER_OPEN_URL_FAILED:
                    Log.d(TAG, "推流连接失败:如果失败,大多是推流地址不可用或者网络问题");
                    Toast.makeText(getContext(), "推流连接失败:如果失败,大多是推流地址不可用或者网络问题", Toast.LENGTH_SHORT).show();
//                    timeView.setText("连接失败");
                    break;
                case RecorderConstance.RECORDER_PUSH_FIRST_SIZE:
                    Log.d(TAG, "第一针画面推流成功,代表成功的开始推流了:推流成功的标志回调");
                    Toast.makeText(getContext(), "第一针画面推流成功,代表成功的开始推流了:推流成功的标志回调", Toast.LENGTH_SHORT).show();
                    if (!timeFlag) {
                        timerHandler.postDelayed(timerRunnable, 1000);
                    }
                    timeFlag = true;
                    break;
                case RecorderConstance.RECORDER_PUSH_AUDIO_PACKET_LOSS_RATE:
                    Log.d(TAG, "音频出现丢帧现象。如果一分钟丢帧次数大于5次,导致声音跳动:可以对网络进行判定");
                    break;
                case RecorderConstance.RECORDER_PUSH_VIDEO_PACKET_LOSS_RATE:
                    Log.d(TAG, "视频出现丢帧现象,如果一分钟丢帧次数大于5次,导致画面跳动:可以对网络进行判定");
                    break;
                case RecorderConstance.RECORDER_PUSH_ERROR:
                    Toast.makeText(getContext(), "推流失败,原因:网络较差,编码出错,推流崩溃,第一针数据发送失败...等等各种原因导致", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "推流失败,原因:网络较差,编码出错,推流崩溃,第一针数据发送失败...等等各种原因导致");
//                    timeView.setText("推流出错");
                    break;
                case RecorderConstance.RECORDER_PUSH_STOP_SUCCESS:
                    Log.d(TAG, "成功的关闭了底层推流,可以进行下次推流了:保证推流成功关闭");
                    Toast.makeText(getContext(), "成功的关闭了底层推流,可以进行下次推流了:保证推流成功关闭", Toast.LENGTH_SHORT).show();
//                    timeView.setText("成功关闭推流服务");
                    break;
            }
        }
    };

    private Handler tenHandler = new Handler();
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (publisher.isRecording()) {
                time++;
//                timeView.setText("时间:" + time);
                timerHandler.postDelayed(timerRunnable, 1000);
            }
        }
    };

    void setTime(TextView time) {
//        this.timeView = time;
    }


/*============================= 周期方法 ===================================*/

    @Override
    public void onPause() {
        super.onPause();
        if (publisher.isRecording()) { //正在推流
            isBack = true;
            publisher.stopPublish();//停止推流
        }
        //关闭摄像头
        publisher.getVideoRecordDevice().stop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        publisher.release();//销毁推流器
    }

    @Override
    public void onGLSurfaceCreatedListener() {
        publisher.getVideoRecordDevice().start();//打开摄像头
        if (isBack) {
            isBack = false;
            publish(mPushPara);
        }
    }

    @Override
    public void zoomOnTouch(int state, int zoom, int maxZoom) {

    }

}
