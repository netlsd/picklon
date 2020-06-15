package id.co.picklon.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.picklon.R;
import id.co.picklon.model.data.DataSource;
import id.co.picklon.model.entities.Order;
import id.co.picklon.model.rest.utils.ProgressSubscriber;
import id.co.picklon.ui.view.CommentsDialog;
import id.co.picklon.utils.TimeUtil;
import io.techery.properratingbar.ProperRatingBar;
import rx.functions.Action1;

public class FinishedOrderAdapter extends RecyclerView.Adapter<FinishedOrderAdapter.FinishedOrderViewHolder>
        implements Action1<List<Order>>, CommentsDialog.OnSubmitListener {
    private Context mContext;
    private DataSource dataSource;
    private List<Order> orderList = Collections.emptyList();
    private OnRefreshListener listener;

    public FinishedOrderAdapter(Context context, DataSource dataSource) {
        this.mContext = context;
        this.dataSource = dataSource;
    }

    @Override
    public FinishedOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new FinishedOrderViewHolder(layoutInflater.inflate(R.layout.item_finished_order, parent, false));
    }

    @Override
    public void onBindViewHolder(FinishedOrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.idView.setText(order.getOrderId());
        holder.pickDateView.setText(TimeUtil.convertServerTime(order.getPickupTime().split(" ")[0]));
        holder.pickTimeView.setText(order.getPickupTime().split(" ")[1]);
        holder.deliverDateView.setText(TimeUtil.convertServerTime(order.getDeliverTime().split(" ")[0]));
        holder.deliverTimeView.setText(order.getDeliverTime().split(" ")[1]);
        holder.priceView.setText(String.format(Locale.ENGLISH, "%,d", Long.valueOf(order.getPrice())));

        if (order.getStar() == -1) {
            holder.ratingBar.setVisibility(View.GONE);
            holder.commentsView.setVisibility(View.VISIBLE);
        } else {
            holder.ratingBar.setRating(order.getStar());
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.commentsView.setVisibility(View.GONE);
        }
    }

    @Override
    public void call(List<Order> orders) {
        Collections.reverse(orders);
        orderList = orders;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class FinishedOrderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.finished_order_id)
        TextView idView;
        @BindView(R.id.finished_order_pick_date)
        TextView pickDateView;
        @BindView(R.id.finished_order_pick_time)
        TextView pickTimeView;
        @BindView(R.id.finished_order_deliver_date)
        TextView deliverDateView;
        @BindView(R.id.finished_order_deliver_time)
        TextView deliverTimeView;
        @BindView(R.id.finished_order_price)
        TextView priceView;
        @BindView(R.id.finished_order_ratingbar)
        ProperRatingBar ratingBar;
        @BindView(R.id.finished_order_comments)
        Button commentsView;

        public FinishedOrderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            commentsView.setOnClickListener(view -> new CommentsDialog(mContext,
                    orderList.get(getAdapterPosition()).getOrderId(), FinishedOrderAdapter.this).show());
        }
    }

    @Override
    public void onSubmit(String id, String comment, int star) {
        dataSource.commentOrder(id, comment, star)
                .subscribe(new ProgressSubscriber<>(mContext, o -> listener.onRefresh()));
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        this.listener = listener;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }
}