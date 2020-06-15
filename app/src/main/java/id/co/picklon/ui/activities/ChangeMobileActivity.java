package id.co.picklon.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.model.data.AccountManager;
import id.co.picklon.ui.view.LoadingDialog;
import id.co.picklon.utils.Const;
import id.co.picklon.utils.ViewUtils;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static id.co.picklon.utils.Tool.autoCountryCode;

public class ChangeMobileActivity extends ToolbarActivity {

    @BindView(R.id.register_text)
    TextView textView;
    @BindView(R.id.register_phone_number)
    EditText phoneNumberView;
    @BindView(R.id.register_verify)
    Button verifyBtn;
    @BindView(R.id.register_verify_code)
    EditText verifyCodeView;
    @BindView(R.id.register_password_layout)
    LinearLayout passwordLayout;
    @BindView(R.id.register_terms_layout)
    LinearLayout termsLayout;
    @BindView(R.id.register_sign_up)
    Button changeBtn;

    @Inject
    AccountManager accountManager;

    private Subscription timerSubscription;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        activityComponent().inject(this);

        initUi();
    }

    private void initUi() {
        String changeMobileText = getString(R.string.change_mobile);
        setToolbarTitle(changeMobileText.toUpperCase());
        passwordLayout.setVisibility(View.GONE);
        termsLayout.setVisibility(View.GONE);
        changeBtn.setText(R.string.confirm_to_change);
        textView.setText(R.string.change_mobile_text);

        // todo 抽取验证码部分
        verifyBtn.setOnClickListener(v -> {
            String phoneNumber = phoneNumberView.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNumber)) {
                ViewUtils.showDialog(this, R.string.enter_phone_number);
                return;
            }

            phoneNumber = autoCountryCode(phoneNumber);
            doGetCode(phoneNumber);
        });
        changeBtn.setOnClickListener(this::change);
    }

    private void doGetCode(String phoneNumber) {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        compositeSubscription.add(accountManager.getVerifyCode(phoneNumber,getString(R.string.code_type_modify), empty -> {
            loadingDialog.dismiss();
            countDown();
        }, throwable -> {
            loadingDialog.dismiss();
            ViewUtils.showDialog(this, R.string.verify_code_error);
        }));
    }

    private void countDown() {
        int count = 60;
        verifyBtn.setEnabled(false);
        verifyBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.round_black));

        timerSubscription = Observable.interval(1, TimeUnit.SECONDS)
                .map(i -> count - i)
                .take(count + 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(curSecond -> {
                    if (curSecond == 0) {
                        verifyBtn.setEnabled(true);
                        verifyBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.round_red));
                        verifyBtn.setText(R.string.register_verify);
                        timerSubscription.unsubscribe();
                    } else {
                        verifyBtn.setText(getString(R.string.resend_code_tips, curSecond));
                    }
                });
    }

    private void change(View view) {
        String phoneNumber = phoneNumberView.getText().toString().trim();
        String verifyCode = verifyCodeView.getText().toString().trim();

        if (TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(verifyCode)) {
            ViewUtils.showDialog(this, R.string.field_tips);
        } else {
            accountManager.changeMobile(phoneNumber, verifyCode, this::doSuccess);
        }
    }

    private void doSuccess(Object object) {
        Toast.makeText(this, R.string.change_mobile_success, Toast.LENGTH_LONG).show();
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Const.UPDATE_USERNAME));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerSubscription != null && !timerSubscription.isUnsubscribed()) {
            timerSubscription.unsubscribe();
        }

        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}
