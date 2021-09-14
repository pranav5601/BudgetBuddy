package com.example.budgetbuddy.fragments;

import android.app.AlarmManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.adapter.PagerAdapter;
import com.example.budgetbuddy.model.Expense;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;


public class FragMain extends FragBase {

    private ViewPager mainViewPager;
    private TabLayout mainTabLayout;
    private TabItem tabWeekly, tabMonthly, tabYearly;
    private PagerAdapter pagerAdapter;
    private Expense expense;
    private FirebaseDatabase expenseDatabase;
    private DatabaseReference expenseDataRef;
    private ArrayList<Expense> expenseList;
    private long singleDay = AlarmManager.INTERVAL_DAY;
    private long singleWeek = 7 * AlarmManager.INTERVAL_DAY;
    private Calendar calendar;
    private Expense weekWiseExpense;


    @Override
    int getResourceLayout() {
        return R.layout.frag_main;
    }

    @Override
    void setUpView() {
        init();
        initTabLayout();

    }

    private void initTabLayout() {
        mainViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mainTabLayout));
        mainTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mainViewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()) {
                    case 0:
//                        Toast.makeText(baseContext, "Tab 1", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
//                        Toast.makeText(baseContext, "Tab 2", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
//                        Toast.makeText(baseContext, "Tab 3", Toast.LENGTH_SHORT).show();
                        break;

                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void init() {
        //layout
        mainTabLayout = getFragView().findViewById(R.id.mainTabLayout);
        mainViewPager = getFragView().findViewById(R.id.tabPager);
        tabWeekly = getFragView().findViewById(R.id.tab_weekly);
        tabMonthly = getFragView().findViewById(R.id.tab_monthly);
        tabYearly = getFragView().findViewById(R.id.tab_yearly);

        //pager adapter
        if (getFragmentManager() != null) {
            pagerAdapter = new PagerAdapter(getFragmentManager(), mainTabLayout.getTabCount(), weekWiseExpense);
            mainViewPager.setAdapter(pagerAdapter);
        }

        //Firebase
        expenseDatabase = FirebaseDatabase.getInstance();
        expenseDataRef = expenseDatabase.getReference();

        //model class
        expense = new Expense();
        expenseList = new ArrayList<>();
        weekWiseExpense = new Expense();

        //Calender
        calendar = Calendar.getInstance();
    }

}