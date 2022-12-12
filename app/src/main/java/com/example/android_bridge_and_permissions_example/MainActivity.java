package com.example.android_bridge_and_permissions_example;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;

public class MainActivity extends AppCompatActivity {

    private BridgeWebView mWebView;
    private EditText mEdt;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mContext = this;

        initView();
        setClick();
        registerHandlers();

//        View btn_login = findViewById(R.id.btn_login);
//        btn_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println(v);
//            }
//        });
    }

    private void initView() {
        mEdt = findViewById(R.id.edt);
        initWebView();
    }

    private void initWebView() {
        mWebView = findViewById(R.id.webView);
        WebViewSettingUtil.initWebView(mWebView);
        mWebView.loadUrl("file:///android_asset/web/index.html");
    }

    //注册js调用原生的方法
    private void registerHandlers() {
        mWebView.registerHandler("modifyAppEditValue", new BridgeHandler() {
            //data是js传递给原生的数据，CallBackFunction是js的回调
            @Override
            public void handler(String data, CallBackFunction function) {
                mEdt.setText(data);
                function.onCallBack("app的输入框内容已被修改啦");
            }
        });

        mWebView.registerHandler("clearAppEdt", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                mEdt.setText("");
                function.onCallBack("app的输入框内容已被清空啦");
            }
        });
    }

    //原生调用js的方法
    private void setClick() {
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mEdt.getText().toString();
                //原生调用js的方法(content是传递给js的数据)
                mWebView.callHandler("modifyH5InputValue", content, new CallBackFunction() {
                    //原生调用完js方法后的回调(data是js回传给原生的数据)
                    @Override
                    public void onCallBack(String data) {
                        Toast.makeText(mContext, data, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //原生调用js的方法
                mWebView.callHandler("clearH5Input", "", new CallBackFunction() {
                    //原生调用完js方法后的回调(data是js回传给原生的数据)
                    @Override
                    public void onCallBack(String data) {
                        Toast.makeText(mContext, data, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            } else {
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}