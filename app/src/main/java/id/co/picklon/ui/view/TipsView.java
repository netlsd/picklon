package id.co.picklon.ui.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.picklon.R;
import id.co.picklon.utils.ViewUtils;

public class TipsView extends LinearLayout {
    private Context mContext;
    private int tips[] = {2, 5, 10, 15, 20};
    private boolean switchFlag;

    @BindView(R.id.tips_group)
    RadioGroup tipsGroup;
    @BindView(R.id.tips_text_layout)
    FrameLayout textLayout;

    private OnTipsChangeListener listener;

    public TipsView(Context context) {
        this(context, null);
    }

    public TipsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.view_tips, this);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tips1, R.id.tips2, R.id.tips3, R.id.tips4, R.id.tips5})
    void handleClick(View view) {
        if (view.getId() == R.id.tips1) {
            if (switchFlag) {
                switchFlag = false;
                clearAll();
                tipsGroup.clearCheck();
                listener.onChange();
                return;
            } else {
                switchFlag = true;
                view.setBackground(ContextCompat.getDrawable(mContext, R.drawable.round_red));
                setTextColor();
                listener.onChange();
                return;
            }
        }

        clearAll();
        // 点击其他tips默认把第一个选中
        switchFlag = true;

        for (int i = 0; i < tipsGroup.getChildCount(); i++) {
            View childView = tipsGroup.getChildAt(i);

            childView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.round_red));

            if (childView == view) {
                break;
            }
        }

        listener.onChange();

        setTextColor();
    }

    private void clearAll() {
        for (int i = 0; i < tipsGroup.getChildCount(); i++) {
            View childView = tipsGroup.getChildAt(i);
            childView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.round_grey));
        }

        for (int j = 0; j < textLayout.getChildCount(); j++) {
            TextView textView = (TextView) textLayout.getChildAt(j);
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.grey));
        }
    }

    private void setTextColor() {
        for (int i = 0; i < getTipsPosition(); i++) {
            TextView textView = (TextView) textLayout.getChildAt(i);
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        }
    }

    public int getTipsPosition() {
        int position = 0;

        if (tipsGroup.getCheckedRadioButtonId() < 0) {
            return 0;
        }

        for (int i = 0; i < tipsGroup.getChildCount(); i++) {
            View childView = tipsGroup.getChildAt(i);
            if (childView instanceof RadioButton) {
                position++;

                RadioButton radioButton = (RadioButton) childView;
                if (radioButton.isChecked()) {
                    break;
                }
            }
        }

        return position;
    }

    public int getTips() {
        int position = getTipsPosition();

        if (position == 0) {
            return 0;
        }
        return tips[position - 1] * 1000;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            textLayout.removeAllViews();

            int j = 0;
            for (int i = 0; i < tipsGroup.getChildCount(); i++) {
                View childView = tipsGroup.getChildAt(i);
                if (childView instanceof RadioButton) {
                    TextView textView = new TextView(mContext);
                    textView.setText(String.valueOf(tips[j]).concat("K"));
                    textView.setX(childView.getX() + ViewUtils.dpToPix(mContext, 6));
                    textLayout.addView(textView);
                    j++;
                }
            }

            setTextColor();
        }
    }

    public void setOnTipsChangeListener(OnTipsChangeListener listener) {
        this.listener = listener;
    }

    public interface OnTipsChangeListener {
        void onChange();
    }
}
