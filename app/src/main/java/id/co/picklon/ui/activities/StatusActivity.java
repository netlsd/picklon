package id.co.picklon.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.ui.adapter.OrderAdapter;

public class StatusActivity extends ToolbarActivity {

    @BindView(R.id.status_tab)
    TabLayout tabLayout;
    @BindView(R.id.status_pager)
    ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        initUi();
    }

    private void initUi() {
        setToolbarTitle(R.string.my_order);

        viewPager.setAdapter(new OrderAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
    }
}
