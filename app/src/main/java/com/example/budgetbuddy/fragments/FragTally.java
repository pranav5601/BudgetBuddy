package com.example.budgetbuddy.fragments;

import android.app.AlarmManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.model.Expense;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FragTally extends FragBase{
    Expense monthWiseExpense;
    PieChart pieChartMonthly;
    TextView txtTitle;

    private Expense expense;
    private FirebaseDatabase expenseDatabase;
    private DatabaseReference expenseDataRef;
    private ArrayList<Expense> expenseList;
    private long singleDay = AlarmManager.INTERVAL_DAY;
    private long singleWeek = 7 * AlarmManager.INTERVAL_DAY;
    private Calendar calendar;
    private long entryNo;
    static int counter = -1;
    long lastDate = 0;
    private String month_name;
    long expenseCount;
    private Button btnCurrentMonth, btnPreviousMonth;

    int grocery, preGrocery;
    int food, preFood;
    int investment, preInvestment;
    int medical, preMedical;
    int misc, preMisc;
    int transportation, preTransportation;
    int timestamp;
    long currentTS;
    LineChart lineTallyChart;

    @Override
    int getResourceLayout() {
        return R.layout.frag_tally;
    }

    @Override
    void setUpView() {

        init();
        showLoader("FragTally");

        getExpenseCount();
        getExpenseData();
//        setTitle();

    }

    private void init() {
        lineTallyChart = getFragView().findViewById(R.id.line_tally_chart);
        //firebase
        expenseDatabase = FirebaseDatabase.getInstance();
        expenseDataRef = expenseDatabase.getReference();

        //model class
        expense = new Expense();
        expenseList = new ArrayList<>();
        monthWiseExpense = new Expense();

        //Calender
        calendar = Calendar.getInstance();
        currentTS = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, -1);

        

    }

    private void getExpenseCount() {
        expenseDataRef.child(getUserID()).child("expense_details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                expenseCount = snapshot.getChildrenCount();

                if (expenseCount == 0){
                    closeLoader();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

            }
        });

    }


    private void setData() {

        if (expenseList.size() == expenseCount) {

            SimpleDateFormat sdf = new SimpleDateFormat("MM", Locale.getDefault());

            for (int i = expenseList.size() - 1; i >= 0; i--) {

                Log.e("list", gson.toJson(expenseList.get(i)));

                Date currentTimeZone = new Date(Long.parseLong(expenseList.get(i).getTimestamp()));
                String msgWholeDate = sdf.format(currentTimeZone);
                if (getMonth() == Integer.parseInt(msgWholeDate)) {
                    grocery = grocery + Integer.parseInt(expenseList.get(i).getGrocery());
                    food = food + Integer.parseInt(expenseList.get(i).getFood());
                    investment = investment + Integer.parseInt(expenseList.get(i).getInvestment());
                    medical = medical + Integer.parseInt(expenseList.get(i).getMedical());
                    misc = misc + Integer.parseInt(expenseList.get(i).getMisc());
                    transportation = transportation + Integer.parseInt(expenseList.get(i).getTransportation());
                } /*else if (getMonth() - 1 == Integer.parseInt(msgWholeDate)) {
                    preGrocery = preGrocery + Integer.parseInt(expenseList.get(i).getGrocery());
                    preFood = preFood + Integer.parseInt(expenseList.get(i).getFood());
                    preInvestment = preInvestment + Integer.parseInt(expenseList.get(i).getInvestment());
                    preMedical = preMedical + Integer.parseInt(expenseList.get(i).getMedical());
                    preMisc = preMisc + Integer.parseInt(expenseList.get(i).getMisc());
                    preTransportation = preTransportation + Integer.parseInt(expenseList.get(i).getTransportation());
                }*/

            }
            monthWiseExpense.setGrocery(String.valueOf(grocery));
            monthWiseExpense.setFood(String.valueOf(food));
            monthWiseExpense.setInvestment(String.valueOf(investment));
            monthWiseExpense.setMedical(String.valueOf(medical));
            monthWiseExpense.setMisc(String.valueOf(misc));
            monthWiseExpense.setTransportation(String.valueOf(transportation));
            Log.e("tally", gson.toJson(monthWiseExpense));
            setUpLineChart(monthWiseExpense, month_name);

        }


    }
    public void setUpLineChart(Expense monthWiseExpense, String previousMonth) {

        ArrayList<PieEntry> dataEntries = new ArrayList<>();
        ArrayList<Entry> lineExpDataEntries = new ArrayList<>();
        ArrayList<Entry> lineMonthDataEntries = new ArrayList<>();

        lineExpDataEntries.add(new Entry(0f,Float.parseFloat(getUser().getExp_grocery())));
        lineExpDataEntries.add(new Entry(1f,Float.parseFloat(getUser().getExp_food())));
        lineExpDataEntries.add(new Entry(2f,Float.parseFloat(getUser().getExp_medical())));
        lineExpDataEntries.add(new Entry(3f,Float.parseFloat(getUser().getExp_transportation())));
        lineExpDataEntries.add(new Entry(4f,Float.parseFloat(getUser().getExp_misc())));
        lineExpDataEntries.add(new Entry(5f,Float.parseFloat(getUser().getExp_investment())));


        lineMonthDataEntries.add(new Entry(0f,Float.parseFloat(monthWiseExpense.getGrocery())));
        lineMonthDataEntries.add(new Entry(1f,Float.parseFloat(monthWiseExpense.getFood())));
        lineMonthDataEntries.add(new Entry(2f,Float.parseFloat(monthWiseExpense.getMedical())));
        lineMonthDataEntries.add(new Entry(3f,Float.parseFloat(monthWiseExpense.getTransportation())));
        lineMonthDataEntries.add(new Entry(4f,Float.parseFloat(monthWiseExpense.getMisc())));
        lineMonthDataEntries.add(new Entry(5f,Float.parseFloat(monthWiseExpense.getInvestment())));

        LineDataSet lineExpDataSet = new LineDataSet(lineExpDataEntries,"Expected Expense");
        lineExpDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineExpDataSet.setColor(getResources().getColor(R.color.groceries));
        LineDataSet lineMonthDataSet = new LineDataSet(lineMonthDataEntries,"Current Month Expense");
        lineMonthDataSet.setColor(getResources().getColor(R.color.food));
        lineMonthDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        ArrayList<ILineDataSet> dataSetList = new ArrayList<>();
        dataSetList.add(lineExpDataSet);
        dataSetList.add(lineMonthDataSet);

        final String[] quarters = new String[] { "Grocery", "Food", "Medical", "Transport","Misc","Investment" };

        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return quarters[(int) value];
            }
        };
        XAxis xAxis = lineTallyChart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

        LineData data = new LineData(dataSetList);
        lineTallyChart.setData(data);


        closeLoader();

    }

    private void setTitle() {
        month_name = new SimpleDateFormat("MMMM", Locale.getDefault()).format(new Date());
        txtTitle.setText("Pie chart of " + month_name);
    }

    private int getMonth() {
        String currentDate = new SimpleDateFormat("MM", Locale.getDefault()).format(new Date());
//        Log.e("month", currentDate);
        return Integer.parseInt(currentDate);
    }

}