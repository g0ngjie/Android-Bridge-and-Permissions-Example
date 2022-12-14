package com.example.android_bridge_and_permissions_example;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

public class ActivityFile extends AppCompatActivity {

    private WebView myWebView = null;
    private ValueCallback<Uri[]> uploadMessageAboveL;

    private Uri imageUri;
    private final static int PHOTO_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        myWebView = (WebView) findViewById( R.id.webview_file );

        myWebView.setWebChromeClient( new WebChromeClient(){
//            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams ) {
//                if (uploadMessageAboveL != null) {
//                    uploadMessageAboveL.onReceiveValue(null);
//                    uploadMessageAboveL = null;
//                }
//                uploadMessageAboveL = filePathCallback;
//                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//                i.addCategory(Intent.CATEGORY_OPENABLE);
//                i.setType("image/*");
//                startActivityForResult( Intent.createChooser(i, "Image Chooser"), 2);
//                return true;
//            }


//            @Override
//            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
//                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
//            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                //调用系统相机或者相册
//                showPhotoChooser();
                System.out.println("进入文件操作");
                return true;
            }
        });

        myWebView.loadUrl("file:///android_asset/web/uploadFile.html");
    }

    /**
     * 打开选择文件/相机
     */
    private void showPhotoChooser() {

        Intent intentPhoto = new Intent(Intent.ACTION_PICK);
        intentPhoto.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//        Intent intentPhoto = new Intent(Intent.ACTION_GET_CONTENT);
//        intentPhoto.addCategory(Intent.CATEGORY_OPENABLE);
//        intentPhoto.setType("*/*");
        File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/" + SystemClock.currentThreadTimeMillis() + ".jpg");
        imageUri = Uri.fromFile(fileUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", fileUri);//通过FileProvider创建一个content类型的Uri
        }
        //调用系统相机
        Intent intentCamera = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //将拍照结果保存至photo_file的Uri中，不保留在相册中
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_TITLE, "Photo Chooser");
        chooser.putExtra(Intent.EXTRA_INTENT, intentPhoto);
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intentCamera});
        startActivityForResult(chooser, PHOTO_REQUEST);
    }

}