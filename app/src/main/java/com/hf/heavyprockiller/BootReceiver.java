package com.hf.heavyprockiller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Fan on 2016/1/23.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, KillerService.class);
        serviceIntent.setAction(KillerService.ACTION_KILL);
        context.startService(serviceIntent);
    }
}
