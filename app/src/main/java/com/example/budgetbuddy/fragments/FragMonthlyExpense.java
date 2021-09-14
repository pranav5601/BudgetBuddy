package com.example.budgetbuddy.fragments;

import android.app.AlarmManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FragMonthlyExpense extends FragBase {
    Expense monthWiseExpense;
    PieChart pieChartMonthly;
    TextView txtTitle, txtEmptyMsgMonth;
    private LinearLayout lyt_month;
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

    @Override
    int getResourceLayout() {
        return R.layout.frag_monthly_expense;
    }

    @Override
    void setUpView() {
        showLoader("FragMonthlyExpenses");

        init();
        getExpenseCount();
        getExpenseData();
        setTitle();
        clickListeners();
    }

    private void clickListeners() {
        if (!btnCurrentMonth.isEnabled()) {
            btnCurrentMonth.setBackgroundColor(getResources().getColor(R.color.gray));
        }
        btnCurrentMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monthWiseExpense.setGrocery(String.valueOf(grocery));
                monthWiseExpense.setFood(String.valueOf(food));
                monthWiseExpense.setInvestment(String.valueOf(investment));
                monthWiseExpense.setMedical(String.valueOf(medical));
                monthWiseExpense.setMisc(String.valueOf(misc));
                monthWiseExpense.setTransportation(String.valueOf(transportation));
                Log.e("monthWise", gson.toJson(monthWiseExpense));
                setTitle();
                setUpPieChartWeekly(monthWiseExpense, month_name);
                btnCurrentMonth.setBackgroundColor(getResources().getColor(R.color.gray));
                btnPreviousMonth.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnCurrentMonth.setEnabled(false);
                btnPreviousMonth.setEnabled(true);
            }
        });
        btnPreviousMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(preGrocery == 0 && preFood == 0 && preTransportation == 0 && preInvestment == 0 && preMedical == 0 && preMisc == 0){
                    Toast.makeText(baseContext, "There was no expense added in last month.", Toast.LENGTH_LONG).show();
                }else{
                    monthWiseExpense.setGrocery(String.valueOf(preGrocery));
                    monthWiseExpense.setFood(String.valueOf(preFood));
                    monthWiseExpense.setInvestment(String.valueOf(preInvestment));
                    monthWiseExpense.setMedical(String.valueOf(preMedical));
                    monthWiseExpense.setMisc(String.valueOf(preMisc));
                    monthWiseExpense.setTransportation(String.valueOf(preTransportation));
                /*monthWiseExpense.setGrocery("120");
                monthWiseExpense.setFood("320");
                monthWiseExpense.setInvestment("220");
                monthWiseExpense.setMedical("660");
                monthWiseExpense.setMisc("400");
                monthWiseExpense.setTransportation("250");*/
                    Log.e("monthWise", gson.toJson(monthWiseExpense));
                    String previousMonth = new SimpleDateFormat("MMMM").format(calendar.getTime());
                    txtTitle.setText("Pie chart of " + previousMonth);
                    setUpPieChartWeekly(monthWiseExpense, previousMonth);
                    btnPreviousMonth.setBackgroundColor(getResources().getColor(R.color.gray));
                    btnCurrentMonth.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    btnCurrentMonth.setEnabled(true);
                    btnPreviousMonth.setEnabled(false);
                }

            }
        });
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
                } else if (getMonth() - 1 == Integer.parseInt(msgWholeDate)) {
                    preGrocery = preGrocery + Integer.parseInt(expenseList.get(i).getGrocery());
                    preFood = preFood + Integer.parseInt(expenseList.get(i).getFood());
                    preInvestment = preInvestment + Integer.parseInt(expenseList.get(i).getInvestment());
                    preMedical = preMedical + Integer.parseInt(expenseList.get(i).getMedical());
                    preMisc = preMisc + Integer.parseInt(expenseList.get(i).getMisc());
                    preTransportation = preTransportation + Integer.parseInt(expenseList.get(i).getTransportation());
                }
            }


            monthWiseExpense.setGrocery(String.valueOf(grocery));
            monthWiseExpense.setFood(String.valueOf(food));
            monthWiseExpense.setInvestment(String.valueOf(investment));
            monthWiseExpense.setMedical(String.valueOf(medical));
            monthWiseExpense.setMisc(String.valueOf(misc));
            monthWiseExpense.setTransportation(String.valueOf(transportation));
            Log.e("monthWise", gson.toJson(monthWiseExpense));

            setUpPieChartWeekly(monthWiseExpense, month_name);
            pieChartMonthly.refreshDrawableState();
        }
    }

    private void getExpenseCount() {
        expenseDataRef.child(getUserID()).child("expense_details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                expenseCount = snapshot.getChildrenCount();
                if (expenseCount == 0){
                    lyt_month.setVisibility(View.INVISIBLE);
                    txtEmptyMsgMonth.setVisibility(View.VISIBLE);
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

    private void init() {
        //layout
        pieChartMonthly = getFragView().findViewById(R.id.pie_chart_monthly);
        btnCurrentMonth = getFragView().findViewById(R.id.btn_current_month);
        btnPreviousMonth = getFragView().findViewById(R.id.btn_previous_month);
        txtEmptyMsgMonth = getFragView().findViewById(R.id.txt_empty_msg_month);
        lyt_month = getFragView().findViewById(R.id.lyt_month);

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
        txtTitle = getFragView().findViewById(R.id.txtTitleMonth);
    }

    public void setUpPieChartWeekly(Expense monthWiseExpense, String previousMonth) {
        ArrayList<PieEntry> dataEntries = new ArrayList<>();
        dataEntries.add(new PieEntry(Integer.parseInt(monthWiseExpense.getGrocery()), "Grocery"));
        dataEntries.add(new PieEntry(Integer.parseInt(monthWiseExpense.getFood()), "Food"));
        dataEntries.add(new PieEntry(Integer.parseInt(monthWiseExpense.getInvestment()), "Investment"));
        dataEntries.add(new PieEntry(Integer.parseInt(monthWiseExpense.getMedical()), "Medical"));
        dataEntries.add(new PieEntry(Integer.parseInt(monthWiseExpense.getTransportation()), "Transportation"));
        dataEntries.add(new PieEntry(Integer.parseInt(monthWiseExpense.getMisc()), "Miscellaneous"));
        PieDataSet pieDataSet = new PieDataSet(dataEntries, "Expense");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(getResources().getColor(R.color.white));
        pieDataSet.setValueTextSize(20f);
        PieData pieData = new PieData(pieDataSet);
        pieChartMonthly.setData(pieData);
        pieChartMonthly.getDescription().setEnabled(false);
        pieChartMonthly.setCenterText(previousMonth);
        pieChartMonthly.animate();
        closeLoader();
    }

    private int getDate() {
        String currentDate = new SimpleDateFormat("dd", Locale.getDefault()).format(new Date());
//        Log.e("month", currentDate);
        return Integer.parseInt(currentDate);
    }

    private int getYear() {
        String currentDate = new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());
//        Log.e("month", currentDate);
        return Integer.parseInt(currentDate);
    }
}