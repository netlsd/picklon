package id.co.picklon.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

public class DeviceInfo {
    /**
     * 系统内部 Hide的三个网络类型
     */
    private final static int NETWORK_TYPE_LTE = 13;

    private final static int NETWORK_TYPE_EHRPD = 14;

    private final static int NETWORK_TYPE_HSPAP = 15;

    /**
     * 没有网络连接
     */
    private final static String NET_TYPE_NO_CONNECTION = "-1";

    /**
     * 2G网络Wap类型
     */
    private final static String NET_TYPE_MOBILE_2G_WAP = "100";

    /**
     * 2G网络Net类型
     */
    private final static String NET_TYPE_MOBILE_2G_NET = "101";

    /**
     * 3G网络Wap类型
     */
    private final static String NET_TYPE_MOBILE_3G_WAP = "102";

    /**
     * 3G网络Net类型
     */
    private final static String NET_TYPE_MOBILE_3G_NET = "103";

    /**
     * WIFI类型
     */
    private final static String NET_TYPE_WIFI = "104";

    /**
     * 不知名类型
     */
    private final static String NET_TYPE_DEFAULT = "105";

    public static String getDevice() {
        return Build.MODEL + " " + Build.VERSION.RELEASE;
    }

    public static String getImei(Context context) {
        String im = null;
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        try {
            im = tm.getDeviceId();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return im;
    }

    public static String getImsi(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        return tm.getSubscriberId();
    }

    public static String getNum(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        return tm.getLine1Number();
    }

    /**
     * 获取当前网络类型
     *
     * @param context
     * @return 返回与接口指定类型
     */
    public static String getNetType(Context context) {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
            if (info == null || !info.isConnected()) {
                return NET_TYPE_NO_CONNECTION;
            }

            int type = info.getType();

            if (type == ConnectivityManager.TYPE_WIFI) {
                return NET_TYPE_WIFI;
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                int subType = info.getSubtype();
                String proxyHost = android.net.Proxy.getDefaultHost();
                int proxyPort = android.net.Proxy.getDefaultPort();
                switch (subType) {
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                        if (proxyHost != null && proxyPort != -1 && isChinaMobile(context)) {
                            return NET_TYPE_MOBILE_2G_WAP;
                        } else {
                            return NET_TYPE_MOBILE_2G_NET;
                        }
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case NETWORK_TYPE_EHRPD:
                    case NETWORK_TYPE_HSPAP:
                    case NETWORK_TYPE_LTE:
                        if (proxyHost != null && proxyPort != -1 && isChinaMobile(context)) {
                            return NET_TYPE_MOBILE_3G_WAP;
                        } else {
                            return NET_TYPE_MOBILE_3G_NET;
                        }
                    case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    default:
                        return NET_TYPE_DEFAULT;
                }
            }
            return NET_TYPE_DEFAULT;
        } catch (Exception e) {
            return NET_TYPE_NO_CONNECTION;
        }
    }

    /**
     * 判断是否 是中国移动运营商卡 目前只有中国移支区分wap与net网络
     *
     * @param context
     * @return
     */
    private static boolean isChinaMobile(Context context) {

        TelephonyManager mTelephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String operator = mTelephonyManager.getSimOperator();
        if (operator == null || operator.equals("")) {
            return false;
        }

        // 中国移动 46000 46002 46007
        // 中国联通"46001"
        // 中国电信"46003"
        return (operator.equals("46000") || operator.equals("46002") || operator.equals("46007"));
    }

    /**
     * 获取运营商
     *
     * @param context
     * @return
     */
    public static String getOperator(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String operator = mTelephonyManager.getSimOperator();
        if (operator == null || operator.equals("")) {
            return "0";
        }

        return operator;
    }
}
