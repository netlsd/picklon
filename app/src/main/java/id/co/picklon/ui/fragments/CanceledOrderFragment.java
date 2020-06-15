package id.co.picklon.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import id.co.picklon.ui.adapter.CanceledOrderAdapter;

public class CanceledOrderFragment extends CommonListFragment {
    private static final String TITLE = "Canceled";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CanceledOrderAdapter adapter = new CanceledOrderAdapter(mContext);
        initializeRecyclerView(adapter);

        loadNetwork(dataSource.getCanceledOrder(), adapter);
    }

    @Override
    public String getTitle() {
        return TITLE;
    }
}
