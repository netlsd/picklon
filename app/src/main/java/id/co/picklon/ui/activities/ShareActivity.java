package id.co.picklon.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.utils.ShareUtils;


public class ShareActivity extends ToolbarActivity {
    @BindView(R.id.share_fb)
    FrameLayout shareFbView;
    @BindView(R.id.share_sms)
    FrameLayout shareSmsView;
    @BindView(R.id.share_instagram)
    FrameLayout shareInstagramView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        initUi();
    }

    private void initUi() {
        setToolbarTitle(R.string.share_to_friends);

        shareFbView.setOnClickListener(view -> ShareUtils.shareToFacebook(this));
        shareInstagramView.setOnClickListener(view -> ShareUtils.shareToInstagram(this));
        shareSmsView.setOnClickListener(view -> ShareUtils.shareToSms(this));
    }
}
