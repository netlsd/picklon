package id.co.picklon.model.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Order implements Parcelable {
    private int washType;
    private int washWeight;
    private boolean isCarpet;
    private int meters;
    private Address address;
    private String requirements;
    private DateTime pickTime;
    private DateTime dTime;
    private int tips;
    private long estimatePrice;
    private String services;

    @SerializedName("order_id")
    private String orderId;
    @SerializedName("wash_status")
    private String washStatus;
    @SerializedName("pickup_time")
    private String pickupTime;
    @SerializedName("deliver_time")
    private String deliverTime;
    @SerializedName("pickup_guy_name")
    private String pickupGuyName;
    @SerializedName("pickup_guy_mobile")
    private String pickupGuyMobile;
    @SerializedName("address_detail")
    private String addressDetail;
    private int star;
    @SerializedName("shop_name")
    private String shopName;
    @SerializedName("shop_id")
    private int shopId;
    @SerializedName("shop_phone")
    private String shopPhone;
    @SerializedName("dis")
    private long distance;
    @SerializedName("shop_image")
    private String shopImage;
    private String price;
    @SerializedName("wash_items")
    private String washItems;
    @SerializedName("deliver_fee")
    private long deliverFee;
    @SerializedName("shop_orders_count")
    private String shopOrdersCount;
    @SerializedName("shop_stars")
    private float shopStart;
    @SerializedName("pay_type")
    private String payType;
    @SerializedName("shop_count")
    private int shopCount;
    @SerializedName("coupon_rp")
    private String couponRp;

    public int getWashType() {
        return washType;
    }

    public void setWashType(int washType) {
        this.washType = washType;
    }

    public int getWashWeight() {
        return washWeight;
    }

    public void setWashWeight(int washWeight) {
        this.washWeight = washWeight;
    }

    public boolean isCarpet() {
        return isCarpet;
    }

    public void setCarpet(boolean carpet) {
        isCarpet = carpet;
    }

    public int getMeters() {
        return meters;
    }

    public void setMeters(int meters) {
        this.meters = meters;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getDeliverTime() {
        return deliverTime;
    }

    public void setDeliverTime(String deliverTime) {
        this.deliverTime = deliverTime;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getWashStatus() {
        return washStatus;
    }

    public void setWashStatus(String washStatus) {
        this.washStatus = washStatus;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getPickupGuyName() {
        return pickupGuyName;
    }

    public void setPickupGuyName(String pickupGuyName) {
        this.pickupGuyName = pickupGuyName;
    }

    public String getPickupGuyMobile() {
        return pickupGuyMobile;
    }

    public void setPickupGuyMobile(String pickupGuyMobile) {
        this.pickupGuyMobile = pickupGuyMobile;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public DateTime getPickTime() {
        return pickTime;
    }

    public void setPickTime(DateTime pickTime) {
        this.pickTime = pickTime;
    }

    public DateTime getdTime() {
        return dTime;
    }

    public void setdTime(DateTime dTime) {
        this.dTime = dTime;
    }

    public int getTips() {
        return tips;
    }

    public void setTips(int tips) {
        this.tips = tips;
    }

    public long getEstimatePrice() {
        return estimatePrice;
    }

    public void setEstimatePrice(long estimatePrice) {
        this.estimatePrice = estimatePrice;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public String getShopPhone() {
        return shopPhone;
    }

    public void setShopPhone(String shopPhone) {
        this.shopPhone = shopPhone;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public String getShopImage() {
        return shopImage;
    }

    public void setShopImage(String shopImage) {
        this.shopImage = shopImage;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getWashItems() {
        return washItems;
    }

    public void setWashItems(String washItems) {
        this.washItems = washItems;
    }

    public long getDeliverFee() {
        return deliverFee;
    }

    public void setDeliverFee(long deliverFee) {
        this.deliverFee = deliverFee;
    }

    public String getShopOrdersCount() {
        return shopOrdersCount;
    }

    public void setShopOrdersCount(String shopOrdersCount) {
        this.shopOrdersCount = shopOrdersCount;
    }

    public float getShopStart() {
        return shopStart;
    }

    public void setShopStart(int shopStart) {
        this.shopStart = shopStart;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public int getShopCount() {
        return shopCount;
    }

    public void setShopCount(int shopCount) {
        this.shopCount = shopCount;
    }

    public void setShopStart(float shopStart) {
        this.shopStart = shopStart;
    }

    public String getCouponRp() {
        return couponRp;
    }

    public void setCouponRp(String couponRp) {
        this.couponRp = couponRp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.washType);
        dest.writeInt(this.washWeight);
        dest.writeByte(this.isCarpet ? (byte) 1 : (byte) 0);
        dest.writeInt(this.meters);
        dest.writeParcelable(this.address, flags);
        dest.writeString(this.requirements);
        dest.writeParcelable(this.pickTime, flags);
        dest.writeParcelable(this.dTime, flags);
        dest.writeString(this.orderId);
        dest.writeString(this.washStatus);
        dest.writeString(this.pickupTime);
        dest.writeString(this.deliverTime);
        dest.writeString(this.pickupGuyName);
        dest.writeString(this.pickupGuyMobile);
        dest.writeString(this.addressDetail);
        dest.writeInt(this.star);
        dest.writeString(this.shopName);
        dest.writeInt(this.shopId);
        dest.writeInt(this.tips);
        dest.writeLong(this.estimatePrice);
        dest.writeString(this.services);
        dest.writeString(this.shopPhone);
        dest.writeLong(this.distance);
        dest.writeString(this.shopImage);
        dest.writeString(this.price);
        dest.writeString(this.washItems);
        dest.writeLong(this.deliverFee);
        dest.writeString(this.shopOrdersCount);
        dest.writeFloat(this.shopStart);
        dest.writeString(this.payType);
        dest.writeInt(this.shopCount);
        dest.writeString(this.couponRp);
    }

    public Order() {
    }

    protected Order(Parcel in) {
        this.washType = in.readInt();
        this.washWeight = in.readInt();
        this.isCarpet = in.readByte() != 0;
        this.meters = in.readInt();
        this.address = in.readParcelable(Address.class.getClassLoader());
        this.requirements = in.readString();
        this.pickTime = in.readParcelable(DateTime.class.getClassLoader());
        this.dTime = in.readParcelable(DateTime.class.getClassLoader());
        this.orderId = in.readString();
        this.washStatus = in.readString();
        this.pickupTime = in.readString();
        this.deliverTime = in.readString();
        this.pickupGuyName = in.readString();
        this.pickupGuyMobile = in.readString();
        this.addressDetail = in.readString();
        this.star = in.readInt();
        this.shopName = in.readString();
        this.shopId = in.readInt();
        this.tips = in.readInt();
        this.estimatePrice = in.readLong();
        this.services = in.readString();
        this.shopPhone = in.readString();
        this.distance = in.readLong();
        this.shopImage = in.readString();
        this.price = in.readString();
        this.washItems = in.readString();
        this.deliverFee = in.readLong();
        this.shopOrdersCount = in.readString();
        this.shopStart = in.readFloat();
        this.payType = in.readString();
        this.shopCount = in.readInt();
        this.couponRp = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {
            return new Order(source);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}
