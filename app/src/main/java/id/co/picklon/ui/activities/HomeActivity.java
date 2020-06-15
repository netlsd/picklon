package id.co.picklon.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.lightsky.infiniteindicator.InfiniteIndicator;
import cn.lightsky.infiniteindicator.indicator.CircleIndicator;
import cn.lightsky.infiniteindicator.page.OnPageClickListener;
import cn.lightsky.infiniteindicator.page.Page;
import id.co.picklon.R;
import id.co.picklon.model.data.AccountManager;
import id.co.picklon.model.data.DataSource;
import id.co.picklon.model.entities.AD;
import id.co.picklon.model.entities.Inbox;
import id.co.picklon.model.entities.Order;
import id.co.picklon.model.entities.Response;
import id.co.picklon.model.entities.UserInbox;
import id.co.picklon.model.entities.WashItem;
import id.co.picklon.model.entities.WashService;
import id.co.picklon.model.rest.utils.RxUtil;
import id.co.picklon.ui.adapter.WashStatusAdapter;
import id.co.picklon.ui.view.DividerItemDecoration;
import id.co.picklon.ui.view.HomeStatusBubbleLayout;
import id.co.picklon.ui.view.MenuView;
import id.co.picklon.ui.view.ScrollTextView;
import id.co.picklon.utils.Analytics;
import id.co.picklon.utils.Const;
import id.co.picklon.utils.L;
import id.co.picklon.utils.PicassoLoader;
import id.co.picklon.utils.Picklon;
import id.co.picklon.utils.TimeUtil;
import id.co.picklon.utils.Tool;
import rx.Observable;
import rx.Subscription;

import static id.co.picklon.ui.view.DividerItemDecoration.VERTICAL_LIST;
import static id.co.picklon.ui.view.WashStatusView.DELIVER;
import static id.co.picklon.ui.view.WashStatusView.HAVEORDER;
import static id.co.picklon.ui.view.WashStatusView.PICKUP;
import static id.co.picklon.ui.view.WashStatusView.STORE_CONFIRM;
import static id.co.picklon.ui.view.WashStatusView.WASHING;

public class HomeActivity extends ToolbarActivity implements OnPageClickListener {
    private ArrayList<Page> pageList;
    private List<AD> adList;
    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;
    private WashStatusAdapter adapter;
    private List<Order> pickupOrderList = new ArrayList<>();
    private List<Order> progressOrderList = new ArrayList<>();
    private List<Order> deliverOrderList = new ArrayList<>();
    private List<Subscription> subscriptionList = new ArrayList<>();

    @BindView(R.id.home_drawer)
    DrawerLayout drawerView;
    @BindView(R.id.home_viewpager)
    InfiniteIndicator pagerView;
    @BindView(R.id.drawer_username)
    TextView usernameView;
    @BindView(R.id.home_bubble_layout)
    HomeStatusBubbleLayout bubbleLayout;
    @BindView(R.id.home_pickup_number)
    TextView pickupNumberView;
    @BindView(R.id.home_process_number)
    TextView processNumberView;
    @BindView(R.id.home_deliverd_number)
    TextView deliverdNumberView;
    @BindView(R.id.home_status_list)
    RecyclerView statusListView;
    @BindView(R.id.home_no_order_text)
    TextView noOrderTextView;
    @BindView(R.id.home_scrolltext)
    ScrollTextView scrollTextView;
    @BindView(R.id.toolbar_inbox_number)
    TextView inboxNumberView;
    @BindView(R.id.drawer_news)
    MenuView newsMenuView;
    @BindView(R.id.drawer_laundry_number)
    TextView laundryNumberView;
    @BindView(R.id.scroll_text_layout)
    FrameLayout scrollLayout;

    @Inject
    DataSource dataSource;
    @Inject
    AccountManager accountManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        activityComponent().inject(this);

        initUi();
        initBroadcast();

        dataSource.getAdList(1).subscribe(this::adToPages);
        dataSource.getOrderConfig().retry(3).subscribe(this::loadOrderConfig, Throwable::printStackTrace);
        dataSource.getFinishOrder().subscribe(orders -> laundryNumberView.setText(String.valueOf(orders.size())), Throwable::printStackTrace);
        loadOrderList();

