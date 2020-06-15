package id.co.picklon.ui.adapter;

import android.content.Context;
import android.content.pm.FeatureGroupInfo;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.co.picklon.R;

import static id.co.picklon.utils.Const.WASH_TYPE;

public class OrderSpinnerAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mList = new ArrayList<>();

    public OrderSpinnerAdapter(Context context) {
        mContext = context;

        for (int resId : WASH_TYPE) {
            mList.add(context.getString(resId));
        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_spinner_service, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.spinner_service_image);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.spinner_service_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(mList.get(position));

        if (position == 0) {
            viewHolder.imageView.setImageResource(R.drawable.ic_wash_fold);
        } else if (position == 1) {
            viewHolder.imageView.setImageResource(R.drawable.ic_wash_iron);
        } else if (position == 2) {
            viewHolder.imageView.setImageResource(R.drawable.ic_iron_only);
        }

        if (position == getCount()) {
            viewHolder.textView.setText("");
            viewHolder.imageView.setVisibility(View.GONE);
            viewHolder.textView.setHint(getItem(getCount()));
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return mList.size() - 1;
    }

    @Override
    public String getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private class ViewHolder {
        public TextView textView;
        public ImageView imageView;
    }
}
