package com.t_hawk.timelog.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v13.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.t_hawk.timelog.R;
import com.t_hawk.timelog.adapters.TabPagerAdapter;

/**
 * Project: TimeLog
 * Created by Morten on 1/30/2017.
 *
 * @author Morten
 */

public class TabDialogFragment extends DialogFragment {
    private static final String ARG_TITLE = "title";

    private FragmentTabHost mTabHost;
    private ViewPager viewPager;
    private TabPagerAdapter adapter;

    public static TabDialogFragment newInstance(String title) {
        TabDialogFragment fragment = new TabDialogFragment();
        Bundle args = new Bundle();

        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_dialog, container);

        getDialog().setTitle(getArguments().getString(ARG_TITLE));

        mTabHost = (FragmentTabHost) view.findViewById(R.id.tabs);

        mTabHost.setup(getActivity(), getChildFragmentManager());
        mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("Плюсов"), Fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator("Минусов"), Fragment.class, null);

        adapter = new TabPagerAdapter(getChildFragmentManager(), getArguments());
        adapter.setTitles(new String[]{"Плюсов", "Минусов"});

        viewPager = (ViewPager)view.findViewById(R.id.pager);
        viewPager.setAdapter(adapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                mTabHost.setCurrentTab(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                int i = mTabHost.getCurrentTab();
                viewPager.setCurrentItem(i);
            }
        });

        return view;
    }
}