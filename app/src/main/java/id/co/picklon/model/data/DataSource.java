package id.co.picklon.model.data;

import android.content.Context;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import id.co.picklon.model.entities.AD;
import id.co.picklon.model.entities.Address;
import id.co.picklon.model.entities.Article;
import id.co.picklon.model.entities.Coupon;
import id.co.picklon.model.entities.Inbox;
import id.co.picklon.model.entities.Order;
import id.co.picklon.model.entities.Response;
import id.co.picklon.model.entities.Token;
import id.co.picklon.model.entities.UserInbox;
import id.co.picklon.model.rest.PicklonService;
import id.co.picklon.model.rest.utils.HttpResultFunc;
import id.co.picklon.model.rest.utils.RxUtil;
import id.co.picklon.utils.Picklon;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

@SuppressWarnings("unchecked")
public class DataSource {
    private Context mContext;
    private PicklonService service;

    @Inject
    public DataSource(Context context, PicklonService service) {
        this.mContext = context;
        this.service = service;
    }

    private Observable<String> getPtk() {
        return service.getPtk()
                .map(new HttpResultFunc<>())
                .map(datas -> datas.get(0).getData());
    }

    // 2017/5/5 获取验证码 添加类型 type
    Subscription getVerifyCode(String phoneNumber,String type, Action1 successListener, Action1<Throwable> errorListener) {
        Map<String, Object> map = new HashMap<>();
        map.put("m", phoneNumber);
        map.put("type", type);
        return service.getVerifyCode(new Gson().toJson(map))
                .compose(RxUtil.applyIOToMainThreadSchedulers())
                .subscribe(successListener, errorListener);
    }

