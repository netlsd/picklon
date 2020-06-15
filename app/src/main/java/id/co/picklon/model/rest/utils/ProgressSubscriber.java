package id.co.picklon.model.rest.utils;

import android.app.Dialog;
import android.content.Context;

import id.co.picklon.R;
import id.co.picklon.ui.view.LoadingDialog;
import id.co.picklon.utils.ViewUtils;
import rx.Subscriber;

public class ProgressSubscriber<T> extends Subscriber<T> {
    private SubscriberOnNextListener<T> mSubscriberOnNextListener;
    private Dialog loadDialog;
    private Context context;

    public ProgressSubscriber(Context context, SubscriberOnNextListener<T> mSubscriberOnNextListener) {
        this(context, mSubscriberOnNextListener, true);
    }

    public ProgressSubscriber(Context context, SubscriberOnNextListener<T> mSubscriberOnNextListener, boolean showProgress) {
        this.context = context;
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;

        if (showProgress) {
            loadDialog = new LoadingDialog(context);
            loadDialog.setOnCancelListener(dialogInterface -> dismissProgressDialog());
        }
    }

    private void showProgressDialog() {
        if (loadDialog != null && !loadDialog.isShowing()) {
            loadDialog.show();
        }
    }

    private void dismissProgressDialog() {
        if (loadDialog != null && loadDialog.isShowing()) {
            loadDialog.cancel();
            loadDialog = null;
        }

        if (!isUnsubscribed()) {
            unsubscribe();
        }
    }

    @Override
    public void onStart() {
        showProgressDialog();
    }

    @Override
    public void onCompleted() {
        dismissProgressDialog();
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof ErrorResponseException) {
            ViewUtils.showDialog(context, e.getMessage());
        } else {
            ViewUtils.showDialog(context, R.string.network_error);
            e.printStackTrace();
        }

        dismissProgressDialog();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onNext(T t) {
        mSubscriberOnNextListener.onNext(t);
    }
}