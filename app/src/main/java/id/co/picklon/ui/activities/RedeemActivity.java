package id.co.picklon.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.picklon.R;

public class RedeemActivity extends ToolbarActivity {

    @BindView(R.id.redeem_regulation)
    TextView regulationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);
        ButterKnife.bind(this);

        initUi();
    }

    private void initUi() {
        String title = getString(R.string.redeem_coupon);
        setToolbarTitle(title.toUpperCase());

        regulationView.setOnClickListener(view -> startActivity(new Intent(this, RegulationActivity.class)));
    }
}
