package cn.sun45.postingmatch.server;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sun45 on 2022/5/2
 * 求职服务
 */
public class PostingServer extends Service {
    private static final String TAG = "PostingServer";

    //定时运行线程池
    private ScheduledExecutorService scheduledThreadPool;
    private ScheduledFuture future;
    private Runnable tickRunnable = new Runnable() {
        @Override
        public void run() {
            if (listener != null) {
                listener.pullNewPosting();
            }
        }
    };
    private int tickInterval = 30;

    private PostingServerListener listener;

    public void setListener(PostingServerListener listener) {
        this.listener = listener;
    }

    public class MyBinder extends Binder {
        public PostingServer getService() {
            return PostingServer.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "default";
            NotificationChannel channel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                if (nm.getNotificationChannel(channelId) == null) {//没有创建
                    nm.createNotificationChannel(channel);//则先创建
                }
            }
            Notification notification;
            Notification.Builder builder = new Notification.Builder(this, channelId)
                    .setContentTitle("")
                    .setContentText("");
            notification = builder.build();
            startForeground(110, notification);
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        if (scheduledThreadPool != null) {
            if (!scheduledThreadPool.isShutdown()) {
                future.cancel(true);
                scheduledThreadPool.shutdown();
            }
        }
        scheduledThreadPool = Executors.newScheduledThreadPool(1);
        future = scheduledThreadPool.scheduleAtFixedRate(tickRunnable, 0, tickInterval, TimeUnit.SECONDS);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }
}
