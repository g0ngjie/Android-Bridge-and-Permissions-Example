package com.example.android_bridge_and_permissions_example;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.lzyzsd.jsbridge.BridgeWebView;

public class ActivityMap extends AppCompatActivity {

    private BridgeWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initWebView();
    }

    private void initWebView() {
        webView = findViewById(R.id.webView);
        WebViewSettingUtil.initWebView(webView);
        webView.loadUrl("https://m.amap.com");
    }
}