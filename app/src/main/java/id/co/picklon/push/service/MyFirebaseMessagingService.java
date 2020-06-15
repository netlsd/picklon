package id.co.picklon.push.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import id.co.picklon.R;
import id.co.picklon.ui.activities.SplashActivity;
import id.co.picklon.utils.L;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        L.e("receiver msg is " + remoteMessage.getNotification().getBody());
        notification(this, getString(R.string.app_name), remoteMessage.getNotification().getBody());
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(String s) {

    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }


    protected void notification(Context iContext, String iTitle, String iMessage)
    {
        NotificationManager notificationManager = (NotificationManager) iContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());

        int notificationIcon = R.drawable.ic_app;
        CharSequence notificationTitle = iTitle;
        long when = System.currentTimeMillis();

        Intent intent = new Intent(this, SplashActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        int requestCode, notificationId;

        final Random random = new Random(System.currentTimeMillis());
        requestCode = random.nextInt();
        notificationId = random.nextInt();

        PendingIntent contentIntent = PendingIntent.getActivity(iContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(notificationIcon).setTicker(notificationTitle)
                .setContentTitle(notificationTitle)
                .setContentText(iMessage)
                .setWhen(when).setAutoCancel(true);
        Notification notification = builder.getNotification();
        notification.defaults = Notification.DEFAULT_ALL;
        notificationManager.notify(notificationId, notification);
    }
}
