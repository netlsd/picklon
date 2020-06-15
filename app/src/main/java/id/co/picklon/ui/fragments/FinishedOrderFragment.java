package id.co.picklon.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import id.co.picklon.ui.adapter.FinishedOrderAdapter;

public class FinishedOrderFragment extends CommonListFragment {
    private static final String TITLE = "Finished";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FinishedOrderAdapter adapter = new FinishedOrderAdapter(mContext, dataSource);
        adapter.setOnRefreshListener(this::startLoad);
        initializeRecyclerView(adapter);

        loadNetwork(dataSource.getFinishOrder(), adapter);
    }

    @Override
    public String getTitle() {
        return TITLE;
    }
}
