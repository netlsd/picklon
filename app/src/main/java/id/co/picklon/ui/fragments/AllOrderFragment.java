package id.co.picklon.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Toast;

import java.util.Map;

import id.co.picklon.R;
import id.co.picklon.ui.adapter.AllOrderAdapter;
import id.co.picklon.utils.Const;

public class AllOrderFragment extends CommonListFragment {
    private static final String TITLE = "All";
    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AllOrderAdapter adapter = new AllOrderAdapter(mContext);
        initializeRecyclerView(adapter);

        loadNetwork(dataSource.getOrderList(), adapter);
        initBroadcast();
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            startLoad();
        }
    }

    private void initBroadcast() {
        localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.ORDER_COMPLETED);
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }
}
