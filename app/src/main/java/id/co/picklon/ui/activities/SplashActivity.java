package id.co.picklon.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.LinearLayout;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.model.data.AccountManager;
import id.co.picklon.model.rest.utils.ErrorResponseException;
import id.co.picklon.utils.ViewUtils;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class SplashActivity extends BaseActivity {
    @BindView(R.id.splash_root)
    LinearLayout rootView;

    @Inject
    AccountManager accountManager;

    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        activityComponent().inject(this);
        mContext = this;

        rootView.setPadding(0, 0, 0, ViewUtils.getStatusBarHeight(this));

        if (accountManager.isRegistered()) {
//             登录需要finish, 需要判断是否打开登录界面, 因此自定义LoginSubscriber
            accountManager.autoLogin().subscribe(new LoginSubscriber<>());
        } else {
            Observable.timer(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::startGuideActivity);
        }
    }

    class LoginSubscriber<T> extends Subscriber<T> {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            if (e instanceof ErrorResponseException) {
                showDialogWithLogin(e.getMessage());
            } else {
                showDialog(getString(R.string.network_error));
                e.printStackTrace();
            }
        }

        @Override
        public void onNext(T t) {
            startHomeActivity();
        }
    }

    private AlertDialog.Builder getDialog(String msg) {
        return new AlertDialog.Builder(mContext)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, this::errorDialogButtonAction);
    }

    private void showDialogWithLogin(String msg) {
        getDialog(msg)
                .setNegativeButton(R.string.re_login, this::startLoginActivity)
                .create()
                .show();
    }

    private void showDialog(String msg) {
        getDialog(msg).create().show();
    }

    private void errorDialogButtonAction(DialogInterface dialog, int i) {
        dialog.dismiss();
        finish();
    }

    private void startLoginActivity(DialogInterface dialog, int i) {
        dialog.dismiss();
        startActivity(new Intent(mContext, LoginActivity.class));
        finish();
    }

    private void startHomeActivity() {
        startActivity(new Intent(mContext, HomeActivity.class));
        finish();
    }

    private void startGuideActivity(long time) {
        startActivity(new Intent(this, GuideActivity.class));
        finish();
    }
}
