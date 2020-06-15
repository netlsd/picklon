package id.co.picklon.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import id.co.picklon.ui.fragments.AllOrderFragment;
import id.co.picklon.ui.fragments.CanceledOrderFragment;
import id.co.picklon.ui.fragments.FinishedOrderFragment;
import id.co.picklon.ui.fragments.TitledFragment;

public class OrderAdapter extends FragmentPagerAdapter {
    private List<TitledFragment> fragmentList = new ArrayList<>();

    public OrderAdapter(FragmentManager fm) {
        super(fm);

        fragmentList.add(new AllOrderFragment());
        fragmentList.add(new FinishedOrderFragment());
        fragmentList.add(new CanceledOrderFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentList.get(position).getTitle();
    }
}
