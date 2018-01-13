package com.example.administrator.customer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.rbj.zxing.decode.QrcodeDecode;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;
    String targetUrl;
    WebView webview;
    private long exitTime = 0;
    //172.114.10.238
    //192.168.1.106
    static String webaddress="47.96.173.116";
    static int salnumber=123;

    private static final int SDK_PAY_FLAG = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webview = (WebView) findViewById(R.id.webview);
        assert webview != null;
        WebSettings settings = webview.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);


        webview.setWebViewClient(new WebViewClient(){

            //无网处理
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                view.loadUrl("file:///android_asset/error.html");
            }

            public boolean shouldOverviewUrlLoading(WebView   view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webview.setWebChromeClient(new WebChromeClient() {

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                openImageChooserActivity();
                return true;
            }
        });
        //默认的主页
        targetUrl = "http://"+webaddress+"/customer/homepage/"+salnumber;

        //扫码页的跳转
        //Bundle extras = getIntent().getExtras();
        String result=getIntent().getStringExtra(QrcodeDecode.BARCODE_RESULT);
        if (null != result) {
            targetUrl =result;
        }
        //通知页的跳转

        String nolink = getIntent().getStringExtra("nolink");

        if (null != nolink) {
            targetUrl = "http://"+webaddress+nolink;
            //Toast.makeText(getApplicationContext(), targetUrl, Toast.LENGTH_LONG).show();
        }

        webview.loadUrl(targetUrl);
        webview.addJavascriptInterface(MainActivity.this,"android");


    }
    @android.webkit.JavascriptInterface
    public void qr(){
        //扫码
        startActivity(new Intent(MainActivity.this,QRActivity.class));
    }
    @android.webkit.JavascriptInterface
    public void countdown(String expireDate,String salname,String cosname){
        Log.e("henhaodejishiqi",""+expireDate);
        //倒计时
        Intent intent3=new Intent(MainActivity.this,CountdownService.class);
        stopService(intent3);
        intent3.putExtra("expireDate",expireDate);
        intent3.putExtra("salname",salname);
        intent3.putExtra("cosname",cosname);
        startService(intent3);
    }
    //用户登陆后id的储存，mysql的查询
    @android.webkit.JavascriptInterface
    public void cusidsave(final int cusid){
        Log.d("nihao", "cusid"+cusid);
        SharedPreferences sharedPreferences=getSharedPreferences("mycusid",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("cusid",cusid);
        editor.putInt("salnumber",salnumber);
        editor.commit();
        Log.d("nihao", "cusid"+cusid);
        //通知线程的开始
        new Thread(newrunnable).start();
    }
    @android.webkit.JavascriptInterface
    public void alipay(final String orderinfo){

        //支付宝
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(MainActivity.this);
                Map<String, String> result = alipay.payV2(orderinfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(MainActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(MainActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }

                default:
                    break;
            }
        };
    };
    //图片上传
    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "图片选择"), FILE_CHOOSER_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }





    //按2次后退退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()){
            webview.goBack();
            return true;
        }else{
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
        return true;
    }

    //TODO 通知的线程 已废除
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //xml
            GetNo getNo =new GetNo();
            try {
                String[] newNO= getNo.getNo("http://"+webaddress+"/customerajax/notificationxml/"+salnumber);
                if (!newNO[0].equals(null)){
                    Intent intent1=new Intent(MainActivity.this,NotificationService.class);
                    intent1.putExtra("notitle",newNO[0]);
                    intent1.putExtra("nocontent",newNO[1]);
                    intent1.putExtra("nodate",newNO[2]);
                    intent1.putExtra("notime",newNO[3]);
                    intent1.putExtra("nolink",newNO[4]);
                    startService(intent1);}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    //TODO 最新通知
    Runnable newrunnable = new Runnable() {
        @Override
        public void run() {

            Intent intent1=new Intent(MainActivity.this,NewNotificationService.class);
            startService(intent1);
            Intent intent2=new Intent(MainActivity.this,MessagenoteService.class);
            startService(intent2);

        }
    };
}
