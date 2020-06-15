package id.co.picklon.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.model.data.AccountManager;
import id.co.picklon.model.data.DataSource;
import id.co.picklon.model.rest.utils.ProgressSubscriber;
import id.co.picklon.ui.adapter.AccountRecordAdapter;
import id.co.picklon.ui.view.DividerItemDecoration;

public class AccountRecordActivity extends ToolbarActivity {
    @BindView(R.id.record_account)
    TextView accountView;
    @BindView(R.id.record_list)
    RecyclerView listView;

    @Inject
    AccountManager accountManager;
    @Inject
    DataSource dataSource;

    private AccountRecordAdapter recordAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_record);
        activityComponent().inject(this);

        initUi();
        initializeRecyclerView();
        dataSource.getFinishOrder().subscribe(new ProgressSubscriber<>(this, recordAdapter));
    }

    private void initUi() {
        String title = getString(R.string.account_record);
        setToolbarTitle(title.toUpperCase());
        accountView.setText(getString(R.string.prefix_my_account, accountManager.getMobile()));
    }

    private void initializeRecyclerView() {
        recordAdapter = new AccountRecordAdapter();
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(recordAdapter);
        listView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }
}
