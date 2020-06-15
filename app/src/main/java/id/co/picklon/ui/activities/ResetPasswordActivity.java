package id.co.picklon.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.model.data.AccountManager;
import id.co.picklon.ui.view.LoadingDialog;
import id.co.picklon.utils.ViewUtils;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static id.co.picklon.utils.Tool.autoCountryCode;

public class ResetPasswordActivity extends ToolbarActivity {

    @BindView(R.id.register_phone_number)
    EditText numberView;
    @BindView(R.id.register_verify)
    Button verifyBtn;
    @BindView(R.id.register_verify_code)
    EditText verifyCodeView;
    @BindView(R.id.register_password)
    EditText passwordView;
    @BindView(R.id.register_terms_layout)
    LinearLayout termsLayout;
    @BindView(R.id.register_sign_up)
    Button resetBtn;

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
        setToolbarTitle(R.string.reset_password);
        termsLayout.setVisibility(View.GONE);
        resetBtn.setText(R.string.reset_password);
        passwordView.setHint(R.string.new_password);

        resetBtn.setOnClickListener(this::reset);
        verifyBtn.setOnClickListener(view -> {
            String phoneNumber = numberView.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNumber)) {
                ViewUtils.showDialog(this, R.string.enter_phone_number);
                return;
            }

            phoneNumber = autoCountryCode(phoneNumber);
            doGetCode(phoneNumber);
        });
    }

    /**
     * 获取验证码，类型：重置密码 ver_reset
     * @param phoneNumber
     */
    private void doGetCode(String phoneNumber) {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        compositeSubscription.add(accountManager.getVerifyCode(phoneNumber,getString(R.string.code_type_reset), empty -> {
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

    private void reset(View view) {
        String phoneNumber = numberView.getText().toString().trim();
        String verifyCode = verifyCodeView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();

        if (TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(password) || TextUtils.isEmpty(verifyCode)) {
            ViewUtils.showDialog(this, R.string.field_tips);
        } else {
            accountManager.resetPassword(phoneNumber, password, verifyCode, this::doSuccess);
        }
    }

    private void doSuccess(Object object) {
        Toast.makeText(this, R.string.reset_password_success, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
