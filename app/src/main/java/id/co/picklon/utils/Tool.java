package id.co.picklon.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import id.co.picklon.R;
import id.co.picklon.model.entities.Order;

public class Tool {

    public static String autoCountryCode(String phoneNumber) {
        if (Const.COUNTRY_CODE.equals(phoneNumber.substring(0, 2))) {
            return phoneNumber;
        } else {
            return Const.COUNTRY_CODE + phoneNumber;
        }
    }

    public static void callPhone(Context context, String number) {
        if (number == null || !isInteger(number)) {
            Toast.makeText(context, R.string.dial_warning, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
        context.startActivity(intent);
    }

    private static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static String getFormatedValue(long value) {
        return "RP ".concat(String.format(Locale.ENGLISH, "%,d", value));
    }

    public static void openBrowser(Context context, String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }

    public static Map<String, Object> collectLog(Context context) {
        Map<String, Object> logMap = Picklon.commonMap();

        logMap.put("uid", getUid(context));
        logMap.put("net", DeviceInfo.getNetType(context));
        logMap.put("uuid", Identify.getIdentity());
        logMap.put("imei", DeviceInfo.getImei(context));
        logMap.put("imsi", DeviceInfo.getImsi(context));
        logMap.put("mobile", DeviceInfo.getNum(context));
        logMap.put("device", DeviceInfo.getDevice());
        logMap.put("oper", DeviceInfo.getOperator(context));
        logMap.put("version", Tool.getCurrentVersion(context));
        logMap.put("date", Long.toString(getUnixTime()));
        logMap.put("action", "start");

        return logMap;
    }

    private static String getUid(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(SP.MOBILE, "");
    }

    private static String getCurrentVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_RECEIVERS).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private static long getUnixTime() {
        long unixTime = 0;
        try {
            unixTime = new Date().getTime();
            long offset = TimeZone.getDefault().getRawOffset();
            unixTime = unixTime + offset;

            unixTime = unixTime / 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return unixTime;
    }

    public static int getItemCount(Order order) {
        String[] services = order.getServices().split(";");
        String carpet = getCarpet(services);
        int count = 0;

        String washItems = order.getWashItems();

        if (!TextUtils.isEmpty(washItems)) {
            String[] items = washItems.split(";");
            for (String item : items) {
                String[] element = item.split(",");
                int value = Integer.valueOf(element[element.length - 1]);
                count += value;
            }
        }

        if (!TextUtils.isEmpty(carpet)) {
            String[] carpetItem = carpet.split(",");
            count += Integer.valueOf(carpetItem[carpetItem.length - 1]);
        }

        return count;
    }

    public static String getCarpet(String[] services) {
        for (String s : services) {
            for (String service : s.split(",")) {
                if ("carpet".equalsIgnoreCase(service)) {
                    return s;
                }
            }
        }
        return null;
    }

    public static void playSound(Context context) {
        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            AssetFileDescriptor descriptor = context.getAssets().openFd("safari.mp3");
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(mediaPlayer1 -> {
            mediaPlayer1.stop();
            mediaPlayer1.release();
        });
    }

    public static boolean isGPSDisabled(Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return !manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