        Analytics.reset();
        Analytics.uploadLastUsedTime(this);
    }

    private void loadOrderList() {
        loadOrderList(true);
    }

    private void loadOrderList(boolean showTip) {
        dataSource.getOrderList().subscribe(orders -> handleOrders(orders, showTip), Throwable::printStackTrace);
    }

    private void loadInbox() {
        if (System.currentTimeMillis() - Picklon.LAST_READ_MS < Picklon.INBOX_MS) {
            return;
        }

        Observable.zip(dataSource.getInboxList(), dataSource.getUserInboxList(), dataSource.getSystemCouponList(),
                (inboxes, userInboxes, coupon) -> {
                    boolean isFind = false;
                    Picklon.userInboxList = userInboxes;
                    Picklon.inboxList.clear();

                    for (Inbox inbox : inboxes) {
                        for (UserInbox ui : userInboxes) {
                            if (ui.getInboxId() == inbox.getId()) {
                                isFind = true;
                                break;
                            }
                        }

                        if (!isFind) {
                            Picklon.inboxList.add(inbox);
                        }
                    }
                    return coupon.isNewCoupon();
                }).subscribe(isNewCoupon -> {
            int num = 0;
            for (UserInbox inbox : Picklon.userInboxList) {
                if (inbox.getReaded() == 0) {
                    num += 1;
                }
            }

            num += Picklon.inboxList.size();

//            if (isNewCoupon) {
//                num += 1;
//            }

            if (num > 0) {
                inboxNumberView.setVisibility(View.VISIBLE);
                inboxNumberView.setText(String.valueOf(num));
                newsMenuView.showRightIcon();
            } else {
                newsMenuView.hideRightIcon();
                inboxNumberView.setVisibility(View.GONE);
            }

            Picklon.LAST_READ_MS = System.currentTimeMillis();
        }, Throwable::printStackTrace);
    }

    private void handleOrders(List<Order> orders, boolean showTips) {
        pickupOrderList.clear();
        progressOrderList.clear();
        deliverOrderList.clear();
        Collections.reverse(orders);

        for (Order order : orders) {
            if (PICKUP.equals(order.getWashStatus()) || HAVEORDER.equals(order.getWashStatus()) || STORE_CONFIRM.equals(order.getWashStatus())) {
                // 状态覆盖成PICKUP
                order.setWashStatus(PICKUP);
                pickupOrderList.add(order);
            } else if (WASHING.equals(order.getWashStatus())) {
                progressOrderList.add(order);
            } else if (DELIVER.equals(order.getWashStatus())) {
                deliverOrderList.add(order);
            }
        }

        pickupNumberView.setText(String.valueOf(pickupOrderList.size()));
        processNumberView.setText(String.valueOf(progressOrderList.size()));
        deliverdNumberView.setText(String.valueOf(deliverOrderList.size()));

        if (showTips) {
            showTips();
        }
    }

    private void showTips() {
        long threshold = TimeUnit.MILLISECONDS.convert(30, TimeUnit.MINUTES);
        unSubscribeNotify();

        List<Order> orderList = new ArrayList<>();
        orderList.addAll(pickupOrderList);
        orderList.addAll(deliverOrderList);

        for (Order order : orderList) {
            long time;
            if (DELIVER.equals(order.getWashStatus())) {
                time = TimeUtil.getTimeStamp(order.getDeliverTime());
            } else {
                time = TimeUtil.getTimeStamp(order.getPickupTime());
            }

            if (time - System.currentTimeMillis() < 0) {
                continue;
            }

            if (time - System.currentTimeMillis() < threshold) {
                orderNotify(order);
                // 只显示一个订单
                return;
            } else {
                long delay = time - System.currentTimeMillis() - threshold;
                subscriptionList.add(Observable.timer(delay, TimeUnit.MILLISECONDS)
                        .compose(RxUtil.applyIOToMainThreadSchedulers())
                        .subscribe(aLong -> orderNotify(order)));
            }
        }

        scrollTextView.setVisibility(View.GONE);
    }

    private void orderNotify(Order order) {
        if (DELIVER.equals(order.getWashStatus())) {
            scrollTextView.setText(getString(R.string.deliver_tips));
        } else {
            scrollTextView.setText(getString(R.string.pickup_tips));
        }

        scrollTextView.setCount(3);
        scrollTextView.post(() -> scrollTextView.startScroll());
        Tool.playSound(this);
    }

    @SuppressWarnings("unchecked")
    private void loadOrderConfig(Object o) {
        List<Response<Object>.Data<List<Object>>> list = (List<Response<Object>.Data<List<Object>>>) o;
        if (list.isEmpty()) {
            return;
        }

        if (list.get(0).getCode() == 5005) {
            String json = new Gson().toJson(list.get(0).getData());
            Picklon.serviceList = new Gson().fromJson(json, new TypeToken<List<WashService>>() {
            }.getType());
        }

        if (list.get(1) != null && list.get(1).getCode() == 5006) {
            String json = new Gson().toJson(list.get(1).getData());
            Picklon.itemList = new Gson().fromJson(json, new TypeToken<List<WashItem>>() {
            }.getType());
        }
    }

    private void initUi() {
        setupToolBar();
        setupDrawer();
        initializeRecyclerView();

        pickupNumberView.setOnClickListener(view -> {
            bubbleLayout.setArrowPosition(HomeStatusBubbleLayout.PICKUP);
            if (pickupOrderList.isEmpty()) {
                statusListView.setVisibility(View.GONE);
                noOrderTextView.setVisibility(View.VISIBLE);
            } else {
                statusListView.setVisibility(View.VISIBLE);
                noOrderTextView.setVisibility(View.GONE);
                adapter.addItems(pickupOrderList);
            }
        });

        processNumberView.setOnClickListener(view -> {
            bubbleLayout.setArrowPosition(HomeStatusBubbleLayout.PROCESS);
            if (progressOrderList.isEmpty()) {
                statusListView.setVisibility(View.GONE);
                noOrderTextView.setVisibility(View.VISIBLE);
            } else {
                statusListView.setVisibility(View.VISIBLE);
                noOrderTextView.setVisibility(View.GONE);
                adapter.addItems(progressOrderList);
                adapter.hidePickupView();
            }
        });

        deliverdNumberView.setOnClickListener(view -> {
            bubbleLayout.setArrowPosition(HomeStatusBubbleLayout.DELIVERD);

            if (deliverOrderList.isEmpty()) {
                statusListView.setVisibility(View.GONE);
                noOrderTextView.setVisibility(View.VISIBLE);
            } else {
                statusListView.setVisibility(View.VISIBLE);
                noOrderTextView.setVisibility(View.GONE);
                adapter.addItems(deliverOrderList);
            }
        });
    }

    private void initializeRecyclerView() {
        adapter = new WashStatusAdapter(this);
        statusListView.setAdapter(adapter);
        statusListView.addItemDecoration(new DividerItemDecoration(this, VERTICAL_LIST));
        statusListView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupDrawer() {
        setupUsername();
    }

    private void setupUsername() {
        usernameView.setText(accountManager.getMobile());
    }

    private void adToPages(List<AD> ads) {
        adList = ads;
        pageList = new ArrayList<>();
        for (AD ad : ads) {
            pageList.add(new Page(String.valueOf(ad.getId()), Picklon.MEDIAHOST + ad.getImage(), this));
        }

        setPages();
    }

    private void setPages() {
        pagerView.setImageLoader(new PicassoLoader());
        pagerView.addPages(pageList);
        pagerView.setPosition(InfiniteIndicator.IndicatorPosition.Right_Bottom);

        CircleIndicator circleIndicator = ((CircleIndicator) pagerView.getPagerIndicator());
        final float density = getResources().getDisplayMetrics().density;
        circleIndicator.setRadius(4 * density);
        circleIndicator.setPageColor(Color.BLACK);
        circleIndicator.setFillColor(Color.RED);
        circleIndicator.setStrokeColor(Color.TRANSPARENT);
    }

    @Override
    public void onPageClick(int position, Page page) {
        Tool.openBrowser(this, adList.get(position).getDesc());
    }

    @Override
    protected void onPause() {
        super.onPause();
        pagerView.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pagerView.start();
        loadInbox();
    }

    private void setupToolBar() {
        setToolbarTitle(R.string.picklon_logo);
        setToolbarTitleSize(22);

        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerView, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }
        };
        mActionBarDrawerToggle.syncState();
        setToolbarIconListener(view -> startActivity(new Intent(this, InboxActivity.class)));
    }

    private void initBroadcast() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.UPDATE_USERNAME);
        intentFilter.addAction(Const.ORDER_COMPLETED);
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }

    @OnClick({R.id.drawer_my_address, R.id.drawer_my_wallet, R.id.drawer_partner, R.id.drawer_share, R.id.drawer_setting, R.id.drawer_logout, R.id.drawer_news})
    void drawerClickEvent(View view) {
        switch (view.getId()) {
            case R.id.drawer_my_address:
                startActivity(new Intent(this, MyAddressActivity.class));
                break;
            case R.id.drawer_my_wallet:
                startActivity(new Intent(this, MyWalletActivity.class));
                break;
            case R.id.drawer_partner:
                startActivity(new Intent(this, PartnerActivity.class));
                break;
            case R.id.drawer_share:
                startActivity(new Intent(this, ShareActivity.class));
                break;
            case R.id.drawer_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.drawer_logout:
                accountManager.logout();
                break;
            case R.id.drawer_news:
                startActivity(new Intent(this, InboxActivity.class));
                break;
        }
        drawerView.closeDrawers();
    }

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Const.UPDATE_USERNAME)) {
                setupUsername();
            } else if (intent.getAction().equals(Const.ORDER_COMPLETED)) {
                loadOrderList(false);
            }
        }
    }

    private void unSubscribeNotify() {
        for (Subscription subscription : subscriptionList) {
            if (!subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Picklon.LAST_READ_MS = 0;
        unSubscribeNotify();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }
}
