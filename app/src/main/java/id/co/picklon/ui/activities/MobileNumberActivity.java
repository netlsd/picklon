package id.co.picklon.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.model.data.AccountManager;

public class MobileNumberActivity extends ToolbarActivity {
    @Inject
    AccountManager accountManager;

    @BindView(R.id.mobile_number_phone)
    TextView phoneNumberView;
    @BindView(R.id.mobile_number_change)
    Button changeBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_number);
        activityComponent().inject(this);

        initUi();
    }

    private void initUi() {
        String mobileNumberText = getString(R.string.mobile_number);
        setToolbarTitle(mobileNumberText.toUpperCase());

        phoneNumberView.setText(getString(R.string.phone_with_code, accountManager.getMobile()));
        changeBtn.setOnClickListener(view -> {
            startActivity(new Intent(this, ChangeMobileActivity.class));
            finish();
        });
    }

}
