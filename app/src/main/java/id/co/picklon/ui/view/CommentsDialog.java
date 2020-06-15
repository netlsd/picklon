package id.co.picklon.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.picklon.R;
import io.techery.properratingbar.ProperRatingBar;

public class CommentsDialog extends Dialog {

    @BindView(R.id.dialog_comments_rating_bar)
    ProperRatingBar ratingBar;
    @BindView(R.id.dialog_comments_cancel)
    Button cancelView;
    @BindView(R.id.dialog_comments_submit)
    Button submitView;
    @BindView(R.id.dialog_comments_edit)
    EditText editView;

    private OnSubmitListener listener;
    private String orderId;

    public CommentsDialog(Context context, String id, OnSubmitListener listener) {
        super(context);
        this.listener = listener;
        orderId = id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_coments);
        ButterKnife.bind(this);

        setCanceledOnTouchOutside(false);

        cancelView.setOnClickListener(view -> cancel());
        submitView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onSubmit(orderId, editView.getText().toString().trim(), ratingBar.getRating());
                dismiss();
            }
        });
    }

    public interface OnSubmitListener {
        void onSubmit(String id, String comment, int star);
    }
}
