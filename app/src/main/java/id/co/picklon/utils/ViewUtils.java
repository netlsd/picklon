package id.co.picklon.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewConfiguration;
import android.view.Window;

import id.co.picklon.R;
import id.co.picklon.ui.activities.OrderRequestActivity;

public class ViewUtils {

    public static int getStatusBarHeight(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return 0;
        }

        int result = 0;
        int resourceId = context.getApplicationContext().getResources().
                getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getApplicationContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getNavigationBarHeight(Context context) {
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && !hasMenuKey) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static int dpToPix(Context context, int dp) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public static void showDialog(Context context, int msgId, DialogInterface.OnClickListener listener) {
        AlertDialog alertDialog = getDialogBuilder(context, listener).setMessage(msgId).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        showDialogWithoutTitle(alertDialog);
    }

    public static void showDialog(Context context, String msg) {
        AlertDialog alertDialog = getDialogBuilder(context).setMessage(msg).create();
        showDialogWithoutTitle(alertDialog);
    }

    public static void showDialog(Context context, int msgId) {
        AlertDialog alertDialog = getDialogBuilder(context).setMessage(msgId).create();
        showDialogWithoutTitle(alertDialog);
    }

    private static void showDialogWithoutTitle(AlertDialog alertDialog) {
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.show();
    }

    private static AlertDialog.Builder getDialogBuilder(Context context) {
        return new AlertDialog.Builder(context)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss());
    }

    private static AlertDialog.Builder getDialogBuilder(Context context, DialogInterface.OnClickListener listener) {
        return new AlertDialog.Builder(context)
                .setPositiveButton(R.string.ok, listener);
    }

    public static int getScreenWidth(Context context) {
        Activity activity = (Activity) context;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }

    public static void cancelOrderDialog(Activity activity) {
        new AlertDialog.Builder(activity)
                .setMessage(R.string.cancel_order_warning)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    activity.finish();
                })
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }
}
