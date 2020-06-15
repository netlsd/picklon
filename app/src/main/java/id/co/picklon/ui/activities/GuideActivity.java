package id.co.picklon.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.widget.Button;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.ui.adapter.GuidePagerAdapter;
import me.relex.circleindicator.CircleIndicator;

import static id.co.picklon.utils.Const.SIGNIN_CODE;

public class GuideActivity extends BaseActivity {
    @BindView(R.id.guide_pager)
    ViewPager pagerView;
    @BindView(R.id.guide_indicator)
    CircleIndicator indicatorView;
    @BindView(R.id.guide_login)
    Button loginView;
    @BindView(R.id.guide_start)
    Button startView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        pagerView.setAdapter(new GuidePagerAdapter(this));
        indicatorView.setViewPager(pagerView);

        loginView.setOnClickListener(view -> startActivityForResult(new Intent(this, LoginActivity.class), SIGNIN_CODE));
        startView.setOnClickListener(view -> startActivityForResult(new Intent(this, RegisterActivity.class), SIGNIN_CODE));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SIGNIN_CODE) {
            finish();
        }
    }
}
