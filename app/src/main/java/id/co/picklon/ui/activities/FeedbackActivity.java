package id.co.picklon.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.picklon.R;
import id.co.picklon.model.data.DataSource;
import id.co.picklon.model.rest.utils.ProgressSubscriber;

public class FeedbackActivity extends ToolbarActivity {
    private static final int CHAR_LIMIT = 140;

    @BindView(R.id.feedback_edit)
    AppCompatEditText editView;
    @BindView(R.id.feedback_char_left)
    TextView charLeftView;
    @BindView(R.id.feedback_submit)
    Button submitView;

    @Inject
    DataSource dataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        activityComponent().inject(this);
        ButterKnife.bind(this);

        initUi();
    }

    private void initUi() {
        String feedback = getString(R.string.feedback);
        setToolbarTitle(feedback.toUpperCase());

        showCharLeft(CHAR_LIMIT);
        editView.addTextChangedListener(new FeedBackTextWatcher());
        submitView.setOnClickListener(view -> dataSource.feedBack(editView.getText().toString())
                .subscribe(new ProgressSubscriber<>(this, o -> {
                    Toast.makeText(this, R.string.submit_success, Toast.LENGTH_SHORT).show();
                    finish();
                })));
    }

    private void showCharLeft(int left) {
        charLeftView.setText(getString(R.string.feedback_char_left, left));
    }

    class FeedBackTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            showCharLeft(CHAR_LIMIT - editable.length());
        }
    }
}
