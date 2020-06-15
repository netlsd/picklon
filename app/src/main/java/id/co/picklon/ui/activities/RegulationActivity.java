package id.co.picklon.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.model.data.DataSource;
import id.co.picklon.model.entities.Article;
import id.co.picklon.model.rest.utils.ProgressSubscriber;
import id.co.picklon.model.rest.utils.RxUtil;
import id.co.picklon.model.rest.utils.SubscriberOnNextListener;
import rx.functions.Action1;

public class RegulationActivity extends ToolbarActivity {

    @BindView(R.id.regulation_text)
    TextView textView;

    @Inject
    DataSource dataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regulation);
        activityComponent().inject(this);

        initUi();
    }

    private void initUi() {
        String regulationText = getString(R.string.regulation);
        setToolbarTitle(regulationText.substring(1).toUpperCase());

        dataSource.getRegulation()
                .subscribe(new ProgressSubscriber<>(this, this::setContent));
    }

    private void setContent(List<Article> articleList) {
        textView.setText(articleList.get(0).getContent());
    }
}
