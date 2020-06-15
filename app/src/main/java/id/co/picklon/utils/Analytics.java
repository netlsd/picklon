package id.co.picklon.utils;

import android.content.Context;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import id.co.picklon.PicklonApplication;
import id.co.picklon.model.rest.PicklonService;
import id.co.picklon.model.rest.utils.HttpResultFunc;
import id.co.picklon.model.rest.utils.RxUtil;
import rx.Observable;

public class Analytics {
    private static boolean isBackgrounded = false;
    private static long onBackgroundTime = 0;
    private static long globalStartTime = 0;
    private static long backgroundTimeTotal = 0;

    public static void reset() {
        isBackgrounded = false;
        onBackgroundTime = 0;
        globalStartTime = System.currentTimeMillis();
        backgroundTimeTotal = 0;
    }

    public static void uploadUsedTime(Context context) {
        long currentTime = System.currentTimeMillis();

        if (onBackgroundTime == 0) {
            backgroundTimeTotal = 0;
        } else {
            backgroundTimeTotal = System.currentTimeMillis() - onBackgroundTime;
        }

        //进入后台时间超过30秒
        if (isBackgrounded && backgroundTimeTotal > 30 * 1000) {
            if (onBackgroundTime < globalStartTime || globalStartTime == 0) {
                return;
            }

            long usedTime = (onBackgroundTime - globalStartTime) / 1000;
            L.d("used time is " + usedTime);
            upLoad(context, usedTime);
            globalStartTime = currentTime;
            backgroundTimeTotal = 0;
        }
        isBackgrounded = false;
    }

    public static void updateBackgroundTime(Context context) {
        onBackgroundTime = System.currentTimeMillis();
        isBackgrounded = true;
    }

    public static void updateLastUsedTime(Context context) {
        long currentTime = System.currentTimeMillis();
        if (currentTime < globalStartTime || globalStartTime == 0) {
            return;
        }

        long time = (currentTime - globalStartTime - backgroundTimeTotal) / 1000;

        SP.putLong(context, SP.LAST_USAGE_TIME, time);
    }

    public static void uploadLastUsedTime(Context context) {
        long time = SP.getLong(context, SP.LAST_USAGE_TIME);

        upLoad(context, time);

        L.d("time is " + time);

        SP.putLong(context, SP.LAST_USAGE_TIME, 0);
    }

    private static void upLoad(Context context, Long time) {
        Map<String, Object> map = Tool.collectLog(context);
        map.put("seconds", time);

        PicklonApplication application = (PicklonApplication) context.getApplicationContext();
        PicklonService service = application.getAppComponent().picklonService();

        Observable.timer(5, TimeUnit.SECONDS)
                .flatMap(aLong -> service.uploadLog(Picklon.TOKEN, new Gson().toJson(map)))
                .map(new HttpResultFunc<>())
                .compose(RxUtil.applyIOToMainThreadSchedulers())
                .subscribe(List::size, Throwable::printStackTrace);
    }
}
