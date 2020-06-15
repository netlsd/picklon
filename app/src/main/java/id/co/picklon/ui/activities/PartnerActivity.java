package id.co.picklon.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.model.data.DataSource;
import id.co.picklon.model.rest.utils.ProgressSubscriber;
import id.co.picklon.utils.ViewUtils;

public class PartnerActivity extends ToolbarActivity {

    @Inject
    DataSource dataSource;

    @BindView(R.id.partner_mobile)
    AppCompatEditText mobileView;
    @BindView(R.id.partner_email)
    AppCompatEditText emailView;
    @BindView(R.id.partner_thing)
    AppCompatEditText thingView;
    @BindView(R.id.partner_submit)
    Button submitBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner);
        activityComponent().inject(this);

        initUi();
    }

    private void initUi() {
        setToolbarTitle(R.string.become_partner);

        submitBtn.setOnClickListener(this::submit);
    }

    private void submit(View view) {
        String mobile = mobileView.getText().toString().trim();
        String email = emailView.getText().toString().trim();
        String thing = thingView.getText().toString().trim();

        if (TextUtils.isEmpty(mobile) && TextUtils.isEmpty(email)) {
            ViewUtils.showDialog(this, R.string.field_tips);
        } else {
            dataSource.becomePartner(mobile, email, thing)
                    .subscribe(new ProgressSubscriber<>(this, articles -> {
                        Toast.makeText(this, R.string.submit_success, Toast.LENGTH_LONG).show();
                        finish();
                    }));
        }
    }
}
