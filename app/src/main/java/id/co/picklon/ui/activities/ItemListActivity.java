package id.co.picklon.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.model.data.DataSource;
import id.co.picklon.model.entities.Order;
import id.co.picklon.model.rest.utils.ProgressSubscriber;
import id.co.picklon.ui.adapter.WashItemAdapter;
import id.co.picklon.ui.view.DividerItemDecoration;
import id.co.picklon.utils.L;
import id.co.picklon.utils.Tool;

import static id.co.picklon.ui.view.DividerItemDecoration.VERTICAL_LIST;

public class ItemListActivity extends ToolbarActivity {
    private static final String ORDER = "order";

    @BindView(R.id.item_list_list)
    RecyclerView listView;
    @BindView(R.id.item_list_total)
    TextView totalView;
    @BindView(R.id.item_list_confirm)
    Button confirmView;
    @BindView(R.id.item_list_metres)
    TextView metresView;
    @BindView(R.id.item_list_top_layout)
    RelativeLayout topLayout;
    @BindView(R.id.item_list_carpet_layout)
    LinearLayout carpetLayout;

    @Inject
    Picasso picasso;
    @Inject
    DataSource dataSource;

    private Order order;
    private String washItems;

    public static void start(Context context, Order order) {
        Intent intent = new Intent(context, ItemListActivity.class);
        intent.putExtra(ORDER, order);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        activityComponent().inject(this);

        setToolbarTitle(R.string.item_list);

        order = getIntent().getParcelableExtra(ORDER);

        // 从订单列表进入的订单缺少详情数据
        if (order.getShopOrdersCount() == null) {
            dataSource.orderDetail(order.getOrderId())
                    .subscribe(new ProgressSubscriber<>(this, order1 -> {
                        order = order1;
                        initUi();
                    }));
        } else {
            initUi();
        }
    }

    private void initUi() {
        String[] services = order.getServices().split(";");
        String carpet = Tool.getCarpet(services);
        washItems = order.getWashItems();

        if (TextUtils.isEmpty(washItems)) {
            if (carpet == null) {
                return;
            }

            String[] c = carpet.split(",");

            carpetLayout.setVisibility(View.VISIBLE);
            metresView.setText(getString(R.string.prefix_meters, c[c.length - 1]));
            listView.setVisibility(View.GONE);
            topLayout.setVisibility(View.GONE);
            totalView.setVisibility(View.GONE);
        } else {
            carpetLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            topLayout.setVisibility(View.VISIBLE);

            if (carpet != null) {
                washItems = washItems.concat(carpet);
            }

            totalView.setText(getString(R.string.prefix_items_total, Tool.getItemCount(order)));
        }

        confirmView.setOnClickListener(view -> {
            PaymentActivity.start(this, order);
            finish();
        });

        initializeRecyclerView();
    }

    private void initializeRecyclerView() {
        WashItemAdapter adapter = new WashItemAdapter(this, picasso);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapter);
        listView.addItemDecoration(new DividerItemDecoration(this, VERTICAL_LIST));
        adapter.addItem(washItems.split(";"));
    }
}
