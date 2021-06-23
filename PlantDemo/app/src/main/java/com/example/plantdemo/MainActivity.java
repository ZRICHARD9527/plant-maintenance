package com.example.plantdemo;

import android.graphics.Color;
import android.os.Bundle;

import com.example.plantdemo.fragment.BlankFragment;
import com.example.plantdemo.fragment.NotifyFragment;
import com.example.plantdemo.fragment.StatisticFragment;
import com.google.android.material.tabs.TabLayout;

import org.litepal.LitePal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewpager;
    int[] icons=new int[]{R.drawable.notify2_gray,R.drawable.statistics2_gray,R.drawable.notify2_blue,
            R.drawable.statistics2_blue};
    int[] titles=new int[]{R.string.statistics,R.string.notify};
    Fragment[] fragments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initview();
        LitePal.initialize(this);
    }
    /**
     * 初始化控件
     */
    void initview() {
        //tablayout
        tabLayout = findViewById(R.id.tablayout);
        for (int i = 0; i < titles.length; i++) {
            tabLayout.addTab(tabLayout.newTab());
        }
        tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
        tabLayout.setFocusableInTouchMode(false);

        //viewpager
        viewpager = findViewById(R.id.viewpager);
        fragments = new Fragment[titles.length];

        for (int i = 0; i < titles.length; i++) {
            if(i==1) fragments[1]= NotifyFragment.newInstance();
            else if(i==0) fragments[0]= StatisticFragment.newInstance();
                else
            fragments[i] = BlankFragment.newInstance(getString(titles[i]));
        }
        viewpager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        });
        //关联tablayout和viewpager
        tabLayout.setupWithViewPager(viewpager);
        for (int i = 0; i < titles.length; i++) {
            tabLayout.getTabAt(i).setIcon(icons[i]);
            tabLayout.getTabAt(i).setText(getString(titles[i]));
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                 updataIcon(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updataIcon(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.getTabAt(0).select();
    }

    /**
     * 切换icon
     */
    void updataIcon(int position){
        if(tabLayout!=null) {
            TabLayout.Tab tab = tabLayout.getTabAt(position);
            if (tab.isSelected()) {
                tab.setIcon(icons[icons.length / 2 + position]);
            } else {
                tab.setIcon(icons[position]);
            }
        }
    }

}