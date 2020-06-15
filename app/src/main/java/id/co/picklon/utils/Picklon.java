package id.co.picklon.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import id.co.picklon.model.entities.Inbox;
import id.co.picklon.model.entities.UserInbox;
import id.co.picklon.model.entities.WashItem;
import id.co.picklon.model.entities.WashService;

public class Picklon {
    public static String TOKEN;
//    public static final String MEDIAHOST = "http://ateam.ticp.io:8100/media/";
    public static final String MEDIAHOST = "http://182.253.221.43:8000/media/";
    private static final Map<String, Object> globalMap = new HashMap<>();
    public static final long INBOX_MS = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES);
    public static long LAST_READ_MS;
    public static String DEVICE_TOKEN;

    public static List<WashService> serviceList;
    public static List<WashItem> itemList;
    public static List<Inbox> inboxList = new ArrayList<>();
    public static List<UserInbox> userInboxList = Collections.emptyList();

    public static Map<String, Object> commonMap() {
        globalMap.clear();
        return globalMap;
    }

    public static String getVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
