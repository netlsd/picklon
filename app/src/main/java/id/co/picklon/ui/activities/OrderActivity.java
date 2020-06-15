package id.co.picklon.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.model.data.DataSource;
import id.co.picklon.model.entities.Order;
import id.co.picklon.model.entities.WashService;
import id.co.picklon.ui.view.TipsView;
import id.co.picklon.utils.Const;
import id.co.picklon.utils.L;
import id.co.picklon.utils.Picklon;
import id.co.picklon.utils.TimeUtil;

public class OrderActivity extends ToolbarActivity {
    private static final String ORDER = "order";
    private Order order;
    private long carpetPrice = 0;
    private long clothesPrice = 0;

    @BindView(R.id.order_pick_up_date)
    TextView pickDateView;
    @BindView(R.id.order_pick_up_time)
    TextView pickTimeView;
    @BindView(R.id.order_deliver_date)
    TextView deliverDate;
    @BindView(R.id.order_deliver_time)
    TextView deliverTime;
    @BindView(R.id.order_special_requirements)
    TextView requirementsView;
    @BindView(R.id.order_price)
    TextView priceView;
    @BindView(R.id.order_edit)
    Button orderEditView;
    @BindView(R.id.order_address)
    TextView addressView;
    @BindView(R.id.order_wash_type)
    TextView washTypeView;
    @BindView(R.id.order_weight)
    TextView weightView;
    @BindView(R.id.order_order_now)
    Button orderNowView;
    @BindView(R.id.order_wash_icon)
    ImageView washIconView;
    @BindView(R.id.order_carpet_icon)
    ImageView carpetIconView;
    @BindView(R.id.order_carpet_text)
    TextView carpetTextView;
    @BindView(R.id.order_carpet_meters)
    TextView metersView;
    @BindView(R.id.order_tips)
    TipsView tipsView;

    @Inject
    DataSource dataSource;

    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;

    public static void start(Context context, Order order) {
        Intent intent = new Intent(context, OrderActivity.class);
        intent.putExtra(ORDER, order);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        activityComponent().inject(this);

        order = getIntent().getParcelableExtra(ORDER);

        initUi();
        initBroadcast();
        loadOrderPrice();
    }

    private void loadOrderPrice() {
        if (Picklon.serviceList == null || Picklon.serviceList.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.order_failure)
                    .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        finish();
                    });
        } else {
            calcPrice();
        }
    }

    //todo 比对名称
    private void calcPrice() {
        for (WashService washService : Picklon.serviceList) {
            if (washService.getId() == order.getWashType() + 1 && washService.getServiceType() == 1) {
                clothesPrice = washService.getRp() * order.getWashWeight();
            }

            if (order.isCarpet() && washService.getServiceType() == 2) {
                carpetPrice = washService.getRp() * order.getMeters();
            }
        }
        priceView.setText(getString(R.string.unit_rp, String.valueOf(clothesPrice + carpetPrice)));
    }

    private void initUi() {
        priceView.setText(getString(R.string.unit_rp, "0"));
        addressView.setText(order.getAddress().getAddress());

        if (order.getWashType() == Const.WASH_TYPE.length - 1) {
            washIconView.setVisibility(View.GONE);
            washTypeView.setVisibility(View.GONE);
            weightView.setVisibility(View.GONE);
        } else {
            washTypeView.setText(Const.WASH_TYPE[order.getWashType()]);
            if (order.getWashType() == 0) {
                washIconView.setImageResource(R.drawable.ic_wash_fold);
            } else if (order.getWashType() == 1) {
                washIconView.setImageResource(R.drawable.ic_wash_iron);
            } else if (order.getWashType() == 2) {
                washIconView.setImageResource(R.drawable.ic_iron_only);
            }
        }

        if (order.isCarpet()) {
            metersView.setText(getString(R.string.unit_number, order.getMeters()));
        } else {
            carpetIconView.setVisibility(View.GONE);
            carpetTextView.setVisibility(View.GONE);
            metersView.setVisibility(View.GONE);
        }

        weightView.setText(getString(R.string.unit_number, order.getWashWeight()));
        pickDateView.setText(TimeUtil.getFormatedDate(order.getPickTime()));
        pickTimeView.setText(TimeUtil.getFormatedTime(order.getPickTime()));
        deliverDate.setText(TimeUtil.getFormatedDate(order.getdTime()));
        deliverTime.setText(TimeUtil.getFormatedTime(order.getdTime()));
        requirementsView.setText(order.getRequirements());

        orderEditView.setOnClickListener(view1 -> finish());
        orderNowView.setOnClickListener(view1 -> {
            order.setTips(tipsView.getTips());
            order.setEstimatePrice(clothesPrice + carpetPrice);
            setServices();
            OrderRequestActivity.start(this, order);
        });

        tipsView.setOnTipsChangeListener(() ->
                priceView.setText(getString(R.string.unit_rp,
                        String.valueOf(clothesPrice + carpetPrice + tipsView.getTips()))));
    }

    private void setServices() {
        String services = "";
        if (isWashClothes()) {
            services = getString(Const.WASH_TYPE[order.getWashType()]);
            services += ",";
            services += order.getWashWeight() == 0 ? 0 : clothesPrice / order.getWashWeight();
            services += ",";
            services += order.getWashWeight();
            services += ";";
        }

        if (order.isCarpet()) {
            services += "Carpet";
            services += ",";
            services += order.getMeters() == 0 ? 0 : carpetPrice / order.getMeters();
            services += ",";
            services += order.getMeters();
        }

        order.setServices(services);
    }

    private boolean isWashClothes() {
        return order.getWashType() != Const.WASH_TYPE.length - 1;
    }

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }

    private void initBroadcast() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.ORDER_COMPLETED);
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }
}
