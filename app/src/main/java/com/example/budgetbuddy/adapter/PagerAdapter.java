package com.example.budgetbuddy.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.budgetbuddy.fragments.FragMonthlyExpense;
import com.example.budgetbuddy.fragments.FragWeeklyExpense;
import com.example.budgetbuddy.fragments.FragYearlyExpense;
import com.example.budgetbuddy.model.Expense;

public class PagerAdapter extends FragmentPagerAdapter {

    private final Expense weekWiseExpense;
    private int behavior;

    public PagerAdapter(@NonNull FragmentManager fm, int behavior, Expense weekWiseExpense) {
        super(fm, behavior);
        this.behavior = behavior;
        this.weekWiseExpense = weekWiseExpense;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:

                return new FragWeeklyExpense();
            case 1:
                return new FragMonthlyExpense();
            case 2:
                return new FragYearlyExpense();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return behavior;
    }
}
