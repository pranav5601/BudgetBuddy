package com.example.budgetbuddy.fragments;

import android.app.AlarmManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.model.Expense;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;


public class FragWeeklyExpense extends FragBase {

    Expense weekWiseExpense;
    PieChart pieChartWeekly;


    private Expense expense;
    private TextView txtEmptyMsg;
    private FirebaseDatabase expenseDatabase;
    private DatabaseReference expenseDataRef;
    private ArrayList<Expense> expenseList;
    private long singleWeek = 7 * AlarmManager.INTERVAL_DAY;
    private Calendar calendar;
    long lastDate = 0;
    long expenseCount;

    int grocery;
    int food;
    int investment;
    int medical;
    int misc;
    int transportation;
    int timestamp;
    long currentTS;

    @Override
    int getResourceLayout() {
        return R.layout.frag_weekly_expense;
    }


    @Override
    void setUpView() {
        init();
        showLoader("FragWeeklyExpenses");

        getExpenseCount();

        getExpenseData();


    }

    @Override
    public void onResume() {
        super.onResume();


        Log.e("onResume", "onResume");
    }

    private void getExpenseCount() {
        expenseDataRef.child(getUserID()).child("expense_details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                expenseCount = snapshot.getChildrenCount();
                if (expenseCount == 0) {
                    pieChartWeekly.setVisibility(View.GONE);
                    txtEmptyMsg.setVisibility(View.VISIBLE);
                    closeLoader();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("week_data_count_error", error.getMessage());

            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("onPause", "onPause");
        grocery = 0;
        food = 0;
        investment = 0;
        medical = 0;
        misc = 0;
        transportation = 0;
    }

    private void setData() {
        if (expenseList.size() == expenseCount) {
            for (int i = expenseList.size() - 1; i >= 0; i--) {

                Log.e("list", gson.toJson(expenseList.get(i)));

                if (Long.parseLong(expenseList.get(i).getTimestamp()) > (currentTS - (7 * AlarmManager.INTERVAL_DAY))) {

                    grocery = grocery + Integer.parseInt(expenseList.get(i).getGrocery());
                    food = food + Integer.parseInt(expenseList.get(i).getFood());
                    investment = investment + Integer.parseInt(expenseList.get(i).getInvestment());
                    medical = medical + Integer.parseInt(expenseList.get(i).getMedical());
                    misc = misc + Integer.parseInt(expenseList.get(i).getMisc());
                    transportation = transportation + Integer.parseInt(expenseList.get(i).getTransportation());
                }

            }
            weekWiseExpense.setGrocery(String.valueOf(grocery));
            weekWiseExpense.setFood(String.valueOf(food));
            weekWiseExpense.setInvestment(String.valueOf(investment));
            weekWiseExpense.setMedical(String.valueOf(medical));
            weekWiseExpense.setMisc(String.valueOf(misc));
            weekWiseExpense.setTransportation(String.valueOf(transportation));
            Log.e("weekWiseExpense", gson.toJson(weekWiseExpense));

           /* if(weekWiseExpense.getFood().equals("0")
            && weekWiseExpense.getGrocery().equals("0")
            && weekWiseExpense.getMedical().equals("0")
            && weekWiseExpense.getInvestment().equals("0")
            && weekWiseExpense.getTransportation().equals("0")
            && weekWiseExpense.getMisc().equals("0")){
                closeLoader();
                pieChartWeekly.setVisibility(View.GONE);
                txtEmptyMsg.setVisibility(View.VISIBLE);
            }else{
            }*/

            setUpPieChartWeekly(weekWiseExpense);
        }
    }

    private void getExpenseData() {

        expenseDataRef.child(getUserID()).child("expense_details").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                expense = snapshot.getValue(Expense.class);


//                getTimeMillis(expense,counter++);
                expenseList.add(expense);
                setData();
                Log.e("expense_details", " " + gson.toJson(expense) + " " + previousChildName);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("week_data_count_error", error.getMessage());
            }
        });
    }


    private void init() {

        //layout
        pieChartWeekly = getFragView().findViewById(R.id.pie_chart_weekly);
        txtEmptyMsg = getFragView().findViewById(R.id.txt_empty_msg_week);


        //firebase
        expenseDatabase = FirebaseDatabase.getInstance();
        expenseDataRef = expenseDatabase.getReference();

        //model class
        expense = new Expense();
        expenseList = new ArrayList<>();
        weekWiseExpense = new Expense();

        //Calender
        calendar = Calendar.getInstance();
        currentTS = calendar.getTimeInMillis();
    }

    public void setUpPieChartWeekly(Expense weekWiseExpense) {

        ArrayList<PieEntry> dataEntries = new ArrayList<>();

        dataEntries.add(new PieEntry(Integer.parseInt(weekWiseExpense.getGrocery()), "Grocery"));
        dataEntries.add(new PieEntry(Integer.parseInt(weekWiseExpense.getFood()), "Food"));
        dataEntries.add(new PieEntry(Integer.parseInt(weekWiseExpense.getInvestment()), "Investment"));
        dataEntries.add(new PieEntry(Integer.parseInt(weekWiseExpense.getMedical()), "Medical"));
        dataEntries.add(new PieEntry(Integer.parseInt(weekWiseExpense.getTransportation()), "Transportation"));
        dataEntries.add(new PieEntry(Integer.parseInt(weekWiseExpense.getMisc()), "Miscellaneous"));

        PieDataSet pieDataSet = new PieDataSet(dataEntries, "Expense");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(getResources().getColor(R.color.white));
        pieDataSet.setValueTextSize(20f);
        PieData pieData = new PieData(pieDataSet);
        pieChartWeekly.setData(pieData);
        pieChartWeekly.getDescription().setEnabled(false);
        pieChartWeekly.setCenterText("Expense of last week");
        pieChartWeekly.animate();

        closeLoader();

    }

}