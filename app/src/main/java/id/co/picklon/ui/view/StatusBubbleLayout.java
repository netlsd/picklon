package id.co.picklon.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.daasuu.bl.BubbleLayout;

import id.co.picklon.utils.ViewUtils;

public class StatusBubbleLayout extends BubbleLayout {
    public static final int PICKUP = 0;
    public static final int CONFIRM = 1;
    public static final int PROCESS = 2;
    public static final int DELIVERD = 3;

    private int pickupPosition;
    private int confirmPosition;
    private int processPosition;
    private int deliveredPosition;

    private int lastStatus = -1;
    private Context mContext;

    public StatusBubbleLayout(Context context) {
        this(context, null);
    }

    public StatusBubbleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int fixWidth = ViewUtils.dpToPix(mContext, 5);
        int bubbleWidth = getWidth();

        int quarterWidth = bubbleWidth / 4;
        int halfOfQuarterWidth = quarterWidth / 2;

        pickupPosition = quarterWidth - halfOfQuarterWidth - fixWidth;
        confirmPosition = quarterWidth * 2 - halfOfQuarterWidth - fixWidth;
        processPosition = quarterWidth * 3 - halfOfQuarterWidth + fixWidth;
        deliveredPosition = bubbleWidth - halfOfQuarterWidth + fixWidth;
    }

    public void setArrowPosition(int status) {
        if (status == PICKUP) {
            toggle(status);
            super.setArrowPosition(pickupPosition);
        } else if (status == CONFIRM) {
            toggle(status);
            super.setArrowPosition(confirmPosition);
        } else if (status == PROCESS) {
            toggle(status);
            super.setArrowPosition(processPosition);
        } else if (status == DELIVERD) {
            toggle(status);
            super.setArrowPosition(deliveredPosition);
        }

        if (isShown()) {
            lastStatus = status;
        }
    }

    private void toggle(int status) {
        if (status == lastStatus) {
            setVisibility(INVISIBLE);
            lastStatus = -1;
        } else {
            setVisibility(VISIBLE);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }
}
