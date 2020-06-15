package id.co.picklon.ui.adapter;

import android.app.Dialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.picklon.R;
import id.co.picklon.model.entities.Address;

public class OrderAddressAdapter extends RecyclerView.Adapter<OrderAddressAdapter.OrderAddressViewHolder> {
    private List<Address> addressList = Collections.emptyList();
    private OnClickAddressListener listener;
    private Dialog dialog;

    public OrderAddressAdapter(Dialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public OrderAddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new OrderAddressViewHolder(layoutInflater.inflate(R.layout.item_order_address, parent, false));
    }

    @Override
    public void onBindViewHolder(OrderAddressViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.addressView.setText(address.getAddress());
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public void addItems(List<Address> addressList) {
        this.addressList = addressList;
        notifyDataSetChanged();
    }

    class OrderAddressViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.order_address)
        TextView addressView;

        public OrderAddressViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            addressView.setOnClickListener(view -> {
                dialog.dismiss();
                listener.onClick(addressList.get(getAdapterPosition()));
            });
        }
    }

    public void setOnClickAddressListener(OnClickAddressListener listener) {
        this.listener = listener;
    }

    public interface OnClickAddressListener {
        void onClick(Address address);
    }
}
