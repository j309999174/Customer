package com.example.administrator.customer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/12/5.
 */

public class CountdownService extends Service{
    Timer timer = new Timer();
    public Long recLen = 0L;

    NotificationManager manager;//通知控制类
    int notification_ID=1;

    Context context=this;

    String salname;
    String cosname;



    TimerTask task = new TimerTask() {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void run() {

                    recLen--;
                    Log.e("daojishi",""+recLen);
            if(recLen == 7200){
                manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("nolink", "/customer/appointment");
                PendingIntent pintent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                Notification.Builder builder = new Notification.Builder(context);
                builder.setSmallIcon(R.drawable.ic_launcher);//设置图标
                builder.setTicker("亲！距离您的预约还有2小时");//手机状态栏的提示；
                builder.setWhen(System.currentTimeMillis());//设置时间
                builder.setContentTitle("亲！距离您的预约还有2小时");//设置标题
                builder.setContentText("预约美容院："+salname+";预约美容师："+cosname);//设置通知内容
                builder.setContentIntent(pintent);//点击后的意图
                builder.setDefaults(Notification.DEFAULT_ALL);//设置震动
                Notification notification = builder.build();//4.1以上
                //builder.getNotification();
                //每日设session=当前时间，如果过了当前时间，则不再发送   还没做

                //manager.notify(notification_ID, notification);
                startForeground(notification_ID, notification);
                Log.e("daole",""+recLen);
                notification_ID++;
            }

            if(recLen == 3600){
                manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("nolink", "/customer/appointment");
                PendingIntent pintent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                Notification.Builder builder = new Notification.Builder(context);
                builder.setSmallIcon(R.drawable.ic_launcher);//设置图标
                builder.setTicker("亲！距离您的预约还有1小时");//手机状态栏的提示；
                builder.setWhen(System.currentTimeMillis());//设置时间
                builder.setContentTitle("亲！距离您的预约还有1小时");//设置标题
                builder.setContentText("预约美容院："+salname+";预约美容师："+cosname);//设置通知内容
                builder.setContentIntent(pintent);//点击后的意图
                builder.setDefaults(Notification.DEFAULT_ALL);//设置震动
                Notification notification = builder.build();//4.1以上
                //builder.getNotification();
                //每日设session=当前时间，如果过了当前时间，则不再发送   还没做

                //manager.notify(notification_ID, notification);
                startForeground(notification_ID, notification);
                Log.e("daole",""+recLen);
                notification_ID++;
            }
                    if(recLen == 900){
                        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("nolink", "/customer/appointment");
                        PendingIntent pintent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        Notification.Builder builder = new Notification.Builder(context);
                        builder.setSmallIcon(R.drawable.ic_launcher);//设置图标
                        builder.setTicker("亲！距离您的预约还有15分钟");//手机状态栏的提示；
                        builder.setWhen(System.currentTimeMillis());//设置时间
                        builder.setContentTitle("亲！距离您的预约还有15分钟");//设置标题
                        builder.setContentText("预约美容院："+salname+";预约美容师："+cosname);//设置通知内容
                        builder.setContentIntent(pintent);//点击后的意图
                        builder.setDefaults(Notification.DEFAULT_ALL);//设置震动
                        Notification notification = builder.build();//4.1以上
                        //builder.getNotification();
                        //每日设session=当前时间，如果过了当前时间，则不再发送   还没做

                        //manager.notify(notification_ID, notification);
                        startForeground(notification_ID, notification);
                        Log.e("daole",""+recLen);
                        notification_ID++;
                    }

                    if (recLen < 0){
                        timer.cancel();
                    }
        }
    };

    /**
     * 日期转换成秒数
     * */
    public static long getSecondsFromDate(String expireDate){
        if(expireDate==null||expireDate.trim().equals(""))
            return 0;
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=null;
        try{
            date=sdf.parse(expireDate);
            return (long)(date.getTime()/1000);
        }
        catch(ParseException e)
        {
            e.printStackTrace();
            return 0L;
        }
    }
    //执行 已废除
    public void startcountdown(String expireDate){
        //时间换算秒已在js中完成
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
        long value=getSecondsFromDate(expireDate)-getSecondsFromDate(df.format(new Date()));
        Log.d("shijian",""+value);
        recLen=value;

        timer.schedule(task, 1000, 1000);
        //timer.cancel();
    }

    //执行
    public void newstartcountdown(String expireDate){
        //时间换算秒已在js中完成
        //recLen=expireDate;
        recLen=Integer.valueOf(expireDate).longValue();

        timer.schedule(task, 1000, 1000);
        //timer.cancel();
    }


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String expireDate=intent.getStringExtra("expireDate");
        salname=intent.getStringExtra("salname");
        cosname=intent.getStringExtra("cosname");
        newstartcountdown(expireDate);
        return super.onStartCommand(intent, flags, startId);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
