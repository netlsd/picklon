package id.co.picklon.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.model.data.DataSource;
import id.co.picklon.model.entities.Order;
import id.co.picklon.ui.view.NotComeDialog;
import id.co.picklon.utils.Const;
import id.co.picklon.utils.Tool;

import static id.co.picklon.ui.view.WashStatusView.STORE_CONFIRM;

public class OrderSuccessActivity extends ToolbarActivity {
    private static final String ORDER = "order";
    private Order order;

    @BindView(R.id.order_success_store_name)
    TextView storeNameView;
    @BindView(R.id.order_success_store_id)
    TextView storeIdView;
    @BindView(R.id.order_success_distance)
    TextView distanceView;
    @BindView(R.id.order_success_phone)
    ImageView phoneView;
    @BindView(R.id.order_success_share)
    FrameLayout shareView;
    @BindView(R.id.order_success_not_come)
    Button notComeView;
    @BindView(R.id.order_success_picked_up)
    Button pickedUpView;
    @BindView(R.id.order_success_store_image)
    ImageView storeImage;

    @Inject
    Picasso picasso;
    @Inject
    DataSource dataSource;

    public static void start(Context context, Order order) {
        Intent intent = new Intent(context, OrderSuccessActivity.class);
        intent.putExtra(ORDER, order);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);
        activityComponent().inject(this);

        order = getIntent().getParcelableExtra(ORDER);
        initUi();

        // 关闭之前的页面
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Const.ORDER_COMPLETED));
    }

    private void loadOrderDetail() {
        dataSource.orderDetail(order.getOrderId())
                .subscribe(order1 -> {
                    order = order1;
                    if (STORE_CONFIRM.equals(order.getWashStatus())) {
                        pickedUpView.setBackground(ContextCompat.getDrawable(this, R.drawable.rect_medium_red));
                        pickedUpView.setEnabled(true);
                    }
                }, Throwable::printStackTrace);
    }

    private void initUi() {
        storeNameView.setText(order.getShopName());
        storeIdView.setText(getString(R.string.prefix_store_id, order.getShopId()));
        distanceView.setText(getString(R.string.order_distance_minutes, getFormatedDistance(order.getDistance()), calcTime()));
        picasso.load(order.getShopImage())
                .placeholder(R.drawable.ic_merchant_default)
                .error(R.drawable.ic_merchant_default)
                .into(storeImage);

        setToolbarIconListener(view -> loadOrderDetail());

        phoneView.setOnClickListener(view -> Tool.callPhone(this, order.getShopPhone()));
        notComeView.setOnClickListener(view -> new NotComeDialog(this, order).show());

        pickedUpView.setOnClickListener(view -> {
            ItemListActivity.start(this, order);
            finish();
        });
        shareView.setOnClickListener(v -> startActivity(new Intent(this, ShareActivity.class)));
    }

    private int calcTime() {
        // 系数1.42, 时速30km, 20分钟为准备时间
        return (int) (order.getDistance() / 1000 * 1.42 / 20 * 60 + 20);
    }

    private String getFormatedDistance(long distance) {
        if (distance < 1000) {
            return distance + "M";
        } else {
            return ((float) distance / 1000) + "KM";
        }
    }
}
