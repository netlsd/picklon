package id.co.picklon.ui.activities;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.Optional;
import id.co.picklon.R;

public class ToolbarActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView textView;
    @Nullable
    @BindView(R.id.toolbar_icon)
    ImageView imageView;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        setupToolBar();
    }

    private void setupToolBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(view -> finish());

        defaultTitle();
    }

    public void setToolbarTitle(String title) {
        textView.setText(title);
        defaultTitleSize();
    }

    public void setToolbarTitle(int titleRes) {
        textView.setText(titleRes);
        defaultTitleSize();
    }

    public void setToolbarIconListener(View.OnClickListener listener) {
        imageView.setVisibility(View.VISIBLE);
        imageView.setOnClickListener(listener);
    }

    private void defaultTitle() {
        setToolbarTitle(R.string.picklon_logo);
        setToolbarTitleSize(22);
    }

    private void defaultTitleSize() {
        setToolbarTitleSize(16);
    }

    public void setToolbarTitleSize(int size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
    }
}
