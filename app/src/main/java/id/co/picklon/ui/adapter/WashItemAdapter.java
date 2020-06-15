package id.co.picklon.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.picklon.R;
import id.co.picklon.model.entities.WashItem;
import id.co.picklon.utils.Picklon;

public class WashItemAdapter extends RecyclerView.Adapter<WashItemAdapter.WashItemViewHolder> {
    private Context mContext;
    private Picasso picasso;
    private String[] washItems = new String[0];

    public WashItemAdapter(Context context, Picasso picasso) {
        this.mContext = context;
        this.picasso = picasso;
    }

    @Override
    public WashItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new WashItemViewHolder(layoutInflater.inflate(R.layout.item_wash, parent, false));
    }

    @Override
    public void onBindViewHolder(WashItemViewHolder holder, int position) {
        String item = washItems[position];
        String[] washItems = item.split(",");

        for (WashItem washItem : Picklon.itemList) {
            if (washItem.getId().equals(washItems[0])) {
                picasso.load(Picklon.MEDIAHOST + washItem.getIcon()).into(holder.icon);
                holder.name.setText(washItem.getItemName());
                holder.quantity.setText(washItems[washItems.length - 1]);
                return;
            }
        }

        if (washItems[0].equalsIgnoreCase("carpet")) {
            holder.icon.setImageResource(R.drawable.ic_items_list_carpet);
            holder.name.setText(mContext.getString(R.string.carpet));
            holder.quantity.setText(washItems[washItems.length - 1]);
        }
    }

    @Override
    public int getItemCount() {
        return washItems.length;
    }

    public void addItem(String[] washItems) {
        this.washItems = washItems;
        notifyDataSetChanged();
    }

    class WashItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_wash_icon)
        ImageView icon;
        @BindView(R.id.item_wash_name)
        TextView name;
        @BindView(R.id.item_wash_quantity)
        TextView quantity;

        WashItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
