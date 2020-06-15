package id.co.picklon.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.picklon.R;
import id.co.picklon.model.entities.Order;
import id.co.picklon.ui.view.StatusBubbleLayout;
import id.co.picklon.utils.Tool;

import static id.co.picklon.ui.view.WashStatusView.DELIVER;
import static id.co.picklon.ui.view.WashStatusView.HAVEORDER;
import static id.co.picklon.ui.view.WashStatusView.PAY_FINISH;
import static id.co.picklon.ui.view.WashStatusView.PICKUP;
import static id.co.picklon.ui.view.WashStatusView.STORE_CONFIRM;
import static id.co.picklon.ui.view.WashStatusView.USER_CONFIRM;
import static id.co.picklon.ui.view.WashStatusView.WASHING;

public class OrderStatusActivity extends ToolbarActivity {
    private static final String ORDER = "order";

    @BindView(R.id.order_status_bubble_layout)
    StatusBubbleLayout bubbleLayout;
    @BindView(R.id.order_status_pickup_icon)
    ImageView pickupIcon;
    @BindView(R.id.order_status_confirm_icon)
    ImageView confirmIcon;
    @BindView(R.id.order_status_progress_icon)
    ImageView progressIcon;
    @BindView(R.id.order_status_delivered_icon)
    ImageView deliveredIcon;
    @BindView(R.id.pickup_order_id)
    TextView idView;
    @BindView(R.id.pickup_order_status)
    TextView statusView;
    @BindView(R.id.pickup_date)
    TextView dateView;
    @BindView(R.id.pickup_time)
    TextView timeView;
    @BindView(R.id.pickup_address)
    TextView addressView;
    @BindView(R.id.pickup_store_name)
    TextView storeNameView;
    @BindView(R.id.pickup_store_id)
    TextView storeIdView;
    @BindView(R.id.pickup_pilot)
    TextView pilotView;
    @BindView(R.id.pickup_phone)
    ImageView phoneView;
    @BindView(R.id.pickup_line1)
    View line1View;
    @BindView(R.id.pickup_line2)
    View line2View;
    @BindView(R.id.pickup_store_phone)
    ImageView storePhoneView;
    @BindView(R.id.pickup_line3)
    View line3View;
    @BindView(R.id.pickup_time_layout)
    LinearLayout timeLayout;
    @BindView(R.id.pickup_pilot_layout)
    RelativeLayout pilotLayout;
    @BindView(R.id.pickup_prefix_time)
    TextView prefixTimeView;
    @BindView(R.id.order_status_show_code)
    Button showCodeView;
    @BindView(R.id.order_status_go_order_success)
    Button goOrderSuccessView;

    private Order order;

    public static void start(Context context, Order order) {
        Intent intent = new Intent(context, OrderStatusActivity.class);
        intent.putExtra(ORDER, order);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        ButterKnife.bind(this);

        order = getIntent().getParcelableExtra(ORDER);
        initUi();
    }

    private void initUi() {
        setToolbarTitle(R.string.order_status);

        idView.setText(getString(R.string.order_id_place_holder, order.getOrderId()));
        statusView.setText(order.getWashStatus());
        dateView.setText(order.getPickupTime().split(" ")[0]);
        timeView.setText(order.getPickupTime().split(" ")[1]);
        addressView.setText(getString(R.string.prefix_address, order.getAddressDetail()));
        storeNameView.setText(getString(R.string.prefix_store_name, order.getShopName()));
        storeIdView.setText(getString(R.string.prefix_store_id_string, String.valueOf(order.getShopId())));
        pilotView.setText(getString(R.string.prefix_pickup_pilot, order.getPickupGuyName()));
        phoneView.setOnClickListener(view -> Tool.callPhone(this, order.getPickupGuyMobile()));
        storePhoneView.setOnClickListener(view -> Tool.callPhone(this, order.getShopPhone()));
        showCodeView.setOnClickListener(view -> DeliverActivity.start(this, order));
        setBubblePosition(order.getWashStatus());
    }

    private void setBubblePosition(String status) {
        showCodeView.setVisibility(View.GONE);
        goOrderSuccessView.setVisibility(View.GONE);
        switch (status) {
            case PICKUP:
            case HAVEORDER:
                goOrderSuccessView.setOnClickListener(view -> {
                    OrderSuccessActivity.start(this, order);
                    finish();
                });
                goOrderSuccessView.setVisibility(View.VISIBLE);
                setPickupStatus();
                break;
            case USER_CONFIRM:
                goOrderSuccessView.setOnClickListener(view -> {
                    PaymentActivity.start(this, order);
                    finish();
                });
                goOrderSuccessView.setVisibility(View.VISIBLE);
                goOrderSuccessView.setText(R.string.go_pay_page);
                setConfirmStatus();
                statusView.setText(USER_CONFIRM);
                break;
            case STORE_CONFIRM:
                goOrderSuccessView.setOnClickListener(view -> {
                    ItemListActivity.start(this, order);
                    finish();
                });
                goOrderSuccessView.setVisibility(View.VISIBLE);
                goOrderSuccessView.setText(R.string.go_confirm_page);
                setConfirmStatus();
                statusView.setText(STORE_CONFIRM);
                break;
            case PAY_FINISH:
                goOrderSuccessView.setVisibility(View.GONE);
                setConfirmStatus();
                statusView.setText(PAY_FINISH);
                break;
            case WASHING:
                setWashingStatus();
                break;
            case DELIVER:
                showCodeView.setVisibility(View.VISIBLE);
                setDeliveredStatus();
                break;
        }
    }

    private void setPickupStatus() {
        statusView.setText(PICKUP);
        pickupIcon.setImageResource(R.drawable.ic_pickup);
        bubbleLayout.post(() -> bubbleLayout.setArrowPosition(StatusBubbleLayout.PICKUP));
    }

    private void setConfirmStatus() {
        confirmIcon.setImageResource(R.drawable.ic_store_confirm);
        bubbleLayout.post(() -> bubbleLayout.setArrowPosition(StatusBubbleLayout.CONFIRM));
    }

    private void setWashingStatus() {
        statusView.setText(WASHING);

        line1View.setVisibility(View.GONE);
        line2View.setVisibility(View.GONE);
        line3View.setVisibility(View.GONE);
        addressView.setVisibility(View.GONE);
        timeLayout.setVisibility(View.GONE);
        pilotLayout.setVisibility(View.GONE);
        storePhoneView.setVisibility(View.VISIBLE);

        progressIcon.setImageResource(R.drawable.ic_progress);
        bubbleLayout.post(() -> bubbleLayout.setArrowPosition(StatusBubbleLayout.PROCESS));
    }

    private void setDeliveredStatus() {
        prefixTimeView.setText(R.string.prefix_deliver_time);
        deliveredIcon.setImageResource(R.drawable.ic_delivered);
        bubbleLayout.post(() -> bubbleLayout.setArrowPosition(StatusBubbleLayout.DELIVERD));
    }
}
