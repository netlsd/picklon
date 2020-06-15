package id.co.picklon.ui.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.picklon.R;
import id.co.picklon.ui.activities.MyWalletActivity;
import id.co.picklon.ui.activities.NewOrderActivity;
import id.co.picklon.ui.activities.StatusActivity;

public class BottomNavigationView extends LinearLayout {

    @BindView(R.id.bottom_new_order)
    LinearLayout newOrderView;
    @BindView(R.id.bottom_status)
    LinearLayout statusView;
    @BindView(R.id.bottom_my_wallet)
    LinearLayout myWalletView;

    private int selectedViewId;

    public BottomNavigationView(Context context) {
        this(context, null);
    }

    public BottomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.view_bottom_navigation, this);
        ButterKnife.bind(this);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BottomNavigationView);
        selectedViewId = ta.getResourceId(R.styleable.BottomNavigationView_selectedView, -1);
        ta.recycle();

        newOrderView.setOnClickListener(view -> context.startActivity(new Intent(context, NewOrderActivity.class)));
        statusView.setOnClickListener(view -> context.startActivity(new Intent(context, StatusActivity.class)));
        myWalletView.setOnClickListener(view -> context.startActivity(new Intent(context, MyWalletActivity.class)));

        setSelectView(context);
    }

    private void setSelectView(Context context) {
        switch (selectedViewId) {
            case R.id.bottom_new_order:
                newOrderView.setClickable(false);
                newOrderView.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_rect_transparent));
                break;
            case R.id.bottom_status:
                statusView.setClickable(false);
                statusView.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_rect_transparent));
                break;
            case R.id.bottom_my_wallet:
                myWalletView.setClickable(false);
                myWalletView.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_rect_transparent));
                break;
        }
    }

}
