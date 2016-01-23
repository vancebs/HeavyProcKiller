package com.hf.heavyprockiller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by Fan on 2016/1/23.
 */
public class KillerService extends Service {
    public static final String TAG = "KillerService";
    public static final String ACTION_KILL = "com.hf.heavyprocesskiller.CHECK_AND_KILL";
    public static final long DURATION = 30 * 60 * 1000; // 30 mins

    private Handler mHandler = new Handler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        if (ACTION_KILL.equals(intent.getAction())) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    kill();
                }
            });
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void kill() {
        HeavyProcKiller killer = new HeavyProcKiller();
        int count = killer.killHeavyProc();
        if (count > 0) {
            Toast.makeText(this, "" + count + " proceses killed.", Toast.LENGTH_SHORT).show();
        }

        Log.i(TAG, "onStartCommand()# " + count + " processes killed.");

        // next kill
        nextKill();
    }

    private void nextKill() {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent serviceIntent = new Intent(this, KillerService.class);
        serviceIntent.setAction(KillerService.ACTION_KILL);
        PendingIntent pi = PendingIntent.getService(this, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // next kill after 30 mins
        am.cancel(pi);
        am.set(AlarmManager.RTC, System.currentTimeMillis() + DURATION, pi);
    }
}
