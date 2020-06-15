package id.co.picklon.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.model.data.AccountManager;
import id.co.picklon.model.entities.Token;
import id.co.picklon.model.rest.utils.ProgressSubscriber;
import id.co.picklon.utils.Const;
import id.co.picklon.utils.ViewUtils;

import static id.co.picklon.utils.Const.SIGNIN_CODE;
import static id.co.picklon.utils.Const.SIGNUP_CODE;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.login_in)
    Button loginInView;
    @BindView(R.id.login_forgot_password)
    TextView forgotPassView;
    @BindView(R.id.login_phone_number)
    EditText phoneNumberView;
    @BindView(R.id.login_password)
    EditText passwordView;
    @BindView(R.id.login_register)
    TextView registerView;

    @Inject
    AccountManager accountManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activityComponent().inject(this);

        forgotPassView.setOnClickListener(view -> startActivity(new Intent(this, ResetPasswordActivity.class)));
        registerView.setOnClickListener(view -> startActivityForResult(new Intent(this, RegisterActivity.class), SIGNUP_CODE));

        loginInView.setOnClickListener(view -> {
            String phoneNumber = phoneNumberView.getText().toString().trim();
            String password = passwordView.getText().toString().trim();

            if (TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(password)) {
                ViewUtils.showDialog(this, R.string.field_tips);
                return;
            }

            accountManager.newLogin(phoneNumber, password)
                    .subscribe(new ProgressSubscriber<>(LoginActivity.this, this::doSuccess));

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SIGNUP_CODE) {
            setResult(RESULT_OK, null);
            finish();
        }
    }

    private void doSuccess(Token token) {
        setResult(RESULT_OK, null);
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
