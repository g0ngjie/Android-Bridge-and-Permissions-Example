package com.example.android_bridge_and_permissions_example;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;

import java.util.HashMap;
import java.util.Map;

public class ActivityBridge extends AppCompatActivity {

    private BridgeWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridge);

        webView = findViewById(R.id.bridge_webview);
        WebViewSettingUtil.initWebView(webView);
        webView.loadUrl("file:///android_asset/web/bridge.html");

        registerHandlers();
        registerBindBtn();
    }

    //注册js调用原生的方法
    private void registerHandlers() {
        webView.registerHandler("isLogin", new BridgeHandler() {
            //data是js传递给原生的数据，CallBackFunction是js的回调
            @Override
            public void handler(String data, CallBackFunction function) {
                Map res = new HashMap();
                res.put("login", true);
                String jsonStr = JSON.toJSONString(res);
                function.onCallBack(jsonStr);
            }
        });

        // 获取车机Vin号
        webView.registerHandler("getVinCode", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                function.onCallBack("xxxx12345678");
            }
        });

        // 随便给车机传点值
        webView.registerHandler("ping", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                System.out.println("收到H5传过来的参数: " + data);
                function.onCallBack("pong");
            }
        });
    }

    //原生调用js的方法
    private void registerBindBtn() {
        findViewById(R.id.invoke_h5_func).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map params = new HashMap();
                params.put("test", "okk");
                String content = JSON.toJSONString(params);
                webView.callHandler("waitByNativeInvoke", content, new CallBackFunction() {
                    //原生调用完js方法后的回调(data是js回传给原生的数据)
                    @Override
                    public void onCallBack(String data) {
                        // h5 回传
                        System.out.println(data);
                    }
                });
            }
        });
    }
}