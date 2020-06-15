package id.co.picklon.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.model.data.AccountManager;
import id.co.picklon.model.entities.Token;
import id.co.picklon.model.rest.utils.ProgressSubscriber;
import id.co.picklon.ui.view.LoadingDialog;
import id.co.picklon.utils.ViewUtils;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static id.co.picklon.utils.Const.TERMS_CODE;
import static id.co.picklon.utils.Tool.autoCountryCode;

public class RegisterActivity extends ToolbarActivity {
    @Inject
    AccountManager accountManager;

    @BindView(R.id.register_verify)
    Button verifyView;
    @BindView(R.id.register_phone_number)
    EditText phoneNumberView;
    @BindView(R.id.register_password)
    EditText passwordView;
    @BindView(R.id.register_verify_code)
    EditText verifyCodeView;
    @BindView(R.id.register_checkbox)
    AppCompatCheckBox checkboxView;
    @BindView(R.id.register_sign_up)
    Button signUpView;
    @BindView(R.id.register_terms)
    TextView termsView;

    private Subscription timerSubscription;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        activityComponent().inject(this);

        setToolbarTitle(R.string.create_account);

        termsView.setOnClickListener(view -> startActivityForResult(new Intent(this, TermsAcitivity.class), TERMS_CODE));

        verifyView.setOnClickListener(view -> {
            String phoneNumber = phoneNumberView.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNumber)) {
                ViewUtils.showDialog(this, R.string.enter_phone_number);
                return;
            }

            phoneNumber = autoCountryCode(phoneNumber);
            doGetCode(phoneNumber);
        });

        signUpView.setOnClickListener(view -> {
            String phoneNumber = phoneNumberView.getText().toString().trim();
            String password = passwordView.getText().toString().trim();
            String verifyCode = verifyCodeView.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(password) || TextUtils.isEmpty(verifyCode)) {
                ViewUtils.showDialog(this, R.string.field_tips);
                return;
            }

            if (!checkboxView.isChecked()) {
                ViewUtils.showDialog(this, R.string.terms_tips);
                return;
            }

            accountManager.register(phoneNumber, password, verifyCode)
                    .subscribe(new ProgressSubscriber<>(RegisterActivity.this, this::onSuccess));

        });
    }

    private void onSuccess(Token token) {
        setResult(RESULT_OK, null);
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    /**
     * 获取验证码 类型： 注册新密码 ver_new
     * @param phoneNumber
     */
    private void doGetCode(String phoneNumber) {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        compositeSubscription.add(accountManager.getVerifyCode(phoneNumber,getString(R.string.code_type_new), empty -> {
            loadingDialog.dismiss();
            countDown();
        }, throwable -> {
            loadingDialog.dismiss();
            ViewUtils.showDialog(this, R.string.verify_code_error);
        }));
    }

    private void countDown() {
        int count = 60;
        verifyView.setEnabled(false);
        verifyView.setBackground(ContextCompat.getDrawable(this, R.drawable.round_black));

        timerSubscription = Observable.interval(1, TimeUnit.SECONDS)
                .map(i -> count - i)
                .take(count + 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(curSecond -> {
                    if (curSecond == 0) {
                        verifyView.setEnabled(true);
                        verifyView.setBackground(ContextCompat.getDrawable(this, R.drawable.round_red));
                        verifyView.setText(R.string.register_verify);
                        timerSubscription.unsubscribe();
                    } else {
                        verifyView.setText(getString(R.string.resend_code_tips, curSecond));
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TERMS_CODE && resultCode == RESULT_OK) {
            checkboxView.setChecked(true);
        }
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
