package id.co.picklon.model.entities;

import com.google.gson.annotations.SerializedName;

import id.co.picklon.R;
import id.co.picklon.utils.Tool;

public class Coupon {
    private String id;
    @SerializedName("coupon_id")
    private String couponId;
    @SerializedName("coupon_name")
    private String name;
    @SerializedName("coupon_value")
    private long value;
    private String description;
    @SerializedName("expire_days")
    private int expireDay;
    @SerializedName("expire_date")
    private String expireDate;
    private boolean isNewCoupon;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getExpireDay() {
        return expireDay;
    }

    public void setExpireDay(int expireDay) {
        this.expireDay = expireDay;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    @Override
    public String toString() {
        return Tool.getFormatedValue(value);
    }

    public boolean isNewCoupon() {
        return isNewCoupon;
    }

    public void setNewCoupon(boolean newCoupon) {
        isNewCoupon = newCoupon;
    }
}
