package com.example.administrator.customer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/11/21.
 */

public class NewNotificationService extends Service {
    NotificationManager manager;//通知控制类
    int notification_ID;


    String notitle;
    String nocontent;
    String nodate;
    String notime;
    String nolink;
    Context context=this;
    public void sendNow(){
        //1.取出cusid  2.mysql查询  3.通知
        //1.取出cusid
        SharedPreferences sharedPreferences=getSharedPreferences("mycusid",MODE_PRIVATE);
        int cusid=sharedPreferences.getInt("cusid",0);
        final int salnumber=sharedPreferences.getInt("salnumber",0);
        //2.mysql查询
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                try {
                    //注册驱动
                    Class.forName("com.mysql.jdbc.Driver");
                    String url = "jdbc:mysql://47.96.173.116:3306/company";
                    java.sql.Connection conn = DriverManager.getConnection(url, "cusmysql", "j12321456");
                    Statement stmt = conn.createStatement();
                    //当天日期
                    Date dNow = new Date( );
                    SimpleDateFormat ft = new SimpleDateFormat ("yyyyMMdd");
                    String sql = "select * from notificationinfo where salnumber="+salnumber+" and comparenodate="+ft.format(dNow);
                    Log.e("datestr",sql);
                    ResultSet rs = stmt.executeQuery(sql);

                    //储存此条通知ID ,判断是否已发此条通知
                    SharedPreferences sharedPreferencesnotification=getSharedPreferences("mynotification",MODE_PRIVATE);
                    SharedPreferences.Editor editornotification=sharedPreferencesnotification.edit();

                    manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    while (rs.next()) {
                        Log.v("yzy", "field1-->"+rs.getString(1)+"  field2-->"+rs.getString(2));
                        //3.通知
                        //判断通知id是否大于已储存的id，大于（表名是新的）则发送
                        if (sharedPreferencesnotification.getInt("noticeid",0)<rs.getInt(1)) {
                            notification_ID = rs.getInt(1);
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.putExtra("nolink", rs.getString(3));
                            PendingIntent pintent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                            Notification.Builder builder = new Notification.Builder(context);
                            builder.setSmallIcon(R.drawable.corporatelogo);//设置图标
                            builder.setTicker(rs.getString(4));//手机状态栏的提示；
                            builder.setWhen(System.currentTimeMillis());//设置时间
                            builder.setContentTitle(rs.getString(4));//设置标题
                            builder.setContentText(rs.getString(5));//设置通知内容
                            builder.setContentIntent(pintent);//点击后的意图
                            builder.setDefaults(Notification.DEFAULT_ALL);//设置震动
                            Notification notification = builder.build();//4.1以上
                            //builder.getNotification();
                            //每日设session=当前时间，如果过了当前时间，则不再发送   还没做
                            Log.v("not", "" + notification_ID);
                            manager.notify(notification_ID, notification);
                            //startForeground(notification_ID, notification);


                            editornotification.putInt("noticeid",rs.getInt(1));
                            editornotification.commit();
                        }
                    }
                    rs.close();
                    stmt.close();
                    conn.close();
                    Log.v("yzy", "success to connect!");

                }catch(ClassNotFoundException e)
                {
                    Log.v("yzy", "fail to connect!"+"  "+e.getMessage());
                } catch (SQLException e)
                {
                    Log.v("yzy", "fail to connect!"+"  "+e.getMessage());
                }
            }
        };
        new Thread(runnable).start();






    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // handler自带方法实现定时器
            try {
                //每天都执行
                handler.postDelayed(this, 1000*10);
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
    public void onCreate() {
        super.onCreate();
        runnable.run();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
