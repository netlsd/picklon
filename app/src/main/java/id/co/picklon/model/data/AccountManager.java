package id.co.picklon.model.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import javax.inject.Inject;

import id.co.picklon.model.entities.Token;
import id.co.picklon.model.rest.utils.ProgressSubscriber;
import id.co.picklon.model.rest.utils.SubscriberOnNextListener;
import id.co.picklon.ui.activities.LoginActivity;
import id.co.picklon.utils.Picklon;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

import static id.co.picklon.utils.SP.MOBILE;
import static id.co.picklon.utils.SP.PASSWORD;
import static id.co.picklon.utils.SP.TK;

public class AccountManager {
    private Context mContext;
    private DataSource dataSource;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Inject
    AccountManager(Context context, DataSource dataSource,
                   SharedPreferences sharedPreferences, SharedPreferences.Editor editor) {
        this.mContext = context;
        this.dataSource = dataSource;
        this.sharedPreferences = sharedPreferences;
        this.editor = editor;
    }

    public Observable<Token> register(String phoneNumber, String password, String verifyCode) {
        return dataSource.register(phoneNumber, password, verifyCode)
                .doOnNext(this::saveToken)
                .doOnNext(token -> saveLogin(phoneNumber, password));
    }

    public Observable<Token> autoLogin() {
        String phoneNumber = sharedPreferences.getString(MOBILE, null);
        String password = sharedPreferences.getString(PASSWORD, null);
        return login(phoneNumber, password);
    }

    public Observable<Token> newLogin(String phoneNumber, String password) {
        return login(phoneNumber, password)
                .doOnNext(token -> saveLogin(phoneNumber, password));
    }

    private Observable<Token> login(String phoneNumber, String password) {
        return dataSource.login(phoneNumber, password)
                .doOnNext(this::saveToken);
    }

    // 2017/5/5 获取验证码 添加类型 type
    public Subscription getVerifyCode(String phoneNumber,String type, Action1 successListener, Action1<Throwable> errorListener) {
        return dataSource.getVerifyCode(phoneNumber,type,successListener, errorListener);
    }

    public Subscription resetPassword(String phoneNumber, String password, String verifyCode, SubscriberOnNextListener<Object> listener) {
        return dataSource.resetPassword(phoneNumber, password, verifyCode)
                .subscribe(new ProgressSubscriber<>(mContext, listener));
    }

    public Subscription changeMobile(String phoneNumber, String verifyCode, SubscriberOnNextListener<Object> listener) {
        return dataSource.changeMobile(phoneNumber, verifyCode)
                .doOnNext(aVoid -> saveMobile(phoneNumber))
                .subscribe(new ProgressSubscriber<>(mContext, listener));
    }

    public void logout() {
        ((Activity) mContext).finish();
        editor.putString(TK, null).apply();
        editor.putString(PASSWORD, null).apply();
        editor.putString(MOBILE, null).apply();
        mContext.startActivity(new Intent(mContext, LoginActivity.class));
    }

    public String getMobile() {
        return sharedPreferences.getString(MOBILE, null);
    }

    public boolean isRegistered() {
        return (Picklon.TOKEN = getToken()) != null;
    }

    private String getToken() {
        return sharedPreferences.getString(TK, null);
    }

    private void saveToken(Token token) {
        Picklon.TOKEN = token.getToken();
        editor.putString(TK, Picklon.TOKEN).apply();
    }

    private void saveMobile(String phoneNumber) {
        editor.putString(MOBILE, phoneNumber).apply();
    }

    private void saveLogin(String phoneNumber, String password) {
        saveMobile(phoneNumber);
        editor.putString(PASSWORD, password).apply();
    }
}
