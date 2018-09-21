package com.autocontrol.coc.cocautomanagement.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.autocontrol.coc.cocautomanagement.taskai.Task;
import com.autocontrol.coc.cocautomanagement.taskai.TaskManage;

import java.util.ArrayList;

/**
 * Created by mac_py on 18/09/2018.
 */

public class LongRunningService extends Service {

    private TaskManage taskManage;
    private Thread thread;

    @Override
    public void onCreate() {
        super.onCreate();
        taskManage = new TaskManage(getApplicationContext());
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (thread == null) {
            thread = new Thread(new Runnable() {

                @Override

                public void run() {
                    while (taskManage.hasTask()) {
                        taskManage.executeTask();
                    }
                }

            });
            thread.start();
        } else {
            if (taskManage.hasTask()) {
                thread.start();
            }
        }

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        int anHour = 30 * 1000;//每隔30秒检查一次任务机

        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);

        manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }
}
