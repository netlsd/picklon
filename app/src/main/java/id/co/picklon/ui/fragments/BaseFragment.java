package id.co.picklon.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import butterknife.ButterKnife;
import id.co.picklon.PicklonApplication;
import id.co.picklon.injector.components.ActivityComponent;
import id.co.picklon.injector.components.DaggerActivityComponent;
import id.co.picklon.injector.modules.ActivityModule;

public class BaseFragment extends Fragment {
    public Context mContext;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public ActivityComponent fragmentComponent() {
        Activity activity = (Activity) mContext;
        PicklonApplication picklonApplication = (PicklonApplication) activity.getApplication();

        return DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(mContext))
                .appComponent(picklonApplication.getAppComponent())
                .build();
    }
}
