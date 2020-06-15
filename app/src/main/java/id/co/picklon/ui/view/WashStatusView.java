package id.co.picklon.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.picklon.R;

public class WashStatusView extends LinearLayout {
    public static final String PICKUP = "PICKUP";
    public static final String STORE_CONFIRM = "STORECONFIRM";
    public static final String USER_CONFIRM = "USERCONFIRM";
    public static final String HAVEORDER = "HAVEORDER";
    public static final String PAY_FINISH = "PAYFINISH";
    public static final String WASHING = "WASHING";
    public static final String DELIVER = "DELIVER";
    public static final String FINISHED = "FINISHED";

    @BindView(R.id.wash_status_progress)
    CircleProgressBar progressView;
    @BindView(R.id.wash_status_image)
    ImageView imageView;
    @BindView(R.id.wash_status_text)
    TextView textView;

    public WashStatusView(Context context) {
        this(context, null);
    }

    public WashStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        LayoutInflater.from(context).inflate(R.layout.view_wash_status, this);
        ButterKnife.bind(this);
    }

    public void setProgress(int progress) {
        progressView.setProgress(progress);
    }

    private void setStatusText(int textId) {
        textView.setText(textId);
    }

    private void setImageResource(int resource) {
        imageView.setImageResource(resource);
    }

    public void setStatus(String status) {
        switch (status) {
            case PICKUP:
            case HAVEORDER:
                setStatusText(R.string.pick_up);
                setImageResource(R.drawable.ic_pickup_grey);
                setProgress(20);
                break;
            case PAY_FINISH:
                setStatusText(R.string.pay_finish);
                setImageResource(R.drawable.ic_store_confirm_grey);
                setProgress(40);
                break;
            case STORE_CONFIRM:
                setStatusText(R.string.store_confirm);
                setImageResource(R.drawable.ic_store_confirm_grey);
                setProgress(40);
                break;
            case USER_CONFIRM:
                setStatusText(R.string.user_confirm);
                setImageResource(R.drawable.ic_store_confirm_grey);
                setProgress(40);
                break;
            case WASHING:
                setStatusText(R.string.washing);
                setImageResource(R.drawable.ic_progress_grey);
                setProgress(60);
                break;
            case DELIVER:
                setStatusText(R.string.deliver_qr);
                setImageResource(R.drawable.ic_delivered_grey);
                setProgress(80);
                break;
        }
    }
}
