package id.co.picklon.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.http.WebSocket;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.model.entities.Order;
import id.co.picklon.model.rest.PicklonSocket;
import id.co.picklon.model.rest.utils.RxUtil;
import id.co.picklon.ui.view.CircularProgress;
import id.co.picklon.utils.AES;
import id.co.picklon.utils.L;
import id.co.picklon.utils.Picklon;
import id.co.picklon.utils.TimeUtil;
import id.co.picklon.utils.ViewUtils;
import rx.Observable;
import rx.Subscription;

import static id.co.picklon.utils.Tool.getFormatedValue;

public class OrderRequestActivity extends ToolbarActivity implements PicklonSocket.OnStringCallback {
    private static final String ORDER = "order";

    @BindView(R.id.request_progress)
    CircularProgress progressView;
    @BindView(R.id.request_estimate_price)
    TextView estimatePriceView;
    @BindView(R.id.request_tips)
    TextView tipsView;
    @BindView(R.id.request_cancel_order)
    Button cancelOrderBtn;
    @BindView(R.id.request_shop_count)
    TextView shopCountView;

    private Order order;
    private Subscription subscription;
    private PicklonSocket picklonSocket = new PicklonSocket();
    private boolean isSuccess = false;

    public static void start(Context context, Order order) {
        Intent intent = new Intent(context, OrderRequestActivity.class);
        intent.putExtra(ORDER, order);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_request);

        order = getIntent().getParcelableExtra(ORDER);
        initUi();
        startFind();
    }

    private void startFind() {
        picklonSocket.setOnStringCallback(this);
        Observable.create((Observable.OnSubscribe<WebSocket>) subscriber -> picklonSocket.getDefault())
                .compose(RxUtil.applyIOToMainThreadSchedulers())
                .subscribe(webSocket -> {
                }, this::handleError);
    }

    @Override
    public void onString(String msg) {
        String result = decrypt(msg);
        Packet packet = new Gson().fromJson(result, Packet.class);

        if (packet.i == 10001 && (boolean) packet.d) {
            sendOrder();
        } else if (packet.i == 20001) {
            Packet<Order> orderPacket = new Gson().fromJson(result, new TypeToken<Packet<Order>>() {
            }.getType());
            order.setOrderId(orderPacket.d.getOrderId());
            showShopCount(orderPacket.d.getShopCount());
        } else if (packet.i == 20002) {
            Packet<Order> orderPacket = new Gson().fromJson(result, new TypeToken<Packet<Order>>() {
            }.getType());
            isSuccess = true;
            OrderSuccessActivity.start(this, orderPacket.d);
            finish();
        }
    }

    private void showShopCount(final int shopCount) {
        runOnUiThread(() -> shopCountView.setText(String.valueOf(shopCount)));

        int interval = new Random().nextInt(10) + 10;

        Observable.interval(2, interval, TimeUnit.SECONDS)
                .takeWhile(aLong -> aLong < 120 / interval)
                .compose(RxUtil.applyIOToMainThreadSchedulers())
                .subscribe(aLong -> {
                    int currentNumber = Integer.parseInt(shopCountView.getText().toString());
                    currentNumber += new Random().nextInt(4);
                    shopCountView.setText(String.valueOf(currentNumber));
                } );
    }

    private void sendOrder() {
        Map<String, Object> map = Picklon.commonMap();
        map.put("address_id", order.getAddress().getId());
        map.put("pk_time", TimeUtil.getPostTime(order.getPickTime()));
        map.put("dl_time", TimeUtil.getPostTime(order.getdTime()));
        map.put("sp", order.getRequirements());
        map.put("services", order.getServices());
        map.put("tips", order.getTips());
        map.put("lat", order.getAddress().getLatitude());
        map.put("lng", order.getAddress().getLongitude());
        map.put("address_detail", order.getAddress().getAddress());

        Map<String, String> postMap = new HashMap<>();
        postMap.put("d", encrypt(new Gson().toJson(map)));
        postMap.put("path", "20001");

        L.e("post is " + new Gson().toJson(map));

        picklonSocket.send(new Gson().toJson(postMap));
    }

    public void cancelOrder() {
        Map<String, Object> map = Picklon.commonMap();
        map.put("order_id", order.getOrderId());

        Map<String, String> postMap = new HashMap<>();
        postMap.put("d", encrypt(new Gson().toJson(map)));
        postMap.put("path", "20003");

        picklonSocket.send(new Gson().toJson(postMap));
    }

    private void handleError(Throwable throwable) {
        throwable.printStackTrace();
        new AlertDialog.Builder(this)
                .setMessage(R.string.network_error)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    finish();
                });
    }

    private void initUi() {
        toolbar.setNavigationOnClickListener(view -> ViewUtils.cancelOrderDialog(this));
        cancelOrderBtn.setOnClickListener(view -> ViewUtils.cancelOrderDialog(this));

        subscription = Observable.interval(CircularProgress.INTERVAL, TimeUnit.MILLISECONDS)
                .compose(RxUtil.applyIOToMainThreadSchedulers())
                .take(CircularProgress.MAX_PROGRESS)
                .subscribe(aLong -> {
                    if (aLong == CircularProgress.MAX_PROGRESS - 1) {
                        timeout();
                    }
                    progressView.setProgress(aLong.intValue() + 1);
                });

        estimatePriceView.setText(getFormatedValue(order.getEstimatePrice()));
        tipsView.setText(getFormatedValue(order.getTips()));
    }

    private void timeout() {
        cancelOrder();

        ViewUtils.showDialog(this, R.string.order_timeout_warning, (dialogInterface, i) -> {
            dialogInterface.dismiss();
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        ViewUtils.cancelOrderDialog(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clean();
    }

    private void clean() {
        if (!isSuccess) {
            cancelOrder();
        }

        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        picklonSocket.close();
    }

    class Packet<T> {
        int i;
        T d;
    }

    private String decrypt(String content) {
        if (content == null) {
            return null;
        }

        return AES.decrypt(content);
    }

    private String encrypt(String content) {
        try {
            return URLEncoder.encode(AES.encrypt(content), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
