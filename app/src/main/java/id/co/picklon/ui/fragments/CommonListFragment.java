package id.co.picklon.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import id.co.picklon.R;
import id.co.picklon.model.data.DataSource;
import id.co.picklon.ui.view.DividerItemDecoration;
import rx.Observable;
import rx.functions.Action1;

public abstract class CommonListFragment extends TitledFragment {
    @BindView(R.id.list)
    RecyclerView listView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeView;

    @Inject
    DataSource dataSource;

    private Observable observable;
    private Action1 action1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.common_list_swipe, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentComponent().inject(this);

        initializeSwipeRefreshLayout();
    }

    protected void loadNetwork(Observable observable, Action1 action1) {
        this.observable = observable;
        this.action1 = action1;

        startLoad();
    }

    @SuppressWarnings("unchecked")
    protected void startLoad() {
        swipeView.setRefreshing(true);
        observable
                .doOnNext(this::stopRefresh)
                .subscribe(action1, this::stopRefresh);
    }

    private void stopRefresh(Object o) {
        swipeView.setRefreshing(false);
    }

    private void initializeSwipeRefreshLayout() {
        swipeView.setColorSchemeResources(R.color.colorPrimary);
        swipeView.setOnRefreshListener(this::startLoad);
    }

    protected void initializeRecyclerView(RecyclerView.Adapter adapter) {
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(mContext));
        listView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
    }
}
