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

/**
 * Created by Administrator on 2017/11/21.
 */

public class MessagenoteService extends Service {
    NotificationManager manager;//通知控制类
    int notification_ID=1;

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
        final int cusid=sharedPreferences.getInt("cusid",0);
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
                    String sql = "select * from unread where receiveid='cus"+cusid+"' and number>0";
                    Log.e("datestr",sql);
                    ResultSet rs = stmt.executeQuery(sql);

                    //储存未读条数 ,判断是否有此条数改变，储存条数
                    SharedPreferences sharedPreferencesunread=getSharedPreferences("myunread",MODE_PRIVATE);
                    SharedPreferences.Editor editorunread=sharedPreferencesunread.edit();

                    //manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    while (rs.next()) {
                        Log.v("mns", "field1-->"+rs.getString(1)+"  field2-->"+rs.getString(2));
                        //判断发送方的未读信息数量是否改变，改变则执行通知，并重新储存未读数量
                        if (sharedPreferencesunread.getInt(rs.getString(2),0)!=rs.getInt(4)) {
                            editorunread.putInt(rs.getString(2),rs.getInt(4));
                            editorunread.commit();
                            //3.通知
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.putExtra("nolink", "/customer/cosmetologist");
                            PendingIntent pintent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                            Notification.Builder builder = new Notification.Builder(context);
                            builder.setSmallIcon(R.drawable.ic_launcher);//设置图标
                            builder.setTicker("您有新的未读消息");//手机状态栏的提示；
                            builder.setWhen(System.currentTimeMillis());//设置时间
                            builder.setContentTitle("您有新的未读消息");//设置标题
                            builder.setContentText(rs.getString(5)+":"+rs.getString(6));//设置通知内容
                            builder.setContentIntent(pintent);//点击后的意图
                            builder.setDefaults(Notification.DEFAULT_ALL);//设置震动
                            Notification notification = builder.build();//4.1以上
                            //builder.getNotification();
                            //每日设session=当前时间，如果过了当前时间，则不再发送   还没做

                            //manager.notify(notification_ID, notification);
                            startForeground(notification_ID, notification);

                            Log.v("unreadid", "" + notification_ID);
                            notification_ID+=notification_ID;
                        }
                    }
                    rs.close();
                    stmt.close();
                    conn.close();
                    Log.v("unreadid", sharedPreferencesunread.getInt("unreadid",0)+"");

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
    public int onStartCommand(Intent intent1, int flags, int startId) {

        //前台通知，保持服务
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("nolink", "/customer/cosmetologist");
        PendingIntent pintent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.ic_launcher);//设置图标
        builder.setTicker("");//手机状态栏的提示；
        builder.setWhen(System.currentTimeMillis());//设置时间
        builder.setContentTitle("美容师为您服务");//设置标题
        builder.setContentText("亲，有任何疑问或意见，随时咨询");//设置通知内容
        builder.setContentIntent(pintent);//点击后的意图
        //builder.setDefaults(Notification.DEFAULT_ALL);//设置震动
        Notification notification = builder.build();//4.1以上
        startForeground(notification_ID, notification);

        return super.onStartCommand(intent1, flags, startId);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
