//包名换成自己的工程包名
package com.happycacr.screencapturedemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import java.nio.ByteBuffer;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MEDIA_PROJECTION_SERVICE;


/*使用说明:
*   `1.在主活动中创建一个类的实例,构造函数的参数为this
*   `2.调用初始化函数(此步骤目前被取消,直接在步骤1完成)
*    3.将onActivityResult函数放置于主活动同名接口中
*    4.调用applyPower申请权限,申请结果由步骤3中函数返回
*    5.调用getScreenCapture截屏
* */
public class ScreenCapture {
    public final static String TAG = "ScreenCapture";
    private final static int REQUEST_MEDIA_PROJECTION = 0;
    private  MediaProjection mMediaProjection;
    private  MediaProjectionManager mMediaProjectionManager;
    private  VirtualDisplay mVirtualDisplay;
    private  Intent mScreenCaptureIntent;
    private  ImageReader mImageReader;
    private  AppCompatActivity  mMainActivity;
    private int mWidth;
    private int mHeight;
    private float mScreenDensity;

    //构造函数
    public ScreenCapture(AppCompatActivity mainactivity){

        //获得程序入口活动的指针,在主活动的onCreate回调接口中new本类的一个实例,参数为:this
        mMainActivity = mainactivity;

        //初始化动作在后期应该由自己主动调用,这里图省事放在构造函数内,如果改为主动初始化则删除这里
        Init();
    }

    //初始化,因为构造函数不返回信息,所以这里预留了初始化函数,需要的花在里面加上错误判断语句
    //在没有加上判断错误的时候,直接由构造函数调用,如果想更健壮将他改为public并在其中加上错误处理
    private boolean Init() {
        //获取设备信息
        mMediaProjectionManager = (MediaProjectionManager) mMainActivity.getSystemService(MEDIA_PROJECTION_SERVICE);
        Display display = mMainActivity.getWindowManager().getDefaultDisplay();
        WindowManager windowManager = (WindowManager) mMainActivity.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        mWidth = displayMetrics.widthPixels;
        mHeight = displayMetrics.heightPixels;
        display.getMetrics(displayMetrics);
        mScreenDensity = displayMetrics.density;
        return true;
    }

    //申请截屏的权限
    //在调用getScreenCapture函数前必须在主活动中提前申请权限
    public void applyPower(){
        //创建用于截图的Intent并为其申请权限
        mScreenCaptureIntent = mMediaProjectionManager.createScreenCaptureIntent();
        mMainActivity.startActivityForResult(mScreenCaptureIntent, REQUEST_MEDIA_PROJECTION);
    }

    //此发放放置于主活动的onActivityResult接口中,用于判断是否得到了权限
    @SuppressLint("WrongConstant") //函数中屏蔽 PixelFormat.RGBA_8888 非法常量提醒
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //这里用switch是为了可以添加如果没有获得权限的判断和处理
        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION: //如果得到了权限
                if (resultCode == RESULT_OK) {
                    mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 1);
                    mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
                    mVirtualDisplay = mMediaProjection.createVirtualDisplay("mediaprojection",
                            mWidth, mHeight,(int) mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                            mImageReader.getSurface(), null, null);
                    break;
                }
        }
    }

    //截屏并返回包含截屏内容的Bitmap
    public Bitmap getScreenCapture() {
        if(mImageReader == null) {return null;}
        Image image = mImageReader.acquireLatestImage();
        if (image != null) {
            final Image.Plane[] planes = image.getPlanes();
            if (planes.length > 0) {
                final ByteBuffer buffer = planes[0].getBuffer();
                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * mWidth;
                Bitmap bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(buffer);
                image.close();
                return bitmap;
            }
        }
        return null;
    }
}
