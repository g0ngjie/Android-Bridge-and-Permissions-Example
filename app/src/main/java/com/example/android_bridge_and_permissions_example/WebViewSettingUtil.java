package com.example.android_bridge_and_permissions_example;

import android.annotation.SuppressLint;
import android.os.Build;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;

import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.DefaultHandler;

public class WebViewSettingUtil {

    @SuppressLint("SetJavaScriptEnabled")
    public static void initWebView(BridgeWebView webView) {
        webView.setDefaultHandler(new DefaultHandler());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new BridgeWebViewClient(webView));
        // 如果不加这一行，当点击界面链接，跳转到外部时，会出现net::ERR_CACHE_MISS错误
        if (Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
    }
}