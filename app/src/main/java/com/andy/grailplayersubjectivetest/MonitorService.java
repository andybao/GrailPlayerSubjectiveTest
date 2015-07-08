package com.andy.grailplayersubjectivetest;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

/**
 * Created by andy on 15-7-3.
 */
public class MonitorService extends Service {

    String TAG = "SubjectiveAuto";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

                if (cn.getClassName().equals("org.libsdl.app.SDLActivity")) {

                    Log.d(TAG, "MonitorService: " + "org.libsdl.app.SDLActivity is running");

                }

                else {

                    ContentMontior contentMontior = new ContentMontior();

                    if (contentMontior.testingFileExist()) {
                        contentMontior.renameContentToOriginal();
                    }

                }

            }
        }).start();

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int second = 500;
        //Todo: change time to 500

        //Todo: another alarm setter need more investigate
        //long triggerAtTime = SystemClock.currentThreadTimeMillis() + second;
        long triggerAtTime = System.currentTimeMillis() + second;
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        //manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        manager.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startID);
    }

    public class ContentMontior {

        String contentRootPath = Environment.getExternalStorageDirectory().toString()
                + "/Test-Folder/";
        String testingContentPath = contentRootPath + "Test.mp4";
        String monitorServerInfoFile = "montior_server_info";
        String monitorServerInfoKey = "motior_server_key";

        public void renameContentToOriginal() {

            File file = new File(testingContentPath);
            file.renameTo(new File(getContentOriginalPath()));
        }

        public String getContentOriginalPath() {

            String contentOriginalPath;

            SharedPreferences monitorServerInfo = getSharedPreferences(monitorServerInfoFile, MODE_PRIVATE);
            contentOriginalPath = monitorServerInfo.getString(monitorServerInfoKey, "");

            return contentOriginalPath;
        }

        public boolean testingFileExist() {

            File file = new File(testingContentPath);

            if (file.exists()) {
                return true;
            }
            else {
                return false;
            }

        }

    }

}
