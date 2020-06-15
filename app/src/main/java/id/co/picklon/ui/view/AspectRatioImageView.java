package id.co.picklon.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class AspectRatioImageView extends ImageView {

    protected Context mContext;

    public AspectRatioImageView(Context context) {
        super(context);
        mContext = context;
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getDrawable() == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        // 计算出ImageView的宽度
        int viewWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        // 根据图片长宽比例算出ImageView的高度
        int viewHeight = viewWidth * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
        // 将计算出的宽度和高度设置为图片显示的大小
        setMeasuredDimension(viewWidth, viewHeight);
    }
}
