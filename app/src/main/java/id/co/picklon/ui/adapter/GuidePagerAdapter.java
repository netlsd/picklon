package id.co.picklon.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.picklon.R;

public class GuidePagerAdapter extends PagerAdapter {
    private static final int numberOfPages = 3;
    private Context mContext;

    @BindView(R.id.guide_image)
    ImageView imageView;
    @BindView(R.id.guide_title)
    TextView titleView;
    @BindView(R.id.guide_text)
    TextView textView;

    public GuidePagerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.page_guide, collection, false);
        ButterKnife.bind(this, layout);

        setViewByPostion(position);
        collection.addView(layout);
        return layout;
    }

    private void setViewByPostion(int position) {
        switch (position) {
            case 0:
                imageView.setImageResource(R.drawable.guide_pickup);
                titleView.setText(R.string.intro_1_title);
                textView.setText(R.string.intro_1);
                break;
            case 1:
                imageView.setImageResource(R.drawable.guide_dressup);
                titleView.setText(R.string.intro_2_title);
                textView.setText(R.string.intro_2);
                break;
            default:
                imageView.setImageResource(R.drawable.guide_delivery);
                titleView.setText(R.string.intro_3_title);
                textView.setText(R.string.intro_2);
                break;
        }
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return numberOfPages;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}