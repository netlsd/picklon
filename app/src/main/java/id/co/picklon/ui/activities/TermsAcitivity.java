package id.co.picklon.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.model.data.DataSource;
import id.co.picklon.model.entities.Article;
import id.co.picklon.model.rest.utils.ProgressSubscriber;
import id.co.picklon.utils.L;

public class TermsAcitivity extends ToolbarActivity {

    @BindView(R.id.terms_accept)
    Button termsAccept;
    @BindView(R.id.terms_text)
    TextView textView;

    @Inject
    DataSource dataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        activityComponent().inject(this);

        setToolbarTitle(R.string.terms_service);

        termsAccept.setOnClickListener(view -> {
            setResult(RESULT_OK, null);
            finish();
        });

        dataSource.getTerms().subscribe(new ProgressSubscriber<>(this, articles -> {
            if (!articles.isEmpty()) {
                textView.setText(articles.get(0).getContent());
            }
        }));
    }
}
