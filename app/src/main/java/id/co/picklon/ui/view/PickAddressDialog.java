package id.co.picklon.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.picklon.R;
import id.co.picklon.model.entities.Address;
import id.co.picklon.ui.activities.EditAddressActivity;
import id.co.picklon.ui.adapter.OrderAddressAdapter;

public class PickAddressDialog extends Dialog {
    private Context mContext;
    private OrderAddressAdapter addressAdapter;

    @BindView(R.id.pick_address_list)
    RecyclerView listView;
    @BindView(R.id.pick_address_add)
    TextView addView;


    public PickAddressDialog(Context context) {
        super(context);
        this.mContext = context;
        addressAdapter = new OrderAddressAdapter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_pick_address);
        setCanceledOnTouchOutside(false);

        ButterKnife.bind(this);
        initializeRecyclerView();

        addView.setOnClickListener(view -> mContext.startActivity(new Intent(mContext, EditAddressActivity.class)));
    }

    private void initializeRecyclerView() {
        listView.setLayoutManager(new LinearLayoutManager(mContext));
        listView.setAdapter(addressAdapter);
        listView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
    }

    public void setAddressList(List<Address> addressList) {
        addressAdapter.addItems(addressList);
    }

    public void setOnClickAddressListener(OrderAddressAdapter.OnClickAddressListener listener) {
        addressAdapter.setOnClickAddressListener(listener);
    }
}
