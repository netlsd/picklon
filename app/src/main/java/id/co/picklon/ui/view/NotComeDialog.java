package id.co.picklon.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.picklon.R;
import id.co.picklon.model.entities.Order;
import id.co.picklon.utils.L;
import id.co.picklon.utils.Tool;

public class NotComeDialog extends Dialog {
    @BindView(R.id.not_come_pilot_name)
    TextView pilotNameView;
    @BindView(R.id.not_come_store_name)
    TextView storeNameView;
    @BindView(R.id.not_come_cancel)
    Button cancelView;
    @BindView(R.id.not_come_store_phone)
    ImageView storePhoneView;
    @BindView(R.id.not_come_pilot_phone)
    ImageView pilotPhoneView;

    private Order order;
    private Context mContext;

    public NotComeDialog(Context context, Order order) {
        super(context);
        this.order = order;
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_not_come);
        setCanceledOnTouchOutside(false);
        ButterKnife.bind(this);

        pilotNameView.setText(order.getPickupGuyName());
        pilotPhoneView.setOnClickListener(view -> Tool.callPhone(mContext, order.getPickupGuyMobile()));
        storeNameView.setText(order.getShopName());
        storePhoneView.setOnClickListener(view -> Tool.callPhone(mContext, order.getShopPhone()));
        cancelView.setOnClickListener(view -> dismiss());
    }
}
