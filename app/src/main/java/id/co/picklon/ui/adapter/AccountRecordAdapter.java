package id.co.picklon.ui.adapter;

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
import id.co.picklon.model.rest.utils.SubscriberOnNextListener;
import id.co.picklon.utils.Tool;

public class AccountRecordAdapter extends RecyclerView.Adapter<AccountRecordAdapter.RecordViewHolder>
        implements SubscriberOnNextListener<List<Order>> {
    private List<Order> orderList = Collections.emptyList();

    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new RecordViewHolder(layoutInflater.inflate(R.layout.item_account_record, parent, false));
    }

    @Override
    public void onBindViewHolder(RecordViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.orderIdView.setText(order.getOrderId());
        holder.timeView.setText(order.getDeliverTime().split(" ")[0]);
        holder.hourView.setText(order.getDeliverTime().split(" ")[1]);
        holder.priceView.setText(Tool.getFormatedValue(Long.valueOf(order.getPrice())));
        holder.typeView.setText(order.getPayType());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    @Override
    public void onNext(List<Order> orders) {
        Collections.reverse(orders);
        this.orderList = orders;
        notifyDataSetChanged();
    }

    class RecordViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.record_order_id)
        TextView orderIdView;
        @BindView(R.id.record_time)
        TextView timeView;
        @BindView(R.id.record_hour)
        TextView hourView;
        @BindView(R.id.record_price)
        TextView priceView;
        @BindView(R.id.record_type)
        TextView typeView;


        public RecordViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
