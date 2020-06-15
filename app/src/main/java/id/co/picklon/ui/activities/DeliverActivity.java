package id.co.picklon.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.model.data.DataSource;
import id.co.picklon.model.entities.Order;
import id.co.picklon.model.rest.utils.RxUtil;
import id.co.picklon.ui.view.WashStatusView;
import id.co.picklon.utils.Const;
import id.co.picklon.utils.QRCode;
import id.co.picklon.utils.Tool;
import rx.Observable;

public class DeliverActivity extends ToolbarActivity {
    private static final String ORDER = "order";
    private static final String TITLE = "title";
    private Order order;
    private String title;
    private boolean isConfirmOrder;

    @BindView(R.id.deliver_qr_code)
    ImageView qrCodeView;
    @BindView(R.id.deliver_order_id)
    TextView orderIdView;
    @BindView(R.id.deliver_store_id)
    TextView storeIdView;
    @BindView(R.id.deliver_total)
    TextView totalView;

    @Inject
    DataSource dataSource;

    public static void start(Context context, Order order) {
        start(context, order, null);
    }

    public static void start(Context context, Order order, String title) {
        Intent intent = new Intent(context, DeliverActivity.class);
        intent.putExtra(ORDER, order);
        intent.putExtra(TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver);
        activityComponent().inject(this);

        order = getIntent().getParcelableExtra(ORDER);
        title = getIntent().getStringExtra(TITLE);
        if (title != null) {
            isConfirmOrder = true;
        }

        initUi();

        Observable<Order> observable = Observable.interval(3, TimeUnit.SECONDS)
                .flatMap(aLong -> dataSource.orderDetail(order.getOrderId()));

        if (isConfirmOrder) {
            compositeSubscription.add(observable
                    .filter(order1 -> order1.getWashStatus().equals(WashStatusView.PAY_FINISH))
                    .compose(RxUtil.applyIOToMainThreadSchedulers())
                    .subscribe(order1 -> handlePayResult(R.string.pay_success), Throwable::printStackTrace));
        } else {
            compositeSubscription.add(observable
                    .filter(order1 -> order1.getWashStatus().equals(WashStatusView.FINISHED))
                    .compose(RxUtil.applyIOToMainThreadSchedulers())
                    .subscribe(order1 -> handlePayResult(R.string.order_finished), Throwable::printStackTrace));
        }
    }

    private void handlePayResult(int textId) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Const.ORDER_COMPLETED));
        Toast.makeText(this, textId, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void initUi() {
        if (isConfirmOrder) {
            setToolbarTitle(title);
        } else {
            setToolbarTitle(R.string.deliver_confirm);
        }

        totalView.setText(String.valueOf(Tool.getItemCount(order)));
        orderIdView.setText(order.getOrderId());
        storeIdView.setText(String.valueOf(order.getShopId()));
        qrCodeView.setImageBitmap(QRCode.createQRCode(this, "mobile:" + order.getOrderId()));
    }
}
