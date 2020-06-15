package id.co.picklon.ui.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.picklon.R;
import id.co.picklon.model.entities.Address;
import id.co.picklon.model.rest.utils.SubscriberOnNextListener;
import id.co.picklon.ui.activities.EditAddressActivity;

public class MyAddressAdapter extends RecyclerView.Adapter<MyAddressAdapter.AddressViewHolder>
        implements SubscriberOnNextListener<List<Address>> {
    private Context mContext;
    private boolean showCheckBox;
    private boolean selectAll;
    private List<Address> addresses = Collections.emptyList();

    public MyAddressAdapter(Context context) {
        mContext = context;
    }

    @Override
    public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new AddressViewHolder(layoutInflater.inflate(R.layout.item_my_address, parent, false));
    }

    @Override
    public void onBindViewHolder(AddressViewHolder holder, int position) {
        Address address = addresses.get(position);

        if (showCheckBox) {
            holder.cheboxView.setVisibility(View.VISIBLE);
            holder.defaultView.setVisibility(View.VISIBLE);
        } else {
            holder.cheboxView.setVisibility(View.GONE);
            holder.defaultView.setVisibility(View.GONE);
        }

        if (selectAll) {
            holder.cheboxView.setChecked(true);
            address.setSelected(true);
        } else {
            holder.cheboxView.setChecked(false);
            address.setSelected(false);
        }

        holder.cheboxView.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                address.setSelected(true);
            } else {
                address.setSelected(false);
            }
        });

        if (address.getDefaulted() == 0) {
            holder.defaultView.setVisibility(View.GONE);
        } else {
            holder.defaultView.setVisibility(View.VISIBLE);
        }

        holder.addressView.setText(address.getAddress());
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public void showCheckBox() {
        showCheckBox = true;
        notifyDataSetChanged();
    }

    public void hideCheckBox() {
        showCheckBox = false;
        notifyDataSetChanged();
    }

    public boolean isChecked() {
        return showCheckBox;
    }

    public void selectAll() {
        selectAll = true;
        notifyDataSetChanged();
    }

    public void disSelectAll() {
        selectAll = false;
        notifyDataSetChanged();
    }

    @Override
    public void onNext(List<Address> addresses) {
        this.addresses = addresses;
        notifyDataSetChanged();
    }

    public List<String> getSelectedAddressIdList() {
        List<String> addressList = new ArrayList<>();
        for (Address address : addresses) {
            if (address.isSelected()) {
                addressList.add(address.getId());
            }
        }
        return addressList;
    }

    class AddressViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_address_checkbox)
        AppCompatCheckBox cheboxView;
        @BindView(R.id.item_address_address)
        TextView addressView;
        @BindView(R.id.item_address_default)
        ImageView defaultView;
        @BindView(R.id.item_address_edit)
        ImageView editView;

        AddressViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(view -> EditAddressActivity.start(mContext, addresses.get(getAdapterPosition())));
        }
    }
}
