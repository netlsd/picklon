package id.co.picklon.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.model.data.DataSource;
import id.co.picklon.model.entities.Article;
import id.co.picklon.model.rest.utils.ProgressSubscriber;
import id.co.picklon.utils.Picklon;

public class AboutUsActivity extends ToolbarActivity {

    @Inject
    DataSource dataSource;
    @Inject
    Picasso picasso;

    @BindView(R.id.about_us_text)
    TextView textView;
    @BindView(R.id.about_us_image)
    ImageView imageView;
    @BindView(R.id.about_us_fb)
    FrameLayout fbView;
    @BindView(R.id.about_us_instagram)
    FrameLayout instagramView;
    @BindView(R.id.about_us_twitter)
    FrameLayout twitterView;
    @BindView(R.id.about_us_website)
    FrameLayout websiteView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        activityComponent().inject(this);

        initUi();

        dataSource.getAboutUs()
                .subscribe(new ProgressSubscriber<>(this, this::setContent));

//        Observable.zip(dataSource.getAboutUs(), dataSource.getFanpage());

        fbView.setOnClickListener(view -> openBrowser(R.string.url_facebook));
        instagramView.setOnClickListener(view -> openBrowser(R.string.url_instagram));
        twitterView.setOnClickListener(view -> openBrowser(R.string.url_twitter));
        websiteView.setOnClickListener(view -> openBrowser(R.string.url_website));
    }

    private void openBrowser(int urlId) {
        String url = getString(urlId);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    private void setContent(List<Article> articleList) {
        Article article = articleList.get(0);
        textView.setText(article.getContent());
        picasso.load(Picklon.MEDIAHOST + article.getImage()).into(imageView);
    }

    private void initUi() {
        String aboutUs = getString(R.string.about_us);
        setToolbarTitle(aboutUs.toUpperCase());
    }
}