    Observable<Token> register(String phoneNumber, String password, String verifyCode) {
        return getPtk().flatMap(ptk -> doRegister(ptk, phoneNumber, password, verifyCode))
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    private Observable doRegister(String ptk, String phoneNumber, String password, String verifyCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("ptk", ptk);
        map.put("c", verifyCode);
        map.put("m", phoneNumber);
        map.put("p", password);
        map.put("devicetoken", Picklon.DEVICE_TOKEN);
        // 是否是商户端
        map.put("s", 0);

        return service.register(new Gson().toJson(map))
                .map(new HttpResultFunc<>())
                .map(datas -> datas.get(0).getData());
    }

    Observable<Token> login(String phoneNumber, String password) {
        return getPtk().flatMap(ptk -> doLogin(ptk, phoneNumber, password))
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    private Observable<Token> doLogin(String ptk, String phoneNumber, String password) {
        Map<String, Object> map = new HashMap<>();
        map.put("ptk", ptk);
        map.put("m", phoneNumber);
        map.put("p", password);
        map.put("s", 0);
        // zero means android
        map.put("device", "0");
        map.put("devicetoken", Picklon.DEVICE_TOKEN);

        return service.login(new Gson().toJson(map))
                .map(new HttpResultFunc<>())
                .map(datas -> datas.get(0).getData());
    }

    Observable<Object> resetPassword(String phoneNumber, String password, String verifyCode) {
        return getPtk().flatMap(ptk -> doReset(ptk, phoneNumber, password, verifyCode))
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    private Observable doReset(String ptk, String phoneNumber, String password, String verifyCode) {
        Map<String, Object> map = Picklon.commonMap();
        map.put("ptk", ptk);
        map.put("c", verifyCode);
        map.put("m", phoneNumber);
        map.put("p", password);
        map.put("s", 0);

        // empty list, so not fetch first data
        return service.resetPassword(new Gson().toJson(map))
                .map(new HttpResultFunc<>());
    }

    Observable<Object> changeMobile(String phoneNumber, String verifyCode) {
        Map<String, Object> map = Picklon.commonMap();
        map.put("c", verifyCode);
        map.put("m", phoneNumber);

        return service.changeMobile(Picklon.TOKEN, new Gson().toJson(map))
                .map(new HttpResultFunc<>())
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<List<Order>> getOrderList() {
        return service.getOrderList(Picklon.TOKEN)
                .map(new HttpResultFunc<>())
                .map(datas -> datas.get(0).getData())
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<List<Order>> getFinishOrder() {
        return service.getFinishOrder(Picklon.TOKEN)
                .map(new HttpResultFunc<>())
                .map(datas -> datas.get(0).getData())
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<List<Order>> getCanceledOrder() {
        return service.getCanceledOrder(Picklon.TOKEN)
                .map(new HttpResultFunc<>())
                .map(datas -> datas.get(0).getData())
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<Order> orderDetail(String id) {
        Map<String, Object> map = Picklon.commonMap();
        map.put("order_id", id);
        return service.orderDetail(Picklon.TOKEN, new Gson().toJson(map))
                .map(new HttpResultFunc<>())
                .map(datas -> datas.get(0).getData())
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<List<AD>> getAdList(int pos) {
        Map<String, Object> map = Picklon.commonMap();
        map.put("position", pos);
        return service.getAdList(Picklon.TOKEN, new Gson().toJson(map))
                .map(new HttpResultFunc<>())
                .map(datas -> datas.get(0).getData())
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    private Observable<List<Article>> getArticle(String type) {
        Map<String, Object> map = Picklon.commonMap();
        map.put("t", type);

        return service.getRegulation(new Gson().toJson(map))
                .map(new HttpResultFunc<>())
                .map(datas -> datas.get(0).getData())
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<List<Article>> getRegulation() {
        return getArticle("REDEEM");
    }

    public Observable<List<Article>> getTerms() {
        return getArticle("TERMSOFSERVICES");
    }

    // noused
    public Observable<List<Object>> getFanpage() {
        return service.getFanPage(Picklon.TOKEN)
                .map(new HttpResultFunc<>())
                .map(datas -> datas.get(0).getData())
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    // empty list if succcess
    public Observable<Object> becomePartner(String mobile, String email, String content) {
        Map<String, Object> map = Picklon.commonMap();
        map.put("m", mobile);
        map.put("e", email);
        map.put("c", content);

        return service.becomePartner(Picklon.TOKEN, new Gson().toJson(map))
                .map(new HttpResultFunc<>())
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<List<Article>> getAboutUs() {
        return getArticle("ABOUT_US");
    }

    public Observable<Order> reqNewOrder() {
        Map<String, Object> map = Picklon.commonMap();

        return service.reqNewOrder(Picklon.TOKEN, new Gson().toJson(map))
                .map(new HttpResultFunc<>())
                .map(datas -> datas.get(0).getData())
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<Object> getRegion() {
        return service.getRegion(Picklon.TOKEN)
                .map(new HttpResultFunc<>())
                .map(datas -> datas.get(0).getData())
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<List<Address>> getAddress() {
        return service.getAddressList(Picklon.TOKEN)
                .map(new HttpResultFunc<>())
                .map(datas -> datas.get(0).getData())
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<Address> addAddress(Address address) {
        Map<String, Object> map = Picklon.commonMap();
        map.put("a", address.getAddress());
        map.put("d", address.getDefaulted());
        map.put("lat", address.getLatitude());
        map.put("lon", address.getLongitude());

        return service.addAddress(Picklon.TOKEN, new Gson().toJson(map))
                .map(new HttpResultFunc<>())
                .map(datas -> datas.get(0).getData())
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<Object> editAddress(Address address) {
        Map<String, Object> map = Picklon.commonMap();
        map.put("id", address.getId());
        map.put("a", address.getAddress());
        map.put("d", address.getDefaulted());
        map.put("lat", address.getLatitude());
        map.put("lon", address.getLongitude());

        return service.editAddress(Picklon.TOKEN, new Gson().toJson(map))
                .map(new HttpResultFunc<>())
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<Object> delAddress(String ids) {
        Map<String, Object> map = Picklon.commonMap();
        map.put("a", ids);

        return service.delAddress(Picklon.TOKEN, new Gson().toJson(map))
                .map(new HttpResultFunc<>())
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<Object> getOrderConfig() {
        return service.getOrderConfig(Picklon.TOKEN)
                .map(Response::getDataList)
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<Object> commentOrder(String id, String comment, int star) {
        Map<String, Object> map = Picklon.commonMap();
        map.put("o", id);
        map.put("c", comment);
        map.put("s", star);

        return service.commentOrder(Picklon.TOKEN, new Gson().toJson(map))
                .map(new HttpResultFunc<>())
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<List<Coupon>> getCouponList() {
        return service.getCouponList(Picklon.TOKEN)
                .map(new HttpResultFunc<>())
                .map(datas -> datas.get(0).getData())
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<Coupon> getSystemCouponList() {
        return service.getSystemCouponList(Picklon.TOKEN)
                .map(new HttpResultFunc<>())
                .map(datas -> datas.get(0).getData())
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<List<Inbox>> getInboxList() {
        return service.getInboxList(Picklon.TOKEN)
                .map(new HttpResultFunc<>())
                .map(datas -> datas.get(0).getData())
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<List<UserInbox>> getUserInboxList() {
        return service.getUserInboxList(Picklon.TOKEN)
                .map(new HttpResultFunc<>())
                .map(datas -> datas.get(0).getData())
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<UserInbox> makeInboxRead(int id, int configId) {
        Map<String, Object> map = Picklon.commonMap();
        map.put("id", id);
        map.put("config_id", configId);

        return service.makeInboxRead(Picklon.TOKEN, new Gson().toJson(map))
                .map(new HttpResultFunc<>())
                .map(datas -> datas.get(0).getData())
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<Object> setOrderDiscount(String orderId, String couponId, String cId, String price, String payType) {
        Map<String, Object> map = Picklon.commonMap();
        map.put("o", orderId);
        map.put("user_coupon_id", couponId);
        map.put("c", cId);
        map.put("p", price);
        map.put("type", payType);

        return service.setOrderDiscount(Picklon.TOKEN, new Gson().toJson(map))
                .map(new HttpResultFunc<>())
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }

    public Observable<Object> feedBack(String content) {
        Map<String, Object> map = Picklon.commonMap();
        map.put("content", content);

        return service.feedback(Picklon.TOKEN, new Gson().toJson(map))
                .map(new HttpResultFunc<>())
                .compose(RxUtil.applyIOToMainThreadSchedulers());
    }
}
