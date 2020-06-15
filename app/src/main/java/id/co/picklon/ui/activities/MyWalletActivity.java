package id.co.picklon.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.picklon.R;
import id.co.picklon.ui.view.MenuView;

public class MyWalletActivity extends ToolbarActivity {

    @BindView(R.id.my_wallet_redeem)
    MenuView redeemView;
    @BindView(R.id.my_wallet_coupons)
    MenuView couponsView;
    @BindView(R.id.my_wallet_record)
    MenuView recordView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);
        ButterKnife.bind(this);

        initUi();
    }

    private void initUi() {
        String myWallet = getString(R.string.my_wallet);
        setToolbarTitle(myWallet.toUpperCase());
        redeemView.setOnClickListener(view -> startActivity(new Intent(this, RedeemActivity.class)));
        couponsView.setOnClickListener(view -> startActivity(new Intent(this, CouponActivity.class)));
        recordView.setOnClickListener(view -> startActivity(new Intent(this, AccountRecordActivity.class)));
    }
}
