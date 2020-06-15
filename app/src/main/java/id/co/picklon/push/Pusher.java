package id.co.picklon.push;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import id.co.picklon.push.service.OnlineService;
import id.co.picklon.push.service.UUID;

public class Pusher {
	public static String registerPush(int appid, Context activity, String nickname){
		SharedPreferences account = activity.getSharedPreferences(Params.DEFAULT_PRE_NAME,Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = account.edit();
		editor.putString(Params.SERVER_IP, "push.astore.co.id");
//		editor.putString(Params.SERVER_IP, "120.24.89.71");
//		editor.putString(Params.SERVER_IP, "192.168.1.53");
		editor.putString(Params.SERVER_PORT, "9966");
		editor.putString(Params.PUSH_PORT, "9999");
		
		String userName = UUID.getIdentity();

		editor.putString(Params.USER_NAME, userName);
		editor.putInt(Params.APP_ID, appid);
		editor.putString(Params.SENT_PKGS, "0");
		editor.putString(Params.RECEIVE_PKGS, "0");
		editor.commit();

		Intent startSrv = new Intent(activity, OnlineService.class);
		startSrv.putExtra("CMD", "RESET");
		activity.startService(startSrv);
		
		String uuid = "";
		try{
			uuid = Util.md5(userName);
		}catch(Exception e){
			
		}
		return uuid;
	}
}
