package id.co.picklon.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.libpayclient.util.GetMerchantMSG;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.model.data.DataSource;
import id.co.picklon.model.entities.Coupon;
import id.co.picklon.model.entities.Order;
import id.co.picklon.model.rest.utils.ProgressSubscriber;
import id.co.picklon.utils.Const;
import id.co.picklon.utils.L;
import id.co.picklon.utils.Tool;

public class PaymentActivity extends ToolbarActivity {
    private static final String ORDER = "order";
    private Order order;
    private long finalPrice;

    @BindView(R.id.payment_store_name)
    TextView nameView;
    @BindView(R.id.payment_store_id)
    TextView storeIdView;
    @BindView(R.id.payment_orders)
    TextView ordersView;
    @BindView(R.id.payment_rank)
    TextView rankView;
    @BindView(R.id.payment_phone)
    ImageView phoneView;
    @BindView(R.id.payment_price)
    TextView priceView;
    @BindView(R.id.payment_order_id)
    TextView orderIdView;
    @BindView(R.id.payment_total_price)
    TextView totalPriceView;
    @BindView(R.id.payment_deliver_fee)
    TextView feeView;
    @BindView(R.id.payment_tips)
    TextView tipsView;
    @BindView(R.id.payment_discount)
    AppCompatSpinner discountView;
    @BindView(R.id.payment_cash_checkbox)
    AppCompatCheckBox cashCheckbox;
    @BindView(R.id.payment_pay)
    Button payView;

    @Inject
    DataSource dataSource;

    public static void start(Context context, Order order) {
        Intent intent = new Intent(context, PaymentActivity.class);
        intent.putExtra(ORDER, order);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        activityComponent().inject(this);

        initPaymentBroadcast();

        order = getIntent().getParcelableExtra(ORDER);

        if (TextUtils.isEmpty(order.getShopOrdersCount())) {
            refreshOrder();
        } else {
            initUi();
            loadDiscount();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshOrder();
    }

    private void refreshOrder() {
        dataSource.orderDetail(order.getOrderId()).subscribe(new ProgressSubscriber<>(this, order1 -> {
            order = order1;
            initUi();
            loadDiscount();
        }));
    }

    private void loadDiscount() {
        Coupon coupon = new Coupon() {
            @Override
            public String toString() {
                return getString(R.string.no_coupon);
            }
        };
        coupon.setId("0");
        coupon.setCouponId("0");

        if (!TextUtils.isEmpty(order.getCouponRp())) {
            List<Coupon> coupons = new ArrayList<>();
            Coupon c = new Coupon();
            c.setValue(Long.parseLong(order.getCouponRp()));
            coupons.add(c);
            ArrayAdapter<Coupon> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, coupons);
            discountView.setAdapter(dataAdapter);
            discountView.setEnabled(false);
            finalPrice = Long.valueOf(order.getPrice());
            totalPriceView.setText(String.valueOf(Long.parseLong(order.getPrice()) + Long.parseLong(order.getCouponRp())));
            priceView.setText(Tool.getFormatedValue(finalPrice));
            if (order.getPayType().equals("CASH")) {
                cashCheckbox.setChecked(true);
            } else {
                cashCheckbox.setChecked(false);
            }
            cashCheckbox.setEnabled(false);
            discountView.setOnItemSelectedListener(null);
            payView.setOnClickListener(view -> handlePayResult());
            return;
        }

        dataSource.getCouponList().retry(3).subscribe(coupons -> {
            coupons.add(coupon);
            ArrayAdapter<Coupon> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, coupons);
            discountView.setAdapter(dataAdapter);

            discountView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Coupon c = coupons.get(i);
                    finalPrice = Long.valueOf(order.getPrice()) - c.getValue();
                    priceView.setText(Tool.getFormatedValue(finalPrice));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        });
    }

    private void initUi() {
        String payment = getString(R.string.payment);
        setToolbarTitle(payment.toUpperCase());

        nameView.setText(order.getShopName());
        storeIdView.setText(getString(R.string.prefix_store_id, order.getShopId()));
        rankView.setText(String.valueOf(order.getShopStart()));
        phoneView.setOnClickListener(view -> Tool.callPhone(this, order.getShopPhone()));
        totalPriceView.setText(Tool.getFormatedValue(Long.valueOf(order.getPrice())));
        tipsView.setText(Tool.getFormatedValue(order.getTips()));
        priceView.setText(Tool.getFormatedValue(Long.valueOf(order.getPrice())));
        orderIdView.setText(order.getOrderId());
        ordersView.setText(order.getShopOrdersCount().concat(" orders"));
        feeView.setText(Tool.getFormatedValue(order.getDeliverFee()));
        cashCheckbox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                payView.setText(getString(R.string.finish_order));
            } else {
                payView.setText(getString(R.string.confirm_to_pay));
            }
        });
        payView.setOnClickListener(view -> {
            String couponId = "0";
            String cId = "0";
            String payType;
            Object object = discountView.getSelectedItem();
            if (object != null) {
                Coupon coupon = (Coupon) object;
                couponId = coupon.getId();
                cId = coupon.getCouponId();
            }

            if (cashCheckbox.isChecked()) {
                payType = "CASH";
            } else {
                payType = "ONLINE";
            }

            dataSource.setOrderDiscount(order.getOrderId(), couponId, cId, String.valueOf(finalPrice), payType)
                    .subscribe(new ProgressSubscriber<>(this, o -> handlePayResult()));
        });
    }

    private void handlePayResult() {
        if (cashCheckbox.isChecked()) {
            DeliverActivity.start(PaymentActivity.this, order, getString(R.string.order_confirm));
            finish();
        } else {
            GetMerchantMSG.getMerchant(this, 5, finalPrice, "bears", "goodtoy",
                    order.getOrderId());
        }
        // refresh status
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Const.ORDER_COMPLETED));
    }

    private void initPaymentBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.PAYMENT_RESULT);
        registerReceiver(myReceiver, filter);
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Map map = (Map) intent.getSerializableExtra("message");
            int finish = Integer.parseInt(map.get("finish").toString());

            if (finish == 0) {
                Toast.makeText(PaymentActivity.this, "Wait payment...", Toast.LENGTH_LONG).show();
            } else if (finish != 2) {
                DeliverActivity.start(PaymentActivity.this, order, getString(R.string.order_confirm));
                finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }
}
