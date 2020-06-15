package id.co.picklon.push.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;

import org.ddpush.im.v1.client.appuser.Message;
import org.ddpush.im.v1.client.appuser.TCPClientBase;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import id.co.picklon.R;
import id.co.picklon.push.Params;
import id.co.picklon.push.Push;
import id.co.picklon.push.Util;
import id.co.picklon.push.receiver.TickAlarmReceiver;
import id.co.picklon.ui.activities.SplashActivity;


public class OnlineService extends Service {

    protected PendingIntent tickPendIntent;
    WakeLock wakeLock;
    MyUdpClient myUdpClient;

    public class MyUdpClient extends TCPClientBase {

        public MyUdpClient(byte[] uuid, int appid, String serverAddr, int serverPort)
                throws Exception {
            super(uuid, appid, serverAddr, serverPort, 5000);

        }

        @Override
        public boolean hasNetworkConnection() {
            return Util.hasNetwork(OnlineService.this);
        }


        @Override
        public void trySystemSleep() {
            tryReleaseWakeLock();
        }

        @Override
        public void onPushMessage(Message message) {
            if (message == null) {
                return;
            }
            if (message.getData() == null || message.getData().length == 0) {
                return;
            }

//            if (message.getCmd() == 16) {// 0x10 通用推送信息
//                notifyUser(16, "Picklon通用推送信息", "时间：" + DateTimeUtil.getCurDateTime(), "收到通用推送信息");
//            }
//            if (message.getCmd() == 17) {// 0x11 分组推送信息
//                long msg = ByteBuffer.wrap(message.getData(), 5, 8).getLong();
//                notifyUser(17, "DDPush分组推送信息", "" + msg, "收到通用推送信息");
//            }
//            if (message.getCmd() == 32) {// 0x20 自定义推送信息
//                String str = null;
//                try {
//                    str = new String(message.getData(), 5, message.getContentLength(), "UTF-8");
//                } catch (Exception e) {
//                    str = Util.convert(message.getData(), 5, message.getContentLength());
//                }
//
//                L.e("str is " + str);
//
//                notifyUser(32, "Picklon自定义推送信息", "" + str, "收到自定义推送信息");
//            }

            String str;
            try {
                str = new String(message.getData(), 5, message.getContentLength(), "UTF-8");
            } catch (Exception e) {
                str = Util.convert(message.getData(), 5, message.getContentLength());
            }

            Push push = new Gson().fromJson(str, Push.class);
            notifyUser(message.getCmd(), "Picklon", push.getMsg(), push.getImage());

            setPkgsInfo();
        }

    }

    public OnlineService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.setTickAlarm();

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "OnlineService");

        resetClient();
    }

    @Override
    public int onStartCommand(Intent param, int flags, int startId) {
        if (param == null) {
            return START_STICKY;
        }
        String cmd = param.getStringExtra("CMD");
        if (cmd == null) {
            cmd = "";
        }
        if (cmd.equals("TICK")) {
            if (wakeLock != null && wakeLock.isHeld() == false) {
                wakeLock.acquire();
            }
        }
        if (cmd.equals("RESET")) {
            if (wakeLock != null && wakeLock.isHeld() == false) {
                wakeLock.acquire();
            }
            resetClient();
        }
        if (cmd.equals("TOAST")) {
            String text = param.getStringExtra("TEXT");
            if (text != null && text.trim().length() != 0) {
                Toast.makeText(this, text, Toast.LENGTH_LONG).show();
            }
        }

        setPkgsInfo();

        return START_STICKY;
    }

    protected void setPkgsInfo() {
        if (this.myUdpClient == null) {
            return;
        }
        long sent = myUdpClient.getSentPackets();
        long received = myUdpClient.getReceivedPackets();
        SharedPreferences account = this.getSharedPreferences(Params.DEFAULT_PRE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = account.edit();
        editor.putString(Params.SENT_PKGS, "" + sent);
        editor.putString(Params.RECEIVE_PKGS, "" + received);
        editor.commit();
    }

    protected void resetClient() {
        SharedPreferences account = this.getSharedPreferences(Params.DEFAULT_PRE_NAME, Context.MODE_PRIVATE);
        String serverIp = account.getString(Params.SERVER_IP, "");
        String serverPort = account.getString(Params.SERVER_PORT, "");
        String pushPort = account.getString(Params.PUSH_PORT, "");
        String userName = account.getString(Params.USER_NAME, "");
        if (serverIp == null || serverIp.trim().length() == 0
                || serverPort == null || serverPort.trim().length() == 0
                || pushPort == null || pushPort.trim().length() == 0
                || userName == null || userName.trim().length() == 0) {
            return;
        }
        if (this.myUdpClient != null) {
            try {
                myUdpClient.stop();
            } catch (Exception e) {
            }
        }
        try {
            myUdpClient = new MyUdpClient(Util.md5Byte(userName), 1, serverIp, Integer.parseInt(serverPort));
            myUdpClient.setHeartbeatInterval(50);
            myUdpClient.start();
            SharedPreferences.Editor editor = account.edit();
            editor.putString(Params.SENT_PKGS, "0");
            editor.putString(Params.RECEIVE_PKGS, "0");
            editor.commit();
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(), "操作失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
//        Toast.makeText(this.getApplicationContext(), "ddpush：终端重置", Toast.LENGTH_LONG).show();
    }

    protected void tryReleaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld() == true) {
            wakeLock.release();
        }
    }

    protected void setTickAlarm() {
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TickAlarmReceiver.class);
        int requestCode = 0;
        tickPendIntent = PendingIntent.getBroadcast(this,
                requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //小米2s的MIUI操作系统，目前最短广播间隔为5分钟，少于5分钟的alarm会等到5分钟再触发！2014-04-28
        long triggerAtTime = System.currentTimeMillis();
        int interval = 300 * 1000;
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime, interval, tickPendIntent);
    }

    protected void cancelTickAlarm() {
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(tickPendIntent);
    }

    protected void cancelNotifyRunning() {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

    public void notifyUser(int id, String title, String content, String image) {
        id = (int) (Math.random() * 10000);
        Intent contentIntent = new Intent(this, SplashActivity.class);
        contentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent intent = PendingIntent.getActivity(this, 0, contentIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(title)
                .setSmallIcon(R.drawable.ic_app)
                .setContentText(content)
                .setContentIntent(intent)
                .setWhen(0)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        if (!TextUtils.isEmpty(image)) {
            Bitmap bitmap = getBitmapFromURL(image);
            if (bitmap != null) {
                mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));
            }
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, mBuilder.build());
    }

    public String encodeUrl(String url) {
        return Uri.encode(url, "-![.:/,%?&=]");
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(encodeUrl(src));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //this.cancelTickAlarm();
        cancelNotifyRunning();
        this.tryReleaseWakeLock();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
