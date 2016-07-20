package com.spshop.stylistpark.activity.collage;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.albinmathew.photocrop.cropoverlay.edge.Edge;
import com.albinmathew.photocrop.cropoverlay.utils.ImageViewUtil;
import com.albinmathew.photocrop.photoview.PhotoView;
import com.albinmathew.photocrop.photoview.PhotoViewAttacher;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.utils.BitmapUtil;
import com.spshop.stylistpark.utils.CommonTools;
import com.spshop.stylistpark.utils.ExceptionUtil;
import com.spshop.stylistpark.utils.FileManager;
import com.spshop.stylistpark.utils.LogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GeneratorGalleryActivity extends BaseActivity {
	
	private static final String TAG = "GeneratorGalleryActivity";
	
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    
    private ContentResolver mContentResolver;
    private PhotoView mPhotoView;
    private TextView tv_hint;
    
    private File mFileTemp;
    private float minScale = 1f; //图片缩放比例
    private final int IMAGE_MAX_SIZE = 1024;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator_gallery);
        
        createTempFile(); //创建图片路径
        if(getIntent().hasExtra("ACTION")){
            if (savedInstanceState == null || !savedInstanceState.getBoolean("restoreState")) {
                String action = getIntent().getStringExtra("ACTION");
                LogUtil.i(TAG, "onCreate TextUtils.isEmpty(action): " + TextUtils.isEmpty(action));
                if (!TextUtils.isEmpty(action)) {
                        getIntent().removeExtra("ACTION");
                        pickImage(); //打开系统相册
                }
            }
        }
        getWindow().setBackgroundDrawable(null);
        
        findViewById();
		initView();
    }

	private void findViewById() {
		mPhotoView = (PhotoView) findViewById(R.id.gallery_view_photo);
        tv_hint = (TextView) findViewById(R.id.gallery_tv_hint);
	}

	private void initView() {
		setTitle(R.string.photo_editor_title);
		setBtnRight(getString(R.string.confirm));
		mContentResolver = getContentResolver();
		mPhotoView.addListener(new PhotoViewAttacher.IGetImageBounds() {
			@Override
			public Rect getImageBounds() {
				return new Rect((int) Edge.LEFT.getCoordinate(), (int) Edge.TOP
						.getCoordinate(), (int) Edge.RIGHT.getCoordinate(), (int) Edge.BOTTOM.getCoordinate());
			}
		});
		// 提示语属性设置
		int screenWidth = CommonTools.getScreeanSize(this).x;
		LayoutParams lp = (LayoutParams) tv_hint.getLayoutParams();
		lp.setMargins(lp.leftMargin, screenWidth + lp.topMargin, lp.rightMargin, lp.bottomMargin);
		// 获取并显示图片
		showImage();
	}
    
    private void showImage() { 
        Uri mImageUri = getImageUri(mFileTemp.getPath());
        Bitmap bitmap = getBitmap(mImageUri);
        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
        
        int h = drawable.getIntrinsicHeight();
        int w = drawable.getIntrinsicWidth();
        final float cropWindowWidth = Edge.getWidth();
        final float cropWindowHeight = Edge.getHeight();
        if (h <= w) {
            minScale = (cropWindowHeight + 1f) / h;
        } else if (w < h) {
            minScale = (cropWindowWidth + 1f) / w;
        }
        mPhotoView.setMaximumScale(minScale * 3);
        mPhotoView.setMediumScale(minScale * 2);
        mPhotoView.setMinimumScale(minScale);
        mPhotoView.setImageDrawable(drawable);
        mPhotoView.setScale(minScale);
    }
    
    private static Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }
    
    private Bitmap getBitmap(Uri uri) {
        InputStream in = null;
        Bitmap returnedBitmap = null;
        try {
            in = mContentResolver.openInputStream(uri);
            //Decode image size
            BitmapFactory.Options o1 = new BitmapFactory.Options();
            o1.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o1);
            in.close();
            int scale = 1;
            if (o1.outHeight > IMAGE_MAX_SIZE || o1.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o1.outHeight, o1.outWidth)) / Math.log(0.5)));
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = mContentResolver.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(in, null, o2);
            in.close();

            //First check
            ExifInterface ei = new ExifInterface(uri.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    returnedBitmap = rotateImage(bitmap, 90);
                    //Free up the memory
                    bitmap.recycle();
                    bitmap = null;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    returnedBitmap = rotateImage(bitmap, 180);
                    //Free up the memory
                    bitmap.recycle();
                    bitmap = null;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    returnedBitmap = rotateImage(bitmap, 270);
                    //Free up the memory
                    bitmap.recycle();
                    bitmap = null;
                    break;
                default:
                    returnedBitmap = bitmap;
            }
            return returnedBitmap;
        } catch (FileNotFoundException e) {
            ExceptionUtil.handle(e);
        } catch (IOException e) {
            ExceptionUtil.handle(e);
        }
        return null;
    }

    /**
     * 旋转图片
     */
    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
    
    private void createTempFile() { //创建图片路径
        mFileTemp = new File(getExternalCacheDir(), TEMP_PHOTO_FILE_NAME);
        if(mFileTemp.exists()) {
            mFileTemp.delete();
            try {
                mFileTemp.createNewFile();
            } catch (IOException e) {
                ExceptionUtil.handle(e);
            }
        }
    }

    /**
     * This brings up the Documents app. 
     * To allow the user to also use any gallery apps they might have installed.
     * http://stackoverflow.com/questions/5309190/android-pick-images-from-gallery
     * */
    private void pickImage() { //打开系统相册
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI).setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
        try {
            startActivityForResult(chooserIntent, AppConfig.ACTIVITY_GALLERY_CHOOSE_PHOTO);
        } catch (ActivityNotFoundException e) {
            showErrorDialog(null);
        }
    }
    
    @Override
    public void OnListenerLeft() {
    	userCancelled();
    	super.OnListenerLeft();
    }
    
    @Override
    public void OnListenerRight() {
    	super.OnListenerRight();
    	saveBitmapNstartActivity();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == AppConfig.ACTIVITY_GALLERY_CHOOSE_PHOTO) {
            if (resultCode == RESULT_CANCELED) {
                userCancelled();
                return;
            } else if (resultCode == RESULT_OK) {
                try {
                	// Got the bitmap .. Copy it to the temp file for cropping
                    InputStream inputStream = getContentResolver().openInputStream(result.getData());
                    FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                    copyStream(inputStream, fileOutputStream);
                    fileOutputStream.close();
                    inputStream.close();
                    showImage(); //获取并显示图片
                } catch (Exception e) {
                    errored();
                    ExceptionUtil.handle(e);
                }
            } else {
                errored();
            }
        }
    }

    private static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public void errored() {
        Intent intent = new Intent();
        intent.putExtra(ERROR, true);
        intent.putExtra(ERROR_MSG, "Error while opening the image file. Please try again.");
        finish();
    }
    
    @Override
    public void onBackPressed() {
        finish();
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("restoreState", true);
    }

    public void userCancelled() { //返回
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
    
    private void saveBitmapNstartActivity() { //确定
        Bitmap croppedImage = getCroppedImage();
        Uri uri = null;
        if(croppedImage != null){
            String imgFileName = FileManager.getFileName();
            File file = BitmapUtil.createPath(imgFileName, false);
            if (file == null) {
            	showErrorDialog(R.string.photo_show_save_fail);
    			return;
			}
            croppedImage = BitmapUtil.getBitmap(croppedImage, GEN_OUTPUT_SIDE, GEN_OUTPUT_SIDE);
            AppApplication.saveBitmapFile(croppedImage, file, 70);
            uri = Uri.fromFile(file);
        }
        Intent intent = new Intent(this, GeneratorSelectProductActivity.class);
        intent.putExtra(AppConfig.ACTIVITY_KEY_COLLAGE_URI, uri);
        startActivity(intent);
        finish();
    }
    
    public Bitmap getCroppedImage() {
        Bitmap mCurrentDisplayedBitmap = getCurrentDisplayedImage();
        Rect displayedImageRect = ImageViewUtil.getBitmapRectCenterInside(mCurrentDisplayedBitmap, mPhotoView);

        // Get the scale factor between the actual Bitmap dimensions and the
        // displayed dimensions for width.
        float actualImageWidth = mCurrentDisplayedBitmap.getWidth();
        float displayedImageWidth = displayedImageRect.width();
        float scaleFactorWidth = actualImageWidth / displayedImageWidth;

        // Get the scale factor between the actual Bitmap dimensions and the
        // displayed dimensions for height.
        float actualImageHeight = mCurrentDisplayedBitmap.getHeight();
        float displayedImageHeight = displayedImageRect.height();
        float scaleFactorHeight = actualImageHeight / displayedImageHeight;

        // Get crop window position relative to the displayed image.
        float cropWindowX = Edge.LEFT.getCoordinate() - displayedImageRect.left;
        float cropWindowY = Edge.TOP.getCoordinate() - displayedImageRect.top;
        float cropWindowWidth = Edge.getWidth();
        float cropWindowHeight = Edge.getHeight();

        // Scale the crop window position to the actual size of the Bitmap.
        float actualCropX = cropWindowX * scaleFactorWidth;
        float actualCropY = cropWindowY * scaleFactorHeight;
        float actualCropWidth = cropWindowWidth * scaleFactorWidth;
        float actualCropHeight = cropWindowHeight * scaleFactorHeight;

        // Crop the subset from the original Bitmap.
        return Bitmap.createBitmap(mCurrentDisplayedBitmap, (int) actualCropX, (int) actualCropY, (int) actualCropWidth, (int) actualCropHeight);
    }
    
    private Bitmap getCurrentDisplayedImage() {
        Bitmap result = Bitmap.createBitmap(mPhotoView.getWidth(), mPhotoView.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(result);
        mPhotoView.draw(c);
        return result;
    }
    
}
