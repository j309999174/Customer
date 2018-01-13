package com.example.administrator.customer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2017/10/23.
 */

public class NotificationService extends Service {
    NotificationManager manager;//通知控制类
    int notification_ID;

    String notitle;
    String nocontent;
    String nodate;
    String notime;
    String nolink;
    @Override
    public void onCreate() {
        super.onCreate();
    }
    public void sendNow(){
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("nolink",nolink);
        PendingIntent pintent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher);//设置图标
        builder.setTicker(notitle);//手机状态栏的提示；
        builder.setWhen(System.currentTimeMillis());//设置时间
        builder.setContentTitle(notitle);//设置标题
        builder.setContentText(nocontent);//设置通知内容
        builder.setContentIntent(pintent);//点击后的意图
//		builder.setDefaults(Notification.DEFAULT_SOUND);//设置提示声音
//		builder.setDefaults(Notification.DEFAULT_LIGHTS);//设置指示灯
//		builder.setDefaults(Notification.DEFAULT_VIBRATE);//设置震动
        builder.setDefaults(Notification.DEFAULT_ALL);//设置震动
        Notification notification = builder.build();//4.1以上
        //builder.getNotification();
        //每日设session=当前时间，如果过了当前时间，则不再发送   还没做

            manager.notify(notification_ID, notification);

    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // handler自带方法实现定时器
            try {
                handler.postDelayed(this, 24*3600*1000);
                sendNow();
                System.out.println("do...");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("exception...");
            }
        }
    };
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //要判断传值是否为空，否则崩溃
        Bundle extras = intent.getExtras();
        if (null!=extras) {
            notitle = intent.getStringExtra("notitle");
            nocontent = intent.getStringExtra("nocontent");
            nodate = intent.getStringExtra("nodate");
            notime = intent.getStringExtra("notime");
            nolink = intent.getStringExtra("nolink");
            sendNow();
            runnable.run(); //每隔1s执行
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
