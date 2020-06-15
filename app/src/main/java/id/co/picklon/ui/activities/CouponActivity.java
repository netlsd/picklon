package id.co.picklon.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.model.data.DataSource;
import id.co.picklon.model.entities.Coupon;
import id.co.picklon.model.rest.utils.ProgressSubscriber;
import id.co.picklon.ui.adapter.CouponAdapter;
import id.co.picklon.utils.L;

public class CouponActivity extends ToolbarActivity {
    private CouponAdapter couponAdapter;

    @Inject
    DataSource dataSource;

    @BindView(R.id.redeem_list)
    RecyclerView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        activityComponent().inject(this);

        initUi();
        initializeRecyclerView();
        loadCouponList();
    }

    private void loadCouponList() {
        dataSource.getCouponList().subscribe(new ProgressSubscriber<>(this, couponAdapter));
    }

    private void initUi() {
        String title = getString(R.string.coupons);
        setToolbarTitle(title.toUpperCase());
    }

    private void initializeRecyclerView() {
        couponAdapter = new CouponAdapter();
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(couponAdapter);
    }
}
