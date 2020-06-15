package id.co.picklon.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import javax.inject.Inject;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.model.data.DataSource;
import id.co.picklon.model.rest.utils.ProgressSubscriber;
import id.co.picklon.ui.adapter.MyAddressAdapter;
import id.co.picklon.utils.Const;

public class MyAddressActivity extends ToolbarActivity {

    @BindView(R.id.my_address_list)
    RecyclerView listView;
    @BindView(R.id.my_address_add_new)
    Button addNewView;
    @BindView(R.id.my_address_edit)
    Button editView;
    @BindView(R.id.my_address_delete_selected)
    Button deleteSelectedView;
    @BindView(R.id.my_address_select_all)
    Button selectAllView;
    @BindView(R.id.my_address_select_layout)
    LinearLayout selectLayoutView;

    @Inject
    DataSource dataSource;

    private MyAddressAdapter addressAdapter;
    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);
        activityComponent().inject(this);

        initUi();
        initializeRecyclerView();
        initBroadcast();
        loadAddress();

        deleteSelectedView.setOnClickListener(view -> deleteSelectedAddress());
    }

    private void initBroadcast() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.UPDATE_ADDRESS);
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }

    private void deleteSelectedAddress() {
        String ids = TextUtils.join(",", addressAdapter.getSelectedAddressIdList());

        dataSource.delAddress(ids).subscribe(new ProgressSubscriber<>(this, o -> {
            hideSelectLayout();
            loadAddress();
        }));
    }

    private void loadAddress() {
        dataSource.getAddress().subscribe(new ProgressSubscriber<>(this, addressAdapter));
    }

    private void initUi() {
        editView.setOnClickListener(view -> {
            if (addNewView.isShown()) {
                editView.setText(R.string.cancel);
                addressAdapter.showCheckBox();
                selectLayoutView.setVisibility(View.VISIBLE);
                addNewView.setVisibility(View.GONE);
            } else {
                hideSelectLayout();
            }
        });

        selectAllView.setOnClickListener(view -> addressAdapter.selectAll());

        addNewView.setOnClickListener(view -> startActivity(new Intent(this, EditAddressActivity.class)));
    }

    @Override
    public void onBackPressed() {
        if (addressAdapter.isChecked()) {
            hideSelectLayout();
        } else {
            super.onBackPressed();
        }
    }

    private void hideSelectLayout() {
        addressAdapter.hideCheckBox();
        addressAdapter.disSelectAll();
        selectLayoutView.setVisibility(View.GONE);
        addNewView.setVisibility(View.VISIBLE);
        editView.setText(R.string.edit);
    }

    private void initializeRecyclerView() {
        addressAdapter = new MyAddressAdapter(this);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(addressAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadAddress();
        }
    }
}
