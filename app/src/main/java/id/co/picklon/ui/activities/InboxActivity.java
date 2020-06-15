package id.co.picklon.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.model.data.DataSource;
import id.co.picklon.model.entities.Inbox;
import id.co.picklon.model.entities.UserInbox;
import id.co.picklon.ui.adapter.InboxAdapter;
import id.co.picklon.utils.L;
import id.co.picklon.utils.Picklon;

public class InboxActivity extends ToolbarActivity {
    @BindView(R.id.inbox_list)
    RecyclerView listView;

    @Inject
    Picasso picasso;
    @Inject
    DataSource dataSource;

    private List<UserInbox> inboxList;
    private InboxAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        activityComponent().inject(this);

        initUi();
        initializeRecyclerView();
        processData();
    }

    private void processData() {
        inboxList = new ArrayList<>();
        inboxList.addAll(Picklon.userInboxList);

        for (Inbox inbox : Picklon.inboxList) {
            UserInbox ui = new UserInbox();
            ui.setImage(inbox.getImage());
            ui.setInboxTitle(inbox.getInboxTitle());
            ui.setInboxId(inbox.getId());
            ui.setInboxContent(inbox.getInboxContent());
            inboxList.add(ui);
        }

        Collections.sort(inboxList, (o1, o2) -> o1.getReaded() - o2.getReaded());
        adapter.addItems(inboxList);
    }

    private void initUi() {
        setToolbarTitle("INBOX");
    }

    private void initializeRecyclerView() {
        adapter = new InboxAdapter(this, picasso);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnItemClickListener((position, v) -> {
            UserInbox userInbox = inboxList.get(position);
            // 立即刷新
            dataSource.makeInboxRead(userInbox.getId(), userInbox.getInboxId())
                    .subscribe(u -> {
                        // inbox缺少id
                        userInbox.setId(u.getId());
                        Picklon.LAST_READ_MS = 0;
                    }, Throwable::printStackTrace);
        });
    }
}
