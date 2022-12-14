package com.example.android_bridge_and_permissions_example;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

public class ActivityMap extends AppCompatActivity {

    private WebView myWebView = null;
    private ValueCallback<Uri[]> uploadMessageAboveL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        myWebView = (WebView) findViewById( R.id.webview );
//        myWebView.clearHistory();
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        myWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        webSettings.setDomStorageEnabled( true );
        webSettings.setAppCacheMaxSize( 1024 * 1024 * 8 );
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        webSettings.setAppCachePath( appCachePath );
        webSettings.setAllowFileAccess( true );
        webSettings.setAppCacheEnabled( true );


        webSettings.setAllowFileAccess(true);
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

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, true);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
        });

        myWebView.addJavascriptInterface( new AndroidtoJs(), "test" );

//        showGPSContacts();

        myWebView.loadUrl( "https://m.amap.com?ts=" + System.currentTimeMillis());
    }

    //APP授权的回调
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0) {
                getLocation();
            } else {
                showGPSContacts();
            }
        }else if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent( ActivityMap.this, CaptureActivity.class );
                startActivityForResult( intent, 111 );
            }
        }
    }

    //获取地理位置
    LocationManager lm;
    Boolean positionNum=false;
    public void showGPSContacts() {
        lm = (LocationManager) ActivityMap.this.getSystemService(ActivityMap.this.LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(ActivityMap.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {  // 没有权限，申请权限。
                    ActivityCompat.requestPermissions(ActivityMap.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE}, 100);
                } else {
                    getLocation();
                }
            } else {
                getLocation();
            }
        } else {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 1315);
        }
    }
    private void getLocation() {
        LocationManager locationManager;
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) this.getSystemService(serviceName);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return; }
        Location location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置
        updateLocation(location);
        if(!positionNum){
            positionNum=true;
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 8, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    updateLocation(location);
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }
                @SuppressLint("MissingPermission")
                @Override
                public void onProviderEnabled(String provider) {
                    updateLocation(lm.getLastKnownLocation(provider));
                }
                @Override
                public void onProviderDisabled(String provider) {
                    updateLocation(null);
                }
            });
        }
    }
    private void updateLocation(Location location) {
        if (location != null) {
            final double latitude = location.getLatitude();
            final double longitude = location.getLongitude();
            System.out.println(latitude+":"+longitude);
            myWebView.post( new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    myWebView.evaluateJavascript( "javascript:test.getPositionFn('" + latitude+"','"+longitude+ "')", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) { }
                    } );
                }
            } );
        } else {
            System.out.println("无法获取到位置信息");
        }
    }

    private class AndroidtoJs extends Object {
        @JavascriptInterface
        public void hello(String msg) {
            final String content = msg;
            myWebView.post( new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    myWebView.evaluateJavascript( "javascript:test.helloFn('hello,"+content+"')", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) { }
                    } );
                }
            } );
        }

        //启动扫描二维码
        @JavascriptInterface
        public void scanQRCode(String msg) {
            if (ContextCompat.checkSelfPermission( ActivityMap.this, android.Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions( ActivityMap.this, new String[]{Manifest.permission.CAMERA}, 1 );
            } else {
                Intent intent = new Intent( ActivityMap.this, CaptureActivity.class );
                startActivityForResult( intent, 111 );
            }
        }
    }

    //获取当前地理位置
    @JavascriptInterface
    public void getPosition(String msg) {
        showGPSContacts();
    }
}