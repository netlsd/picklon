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
import id.co.picklon.ui.activities.OrderStatusActivity;
import id.co.picklon.ui.view.WashStatusView;
import id.co.picklon.utils.TimeUtil;
import rx.functions.Action1;

public class AllOrderAdapter extends RecyclerView.Adapter<AllOrderAdapter.AllOrderViewHolder>
        implements Action1<List<Order>> {
    private Context mContext;
    private List<Order> orderList = Collections.emptyList();

    public AllOrderAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public AllOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new AllOrderViewHolder(layoutInflater.inflate(R.layout.item_all_order, parent, false));
    }

    @Override
    public void onBindViewHolder(AllOrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.idView.setText(getString(R.string.prefix_order_id, order.getOrderId()));
        holder.dateView.setText(getString(R.string.prefix_pick_date,
                TimeUtil.convertServerTime(order.getPickupTime().split(" ")[0])));
        holder.timeView.setText(getString(R.string.prefix_pick_time, order.getPickupTime().split(" ")[1]));
        holder.priceView.setText(order.getPrice());
        holder.washStatusView.setStatus(order.getWashStatus());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    @Override
    public void call(List<Order> orders) {
        Collections.reverse(orders);
        orderList = orders;
        notifyDataSetChanged();
    }

    private String getString(int id, String arg) {
        return mContext.getString(id, arg);
    }

    class AllOrderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.all_order_order_id)
        TextView idView;
        @BindView(R.id.all_order_pickup_date)
        TextView dateView;
        @BindView(R.id.all_order_pickup_time)
        TextView timeView;
        @BindView(R.id.all_order_pickup_price)
        TextView priceView;
        @BindView(R.id.all_order_wash_status)
        WashStatusView washStatusView;

        AllOrderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(view -> OrderStatusActivity.start(mContext, orderList.get(getAdapterPosition())));
        }
    }
}
