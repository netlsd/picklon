package id.co.picklon.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.daasuu.bl.BubbleLayout;

import id.co.picklon.utils.L;
import id.co.picklon.utils.ViewUtils;

public class HomeStatusBubbleLayout extends BubbleLayout {
    public static final int PICKUP = 0;
    public static final int PROCESS = 1;
    public static final int DELIVERD = 2;

    private int pickupPosition;
    private int processPosition;
    private int deliveredPosition;

    private int lastStatus = -1;
    private Context mContext;

    public HomeStatusBubbleLayout(Context context) {
        this(context, null);
    }

    public HomeStatusBubbleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int arrowWidth = ViewUtils.dpToPix(mContext, 3);
        int leftMargin = ViewUtils.dpToPix(mContext, 8);
        int bubbleWidth = getWidth();
        int oneThirdWidth = bubbleWidth / 3;

        pickupPosition = oneThirdWidth / 2 - leftMargin - arrowWidth;
        processPosition = bubbleWidth / 2 - arrowWidth;
        deliveredPosition = bubbleWidth - oneThirdWidth / 2;
    }

    public void setArrowPosition(int status) {
        if (status == PICKUP) {
            toggle(status);
            super.setArrowPosition(pickupPosition);
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
