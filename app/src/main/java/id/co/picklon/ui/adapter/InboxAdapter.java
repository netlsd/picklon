package id.co.picklon.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.picklon.R;
import id.co.picklon.model.entities.UserInbox;
import id.co.picklon.ui.view.AspectRatioImageView;
import id.co.picklon.utils.Picklon;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.InboxViewHolder> {
    private Picasso picasso;
    private Context mContext;
    private List<UserInbox> userInboxList = Collections.emptyList();
    private ClickListener clickListener;

    public InboxAdapter(Context context, Picasso picasso) {
        this.mContext = context;
        this.picasso = picasso;
    }

    @Override
    public InboxViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new InboxViewHolder(layoutInflater.inflate(R.layout.item_inbox, parent, false));
    }

    @Override
    public void onBindViewHolder(InboxViewHolder holder, int position) {
        UserInbox userInbox = userInboxList.get(position);
        picasso.load(Picklon.MEDIAHOST + userInbox.getImage()).into(holder.imageView);
        holder.titleView.setText(userInbox.getInboxTitle());

        if (userInbox.getIsCoupon() == 1) {
            holder.contentView.setText(userInbox.getCouponInfo());
        } else {
            holder.contentView.setText(userInbox.getInboxContent());
        }

        if (userInbox.getReaded() == 1) {
            holder.notificationView.setVisibility(View.INVISIBLE);
        } else {
            holder.notificationView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return userInboxList.size();
    }

    public void addItems(List<UserInbox> inboxes) {
        this.userInboxList = inboxes;
        notifyDataSetChanged();
    }

    class InboxViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_inbox_image)
        AspectRatioImageView imageView;
        @BindView(R.id.item_inbox_title)
        TextView titleView;
        @BindView(R.id.item_inbox_content)
        TextView contentView;
        @BindView(R.id.item_inbox_notification)
        ImageView notificationView;

        InboxViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                clickListener.onItemClick(pos, view);
                Toast.makeText(mContext, R.string.mark_read, Toast.LENGTH_SHORT).show();
                userInboxList.get(pos).setReaded(1);
                notifyDataSetChanged();
            });
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
