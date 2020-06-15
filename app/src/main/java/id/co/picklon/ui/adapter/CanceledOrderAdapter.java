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
import id.co.picklon.utils.TimeUtil;
import rx.functions.Action1;

public class CanceledOrderAdapter extends RecyclerView.Adapter<CanceledOrderAdapter.CanceledOrderViewHolder>
        implements Action1<List<Order>> {
    private Context mContext;
    private List<Order> orderList = Collections.emptyList();

    public CanceledOrderAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public CanceledOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new CanceledOrderViewHolder(layoutInflater.inflate(R.layout.item_canceled_order, parent, false));
    }

    @Override
    public void onBindViewHolder(CanceledOrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.idView.setText(mContext.getString(R.string.prefix_order_id, order.getOrderId()));
        holder.dateView.setText(mContext.getString(R.string.prefix_pick_date, TimeUtil.convertServerTime(order.getPickupTime().split(" ")[0])));
        holder.timeView.setText(mContext.getString(R.string.prefix_pick_time, order.getPickupTime().split(" ")[1]));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    @Override
    public void call(List<Order> orders) {
        Collections.reverse(orders);
        this.orderList = orders;
        notifyDataSetChanged();
    }

    class CanceledOrderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.canceled_order_id)
        TextView idView;
        @BindView(R.id.canceled_order_pickup_date)
        TextView dateView;
        @BindView(R.id.canceled_order_pickup_time)
        TextView timeView;

        public CanceledOrderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
