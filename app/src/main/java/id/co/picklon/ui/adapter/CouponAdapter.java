package id.co.picklon.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.picklon.R;
import id.co.picklon.model.entities.Coupon;
import id.co.picklon.model.rest.utils.SubscriberOnNextListener;
import id.co.picklon.utils.Tool;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponViewHolder>
        implements SubscriberOnNextListener<List<Coupon>> {
    private List<Coupon> couponList = Collections.emptyList();

    @Override
    public CouponViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new CouponViewHolder(layoutInflater.inflate(R.layout.item_coupon, parent, false));
    }

    @Override
    public void onBindViewHolder(CouponViewHolder holder, int position) {
        Coupon coupon = couponList.get(position);

        holder.nameView.setText(coupon.getName());
        holder.expireView.setText("expired data: ".concat(coupon.getExpireDate().split(" ")[0]));
        holder.descriptionView.setText(coupon.getDescription());
        holder.offView.setText(Tool.getFormatedValue(coupon.getValue()));
    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }

    @Override
    public void onNext(List<Coupon> coupons) {
        this.couponList = coupons;
        notifyDataSetChanged();
    }

    class CouponViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.coupon_name)
        TextView nameView;
        @BindView(R.id.coupon_expire)
        TextView expireView;
        @BindView(R.id.coupon_description)
        TextView descriptionView;
        @BindView(R.id.coupon_off)
        Button offView;


        public CouponViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
