package com.t_hawk.timelog.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.widget.DatePicker;

import com.t_hawk.timelog.fragments.DatePickerFragment;

/**
 * Project: TimeLog
 * Created by Morten on 1/30/2017.
 *
 * @author Morten
 */
public class TabPagerAdapter extends FragmentPagerAdapter {
    Bundle bundle;
    String[] titles;

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public TabPagerAdapter(FragmentManager fm, Bundle bundle) {
        super(fm);
        this.bundle = bundle;
    }

    @Override
    public Fragment getItem(int num) {
        /*Fragment fragment = new TabListFragment();
        Bundle args = new Bundle();
        args.putSerializable("voters",bundle.getSerializable( num == 0 ? "pros" : "cons"));
        fragment.setArguments(args);
        return fragment;*/
        return new DatePickerFragment().listener(new DatePickerFragment.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                System.out.println("Here");
            }
        });
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    public void setTitles(String[] titles) {
        this.titles = titles;
    }
}
