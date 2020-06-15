package id.co.picklon.ui.adapter;

import android.content.Context;
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
import id.co.picklon.model.entities.Order;


public class WashStatusAdapter extends RecyclerView.Adapter<WashStatusAdapter.WashStatusViewHolder> {
    private Context mContext;
    private List<Order> orderList = Collections.emptyList();
    private boolean isNeedHide = false;

    public WashStatusAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public WashStatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new WashStatusViewHolder(layoutInflater.inflate(R.layout.item_wash_status, parent, false));
    }

    @Override
    public void onBindViewHolder(WashStatusViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.statusView.setText(mContext.getString(R.string.status_place_holder, order.getWashStatus()));
        holder.idView.setText(mContext.getString(R.string.order_id_place_holder, order.getOrderId()));
        if (isNeedHide) {
            holder.pickupView.setVisibility(View.GONE);
        } else {
            holder.pickupView.setVisibility(View.VISIBLE);
            holder.pickupView.setText(mContext.getString(R.string.pickup_place_holder, order.getPickupTime()));
        }
    }

    public void hidePickupView() {
        isNeedHide = true;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void addItems(List<Order> orders) {
        this.orderList = orders;
        notifyDataSetChanged();
    }

    class WashStatusViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.wash_status_status)
        TextView statusView;
        @BindView(R.id.wash_status_id)
        TextView idView;
        @BindView(R.id.wash_status_pickup)
        TextView pickupView;;

        WashStatusViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
