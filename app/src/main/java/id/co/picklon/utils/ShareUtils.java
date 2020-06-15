package id.co.picklon.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.io.File;

import id.co.picklon.R;
import id.co.picklon.push.service.FileUtils;

public class ShareUtils {
    private static final String SHARE_CONTENT = "Picklon is an on-demand laundry service available through a mobile app. We make a pickup your laundry, clean it and delivery it back to you. You can schedule a pick-up, delivery time and tag a location just with  a simple tap on your mobile device.";
    private static final String SMS_SHARE_CONTENT = "Ayo download aplikasi Picklon sekarang di appstore maupun google playstore! Info klik www.picklon.com";
    public static final String SHARE_FILENAME = "/.picklon_share.png";

    public static void shareToFacebook(Context context) {
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("http://www.picklon.com"))
                .setContentTitle(SHARE_CONTENT)
                .setContentDescription("picklon")
                .build();
        ShareDialog shareDialog = new ShareDialog((Activity) context);
        shareDialog.show(content);
    }

    public static void shareToInstagram(Context context) {
        String pkgName = "com.instagram.android";

        try {
            context.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context, R.string.no_install_instagram, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_STREAM, getInsShareUri(context));
        intent.setPackage(pkgName);
        context.startActivity(intent);
    }

    public static void shareToSms(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"));
        intent.putExtra("sms_body", SMS_SHARE_CONTENT);
        context.startActivity(intent);
    }

    private static Uri getInsShareUri(Context context) {
        File file = new File(Environment.getExternalStorageDirectory(), SHARE_FILENAME);

        if (!file.exists()) {
            FileUtils.copyAssets(context);
        }

        return Uri.fromFile(file);
    }
}
