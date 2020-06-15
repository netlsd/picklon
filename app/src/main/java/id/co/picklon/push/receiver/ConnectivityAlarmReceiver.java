package id.co.picklon.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import id.co.picklon.push.Util;
import id.co.picklon.push.service.OnlineService;


public class ConnectivityAlarmReceiver extends BroadcastReceiver {

	public ConnectivityAlarmReceiver() {
		super();
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		if(Util.hasNetwork(context) == false){
			return;
		}
		Intent startSrv = new Intent(context, OnlineService.class);
		startSrv.putExtra("CMD", "RESET");
		context.startService(startSrv);
	}

}
