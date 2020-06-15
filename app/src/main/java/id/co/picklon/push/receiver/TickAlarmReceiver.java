package id.co.picklon.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager.WakeLock;

import id.co.picklon.push.Util;
import id.co.picklon.push.service.OnlineService;


public class TickAlarmReceiver extends BroadcastReceiver {

    WakeLock wakeLock;

    public TickAlarmReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Util.hasNetwork(context) ) {
            return;
        }
        Intent startSrv = new Intent(context, OnlineService.class);
        startSrv.putExtra("CMD", "TICK");
        context.startService(startSrv);
    }

}
