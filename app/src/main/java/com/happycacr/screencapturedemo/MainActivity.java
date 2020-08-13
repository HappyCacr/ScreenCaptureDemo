package com.happycacr.screencapturedemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

// IDE == Android Studio 4
// Demo内容为测试截屏,一个按钮用于申请权限,另一个截屏,截屏内容显示在一个ImageView

public class MainActivity extends AppCompatActivity {
    public ScreenCapture mScreenCapture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获得一个截屏功能的实例
        mScreenCapture = new ScreenCapture(this);


        //申请截屏权限
        Button button1 = (Button) findViewById(R.id.btn1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mScreenCapture.applyPower();
            }
        });
        //测试截图功能
        final ImageView imageView = (ImageView)findViewById(R.id.iv1);
        Button button2 = (Button) findViewById(R.id.btn2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap3 = mScreenCapture.getScreenCapture();
                imageView.setImageBitmap(bitmap3);
                int a = 0;
            }
        });
    }

    //接收权限申请的返回
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mScreenCapture.onActivityResult(requestCode,resultCode,data);
    }
}