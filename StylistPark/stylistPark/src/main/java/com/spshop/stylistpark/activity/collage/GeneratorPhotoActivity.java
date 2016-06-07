package com.spshop.stylistpark.activity.collage;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.utils.BitmapUtil;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.FileManager;
import com.spshop.stylistpark.utils.LogUtil;
import com.spshop.stylistpark.widgets.SquareCameraPreview;
import com.tencent.stat.StatService;

@SuppressLint("NewApi")
public class GeneratorPhotoActivity extends BaseActivity implements
        SurfaceHolder.Callback, Camera.PictureCallback, OnClickListener {
	
    private static final String TAG = "GeneratorPhotoActivity";
    private static final int PICTURE_SIZE_MAX_WIDTH = 1280;
    private static final int PREVIEW_SIZE_MAX_WIDTH = 640;

    private Camera mCamera;
    private SquareCameraPreview mPreviewView;
    private SurfaceHolder mSurfaceHolder;
    private CameraOrientationListener mOrientationListener;
    private ImageView iv_back, iv_switch, iv_choose, iv_take;
    private View v_cover_top, v_cover_bottom;

    private int mCameraID;
    private int mDisplayOrientation;
    private int mLayoutOrientation;
    private int mCoverHeight;
    private int mPreviewHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator_photo);

        if (getActionBar() != null) {
            getActionBar().hide();
        }
        getWindow().setBackgroundDrawable(null);
        
        findViewById();
		initView();
    }

	private void findViewById() {
        mPreviewView = (SquareCameraPreview) findViewById(R.id.photo_view_camera_preview);
        v_cover_top = findViewById(R.id.photo_view_cover_top);
        v_cover_bottom = findViewById(R.id.photo_view_cover_bottom);
        iv_back = (ImageView) findViewById(R.id.photo_iv_back);
        iv_switch = (ImageView) findViewById(R.id.photo_iv_switch);
        iv_choose = (ImageView) findViewById(R.id.photo_iv_choose);
        iv_take = (ImageView) findViewById(R.id.photo_iv_take);
	}

	private void initView() {
		setHeadVisibility(View.GONE);
		iv_back.setOnClickListener(this);
		iv_switch.setOnClickListener(this);
		iv_choose.setOnClickListener(this);
		iv_take.setOnClickListener(this);
		
		mOrientationListener = new CameraOrientationListener(this);
        mOrientationListener.enable();
		mPreviewView.getHolder().addCallback(this); //添加摄像头回调源(重要)
		
        if (mCoverHeight == 0) {
            ViewTreeObserver observer = mPreviewView.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            	
                @SuppressWarnings("deprecation")
				@Override
                public void onGlobalLayout(){
                    if (v_cover_top.getLayoutParams().height != 0)
                        return;
                    int width = mPreviewView.getWidth();
                    mPreviewHeight = mPreviewView.getHeight();
                    mCoverHeight = (mPreviewHeight - width) / 2;

                    LogUtil.i(TAG, "preview width " + width + " height " + mPreviewHeight);
                    v_cover_top.getLayoutParams().height = mCoverHeight;
                    v_cover_bottom.getLayoutParams().height = mCoverHeight;

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						mPreviewView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					} else {
						mPreviewView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}
                }
            });
        } else {
        	v_cover_top.getLayoutParams().height = mCoverHeight;
        	v_cover_bottom.getLayoutParams().height = mCoverHeight;
        }
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.photo_iv_back:
			finish();
			break;
		case R.id.photo_iv_switch:
			if (mCameraID == CameraInfo.CAMERA_FACING_FRONT) {
    			mCameraID = getBackCameraID();
    		} else {
    			mCameraID = getFrontCameraID();
    		}
			switchPreview();
			break;
		case R.id.photo_iv_choose:
			choosePhoto();
			break;
		case R.id.photo_iv_take:
			takePicture();
			break;
		}
	}
    
    private void switchPreview() { //切换摄像头
        stopCameraPreview();
        if (mCamera != null) {
        	mCamera.release();
		}

        getCamera(mCameraID);
        if (mCamera != null) {
        	startCameraPreview();
		}
    }

    private void startCameraPreview() {
    	determineDisplayOrientation();
    	setupCamera();
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
        } catch (Exception e){
            ExceptionUtil.handle(mContext, e);
        }
    }

    private void determineDisplayOrientation() {
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(mCameraID, cameraInfo);

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation)
        {
        case Surface.ROTATION_0:
        {
            degrees = 0;
            break;
        }
        case Surface.ROTATION_90:
        {
            degrees = 90;
            break;
        }
        case Surface.ROTATION_180:
        {
            degrees = 180;
            break;
        }
        case Surface.ROTATION_270:
        {
            degrees = 270;
            break;
        }
        }

        int displayOrientation;

        // Camera direction
        if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT)
        {
            // Orientation is angle of rotation when facing the camera for
            // the camera image to match the natural orientation of the device
            displayOrientation = (cameraInfo.orientation + degrees) % 360;
            displayOrientation = (360 - displayOrientation) % 360;
        } else
        {
            displayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
        }

        mDisplayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
        mLayoutOrientation = degrees;

        mCamera.setDisplayOrientation(displayOrientation);
    }

    private void setupCamera() {
        // Never keep a global parameters
        Camera.Parameters parameters = mCamera.getParameters();

        Size bestPreviewSize = determineBestPreviewSize(parameters);
        Size bestPictureSize = determineBestPictureSize(parameters);

        parameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
        parameters.setPictureSize(bestPictureSize.width, bestPictureSize.height);

        // Set continuous picture focus, if it's supported
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
        {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        // Lock in the changes
        mCamera.setParameters(parameters);
    }
    
    private Size determineBestPreviewSize(Camera.Parameters parameters) {
        return determineBestSize(parameters.getSupportedPreviewSizes(), PREVIEW_SIZE_MAX_WIDTH);
    }

    private Size determineBestPictureSize(Camera.Parameters parameters) {
        return determineBestSize(parameters.getSupportedPictureSizes(), PICTURE_SIZE_MAX_WIDTH);
    }

    private Size determineBestSize(List<Size> sizes, int widthThreshold) {
        Size bestSize = null;
        Size size;
        int numOfSizes = sizes.size();
        for (int i = 0; i < numOfSizes; i++)
        {
            size = sizes.get(i);
            boolean isDesireRatio = (size.width / 4) == (size.height / 3);
            boolean isBetterSize = (bestSize == null)
                    || size.width > bestSize.width;

            if (isDesireRatio && isBetterSize)
            {
                bestSize = size;
            }
        }

        if (bestSize == null)
        {
            LogUtil.i(TAG, "cannot find the best camera size");
            return sizes.get(sizes.size() - 1);
        }

        return bestSize;
    }

    private void stopCameraPreview() {
        // Nulls out callbacks, stops face detection
        if(mCamera != null){
            mCamera.stopPreview();
        }
        mPreviewView.setCamera(null);
    }
    
    private void choosePhoto() { //相册
        Intent i = new Intent(GeneratorPhotoActivity.this, GeneratorGalleryActivity.class);
        i.putExtra("ACTION", "not null is ok");
        startActivity(i);
        finish();
    }

    private void takePicture() { //拍照
        mOrientationListener.rememberOrientation();
        
        // Shutter callback occurs after the image is captured. This can
        // be used to trigger a sound to let the user know that image is taken
        Camera.ShutterCallback shutterCallback = null;

        // Raw callback occurs when the raw image data is available
        Camera.PictureCallback raw = null;

        // postView callback occurs when a scaled, fully processed
        // postView image is available.
        Camera.PictureCallback postView = null;

        // jpeg callback occurs when the compressed image is available
        if (mCamera != null) {
        	mCamera.takePicture(shutterCallback, raw, postView, this);
		}
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(TAG, "onResume");
		// 页面开始
		StatService.onResume(this);
    }

	@Override
	public void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		// 页面结束
		StatService.onPause(this);
	}

    @Override
    public void onStop() {
        mOrientationListener.disable();
        // stop the preview
        stopCameraPreview();
        if (mCamera != null) {
        	mCamera.release();
		}
        super.onStop();
    }

    private void getCamera(int cameraID) {
        LogUtil.i(TAG, "get camera with id " + cameraID);
        try {
            mCamera = Camera.open(cameraID);
            mPreviewView.setCamera(mCamera);
        } catch (Exception e) {
        	// 没有摄像头权限
            CommonTools.showToast(mContext, getString(R.string.photo_camera_authority), 3000);
            ExceptionUtil.handle(mContext, e);
            finish();
        }
    }
    
    private int getFrontCameraID() {
        PackageManager pm = getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            return CameraInfo.CAMERA_FACING_FRONT;
        }
        return getBackCameraID();
    }
    
    private int getBackCameraID() {
        return CameraInfo.CAMERA_FACING_BACK;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) { //拍照回调
        LogUtil.i(TAG, "onPictureTaken");
        int rotation = (mDisplayOrientation + mOrientationListener.getRememberedNormalOrientation() + mLayoutOrientation) % 360;
        Uri uri = saveImage(Arrays.copyOf(data, data.length), camera, rotation);
        if(uri != null){
            Intent intent = new Intent(this, GeneratorSelectProductActivity.class);
            intent.putExtra(AppConfig.ACTIVITY_KEY_COLLAGE_URI, uri);
            startActivity(intent);
            finish();
        }else{
        	showErrorDialog(R.string.photo_show_save_fail);
        }
    }
    
    public Uri saveImage(byte[] data, Camera camera, int rotation){ //保存照片
        Bitmap bitmap = BitmapUtil.getBitmapFromByte(mContext, data);
        if (rotation != 0){
            Bitmap oldBitmap = bitmap;
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            bitmap = Bitmap.createBitmap(oldBitmap, 0, 0, oldBitmap.getWidth(), oldBitmap.getHeight(), matrix, false);
            oldBitmap.recycle();
        }
        if(bitmap != null){
            String imgFileName = FileManager.getFileName();
            File file = BitmapUtil.createPath(imgFileName, true);
            if (file == null) {
            	showErrorDialog(R.string.photo_show_save_fail);
    			return null;
			}
            bitmap = BitmapUtil.getBitmap(bitmap, GEN_OUTPUT_SIDE, GEN_OUTPUT_SIDE);
            AppApplication.saveBitmapFile(bitmap, file, 70);
            return Uri.fromFile(file);
        }
        return null;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){ //摄像头启用
        mSurfaceHolder = holder;
        getCamera(mCameraID);
        if (mCamera != null) {
        	startCameraPreview();
		}else {
			// 没有摄像头权限
            CommonTools.showToast(mContext, getString(R.string.photo_camera_authority), 3000);
            finish();
		}
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){ //摄像头拉伸
    	
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){ //摄像头关闭
    	
    }
    
    private static class CameraOrientationListener extends OrientationEventListener {

        private int mCurrentNormalizedOrientation;
        private int mRememberedNormalOrientation;

        public CameraOrientationListener(Context context) {
            super(context, SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation != ORIENTATION_UNKNOWN) {
                mCurrentNormalizedOrientation = normalize(orientation);
            }
        }

        private int normalize(int degrees) {
            if (degrees > 315 || degrees <= 45) {
                return 0;
            }

            if (degrees > 45 && degrees <= 135) {
                return 90;
            }

            if (degrees > 135 && degrees <= 225) {
                return 180;
            }

            if (degrees > 225 && degrees <= 315) {
                return 270;
            }

            throw new RuntimeException("The physics as we know them are no more. Watch out for anomalies.");
        }

        public void rememberOrientation() {
            mRememberedNormalOrientation = mCurrentNormalizedOrientation;
        }

        public int getRememberedNormalOrientation() {
            return mRememberedNormalOrientation;
        }
    }

}
