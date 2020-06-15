package id.co.picklon.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.picklon.R;
import id.co.picklon.utils.ViewUtils;

public class MenuView extends RelativeLayout {

    @BindView(R.id.menu_image)
    ImageView imageView;
    @BindView(R.id.menu_text)
    TextView textView;
    @BindView(R.id.menu_right_image)
    ImageView rightImageView;

    private String menuText;
    private int imageId;
    private int rightImageId;

    public MenuView(Context context) {
        this(context, null);
    }

    public MenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_menu, this);
        ButterKnife.bind(this);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MenuView);
        menuText = ta.getString(R.styleable.MenuView_menuText);
        imageId = ta.getResourceId(R.styleable.MenuView_menuImage, -1);
        rightImageId = ta.getResourceId(R.styleable.MenuView_menuRightImage, -1);
        ta.recycle();

        imageView.setImageResource(imageId);
        textView.setText(menuText);
        if (rightImageId != -1) {
            rightImageView.setVisibility(VISIBLE);
            rightImageView.setImageResource(rightImageId);
        }
    }

    public void hideRightIcon() {
        rightImageView.setVisibility(GONE);
    }

    public void showRightIcon() {
        rightImageView.setVisibility(VISIBLE);
    }

}
