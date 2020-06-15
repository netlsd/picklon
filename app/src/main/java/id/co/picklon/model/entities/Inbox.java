package id.co.picklon.model.entities;

import com.google.gson.annotations.SerializedName;

public class Inbox {
    private int id;
    @SerializedName("inbox_title")
    private String inboxTitle;
    private String image;
    @SerializedName("inbox_content")
    private String inboxContent;
    @SerializedName("begin_time")
    private String beginTime;
    @SerializedName("end_time")
    private String endTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInboxTitle() {
        return inboxTitle;
    }

    public void setInboxTitle(String inboxTitle) {
        this.inboxTitle = inboxTitle;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getInboxContent() {
        return inboxContent;
    }

    public void setInboxContent(String inboxContent) {
        this.inboxContent = inboxContent;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
