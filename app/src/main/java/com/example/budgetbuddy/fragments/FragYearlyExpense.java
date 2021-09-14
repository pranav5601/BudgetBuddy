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

public class FragYearlyExpense extends FragBase {
    Expense yearWiseExpense;
    PieChart pieChartYearly;
    TextView txtTitle, txtEmptyMsgYear;
    private LinearLayout lytYear;
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
    private String year;
    long expenseCount;
    private Button btnCurrentYear, btnPreviousYear;
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
        return R.layout.frag_yearly_expense;
    }

    @Override
    void setUpView() {
        showLoader("FragYearlyExpense");

        init();
        getExpenseCount();

        getExpenseData();
        setTitle();
        clickListeners();
    }

    private void clickListeners() {
        if (!btnCurrentYear.isEnabled()) {
            btnCurrentYear.setBackgroundColor(getResources().getColor(R.color.gray));
        }
        btnCurrentYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yearWiseExpense.setGrocery(String.valueOf(grocery));
                yearWiseExpense.setFood(String.valueOf(food));
                yearWiseExpense.setInvestment(String.valueOf(investment));
                yearWiseExpense.setMedical(String.valueOf(medical));
                yearWiseExpense.setMisc(String.valueOf(misc));
                yearWiseExpense.setTransportation(String.valueOf(transportation));
                Log.e("yearWise", gson.toJson(yearWiseExpense));
                setTitle();
                setUpPieChartWeekly(yearWiseExpense, year);
                btnCurrentYear.setBackgroundColor(getResources().getColor(R.color.gray));
                btnPreviousYear.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnCurrentYear.setEnabled(false);
                btnPreviousYear.setEnabled(true);
            }
        });
        btnPreviousYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(preGrocery == 0 && preFood == 0 && preTransportation == 0 && preInvestment == 0 && preMedical == 0 && preMisc == 0){
                    Toast.makeText(baseContext, "There was no expense added in last year.", Toast.LENGTH_LONG).show();
                }else {
                    yearWiseExpense.setGrocery(String.valueOf(preGrocery));
                    yearWiseExpense.setFood(String.valueOf(preFood));
                    yearWiseExpense.setInvestment(String.valueOf(preInvestment));
                    yearWiseExpense.setMedical(String.valueOf(preMedical));
                    yearWiseExpense.setMisc(String.valueOf(preMisc));
                    yearWiseExpense.setTransportation(String.valueOf(preTransportation));
                    /*yearWiseExpense.setGrocery("120");
                    yearWiseExpense.setFood("320");
                    yearWiseExpense.setInvestment("220");
                    yearWiseExpense.setMedical("660");
                    yearWiseExpense.setMisc("400");
                    yearWiseExpense.setTransportation("250");*/
                    Log.e("yearWise", gson.toJson(yearWiseExpense));
                    String previousMonth = String.valueOf(getYear() - 1);
                    txtTitle.setText("Pie chart of " + previousMonth);
                    setUpPieChartWeekly(yearWiseExpense, previousMonth);
                    btnPreviousYear.setBackgroundColor(getResources().getColor(R.color.gray));
                    btnCurrentYear.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    btnCurrentYear.setEnabled(true);
                    btnPreviousYear.setEnabled(false);
                }
            }
        });
    }

    private void getExpenseCount() {
        expenseDataRef.child(getUserID()).child("expense_details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                expenseCount = snapshot.getChildrenCount();
                if (expenseCount == 0) {

                    lytYear.setVisibility(View.INVISIBLE);
                    txtEmptyMsgYear.setVisibility(View.VISIBLE);
                    closeLoader();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setTitle() {
        year = new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());
        txtTitle.setText("Pie chart of " + year);
    }

    private int getYear() {
        String currentDate = new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());
//        Log.e("month", currentDate);
        return Integer.parseInt(currentDate);
    }

    private void setData() {
        if (expenseList.size() == expenseCount) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
            for (int i = expenseList.size() - 1; i >= 0; i--) {
                Log.e("list", gson.toJson(expenseList.get(i)));
                Date currentTimeZone = new Date(Long.parseLong(expenseList.get(i).getTimestamp()));
                String msgWholeDate = sdf.format(currentTimeZone);
                if (getYear() == Integer.parseInt(msgWholeDate)) {
                    grocery = grocery + Integer.parseInt(expenseList.get(i).getGrocery());
                    food = food + Integer.parseInt(expenseList.get(i).getFood());
                    investment = investment + Integer.parseInt(expenseList.get(i).getInvestment());
                    medical = medical + Integer.parseInt(expenseList.get(i).getMedical());
                    misc = misc + Integer.parseInt(expenseList.get(i).getMisc());
                    transportation = transportation + Integer.parseInt(expenseList.get(i).getTransportation());
                } else if (getYear() - 1 == Integer.parseInt(msgWholeDate)) {
                    preGrocery = preGrocery + Integer.parseInt(expenseList.get(i).getGrocery());
                    preFood = preFood + Integer.parseInt(expenseList.get(i).getFood());
                    preInvestment = preInvestment + Integer.parseInt(expenseList.get(i).getInvestment());
                    preMedical = preMedical + Integer.parseInt(expenseList.get(i).getMedical());
                    preMisc = preMisc + Integer.parseInt(expenseList.get(i).getMisc());
                    preTransportation = preTransportation + Integer.parseInt(expenseList.get(i).getTransportation());
                }
            }
            yearWiseExpense.setGrocery(String.valueOf(grocery));
            yearWiseExpense.setFood(String.valueOf(food));
            yearWiseExpense.setInvestment(String.valueOf(investment));
            yearWiseExpense.setMedical(String.valueOf(medical));
            yearWiseExpense.setMisc(String.valueOf(misc));
            yearWiseExpense.setTransportation(String.valueOf(transportation));
            Log.e("yearWise", gson.toJson(yearWiseExpense));
            setUpPieChartWeekly(yearWiseExpense, year);
            pieChartYearly.refreshDrawableState();
            closeLoader();
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
            }
        });
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
        pieChartYearly.setData(pieData);
        pieChartYearly.getDescription().setEnabled(false);
        pieChartYearly.setCenterText(previousMonth);
        pieChartYearly.animate();
        closeLoader();
    }

    private void init() {
//layout
        pieChartYearly = getFragView().findViewById(R.id.pie_chart_yearly);
        btnCurrentYear = getFragView().findViewById(R.id.btn_current_year);
        btnPreviousYear = getFragView().findViewById(R.id.btn_previous_year);
        txtEmptyMsgYear = getFragView().findViewById(R.id.txt_empty_msg_year);
        lytYear = getFragView().findViewById(R.id.lyt_year);

        //firebase
        expenseDatabase = FirebaseDatabase.getInstance();
        expenseDataRef = expenseDatabase.getReference();
        //model class
        expense = new Expense();
        expenseList = new ArrayList<>();
        yearWiseExpense = new Expense();
        //Calender
        calendar = Calendar.getInstance();
        currentTS = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, -1);
        txtTitle = getFragView().findViewById(R.id.txtTitleYear);
    }
}