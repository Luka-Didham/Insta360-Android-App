package com.arashivision.sdk.demo.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;

import com.arashivision.sdk.demo.R;
import com.arashivision.sdk.demo.util.CameraBindNetworkManager;
import com.arashivision.sdkcamera.camera.InstaCameraManager;
import com.arashivision.sdkcamera.camera.callback.IPreviewStatusListener;
import com.arashivision.sdkcamera.camera.resolution.PreviewStreamResolution;
import com.arashivision.sdkmedia.player.capture.CaptureParamsBuilder;
import com.arashivision.sdkmedia.player.capture.InstaCapturePlayerView;
import com.arashivision.sdkmedia.player.config.InstaStabType;
import com.arashivision.sdkmedia.player.listener.PlayerViewListener;

public class PreviewTest extends BaseObserveCameraActivity implements IPreviewStatusListener {

    private PreviewStreamResolution mCurrentResolution = PreviewStreamResolution.STREAM_2176_1088_30FPS;
    private InstaCapturePlayerView mCapturePlayerView;
    private ViewGroup mLayoutContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        setTitle("Test");
        bindViews();


        // 进入页面后可自动开启预览
        // Auto open preview after page gets focus
        InstaCameraManager.getInstance().setPreviewStatusChangedListener(this);
        // mSpinnerResolution的onItemSelected会自动触发开启预览，故此处注释掉
        // mSpinnerResolution -> onItemSelected() Will automatically trigger to open the preview, so comment it out here
//        InstaCameraManager.getInstance().startPreviewStream();

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.

    }

    private void bindViews() {
        mLayoutContent = findViewById(R.id.layout_content);
        mCapturePlayerView = findViewById(R.id.player_capture_test);
        mCapturePlayerView.setLifecycle(getLifecycle());
        mCapturePlayerView.setStabType(InstaStabType.STAB_TYPE_AUTO);
        InstaCameraManager.getInstance().startPreviewStream(mCurrentResolution);
    }

    @Override
    public void onOpened() {
        // 预览开启成功，可以播放预览流
        // Preview stream is on and can be played
        InstaCameraManager.getInstance().setStreamEncode();
        mCapturePlayerView.setPlayerViewListener(new PlayerViewListener() {
            @Override
            public void onLoadingFinish() {
                InstaCameraManager.getInstance().setPipeline(mCapturePlayerView.getPipeline());
            }

            @Override
            public void onReleaseCameraPipeline() {
                InstaCameraManager.getInstance().setPipeline(null);
            }
        });
        mCapturePlayerView.prepare(createParams());
        mCapturePlayerView.play();
        mCapturePlayerView.setKeepScreenOn(true);
        FrameLayout mFrame = findViewById(R.id.layout_content);


    }

    private CaptureParamsBuilder createParams() {
        CaptureParamsBuilder builder = new CaptureParamsBuilder()
                .setCameraType(InstaCameraManager.getInstance().getCameraType())
                .setMediaOffset(InstaCameraManager.getInstance().getMediaOffset())
                .setMediaOffsetV2(InstaCameraManager.getInstance().getMediaOffsetV2())
                .setMediaOffsetV3(InstaCameraManager.getInstance().getMediaOffsetV3())
                .setCameraSelfie(InstaCameraManager.getInstance().isCameraSelfie())
                .setGyroTimeStamp(InstaCameraManager.getInstance().getGyroTimeStamp())
                .setBatteryType(InstaCameraManager.getInstance().getBatteryType())
                .setStabType(InstaStabType.STAB_TYPE_AUTO)
                .setStabEnabled(true);


        builder.setRenderModelType(CaptureParamsBuilder.RENDER_MODE_PLANE_STITCH)
                .setScreenRatio(20, 9);

        return builder;
    }

    @Override
    public void onIdle() {
        // 预览已停止
        // Preview Stopped
        mCapturePlayerView.destroy();
        mCapturePlayerView.setKeepScreenOn(false);
    }

    @Override
    public void onError() {
        // 预览开启失败
        // Preview Failed

    }
}
