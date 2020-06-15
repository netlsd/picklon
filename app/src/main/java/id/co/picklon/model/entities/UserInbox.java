package id.co.picklon.model.entities;

import com.google.gson.annotations.SerializedName;

public class UserInbox {
    private int id;
    @SerializedName("inbox")
    private int inboxId;
    @SerializedName("inbox_title")
    private String inboxTitle;
    @SerializedName("inbox_content")
    private String inboxContent;
    private String image;
    @SerializedName("created_at")
    private String createdAt;
    private int readed;
    @SerializedName("is_coupon")
    private int isCoupon;
    @SerializedName("coupon_info")
    private String couponInfo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInboxId() {
        return inboxId;
    }

    public void setInboxId(int inboxId) {
        this.inboxId = inboxId;
    }

    public String getInboxTitle() {
        return inboxTitle;
    }

    public void setInboxTitle(String inboxTitle) {
        this.inboxTitle = inboxTitle;
    }

    public String getInboxContent() {
        return inboxContent;
    }

    public void setInboxContent(String inboxContent) {
        this.inboxContent = inboxContent;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getReaded() {
        return readed;
    }

    public void setReaded(int readed) {
        this.readed = readed;
    }

    public int getIsCoupon() {
        return isCoupon;
    }

    public void setIsCoupon(int isCoupon) {
        this.isCoupon = isCoupon;
    }

    public String getCouponInfo() {
        return couponInfo;
    }

    public void setCouponInfo(String couponInfo) {
        this.couponInfo = couponInfo;
    }
}
