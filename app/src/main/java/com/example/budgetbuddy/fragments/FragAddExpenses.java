package com.example.budgetbuddy.fragments;

import androidx.annotation.NonNull;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.model.Expense;
import com.example.budgetbuddy.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;


public class FragAddExpenses extends FragBase {
    private FirebaseDatabase expenseDatabase;
    private DatabaseReference expenseDatabaseRef;
    private EditText edtFood, edtTransport, edtGrocery, edtMisc, edtMedical, edtInvest;
    private View v;
    private Button btnAdd;
    private User user;
    private Gson gson;
    private Expense expense;

    @Override
    int getResourceLayout() {
        return R.layout.frag_add_expenses;
    }

    @Override
    void setUpView() {
        init();
        initListeners();
    }


    private void initListeners() {

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoader("FragAddExpenses");
                getData();
            }
        });

    }


    private void getData() {

        String strGrocery = edtGrocery.getText().toString();
        String strFood = edtFood.getText().toString();
        String strMedical = edtMedical.getText().toString();
        String strInvest = edtInvest.getText().toString();
        String strMisc = edtMisc.getText().toString();
        String strTransport = edtTransport.getText().toString();

        if (strGrocery.isEmpty() && strMedical.isEmpty() && strInvest.isEmpty() && strFood.isEmpty() && strMisc.isEmpty() && strTransport.isEmpty()) {
            Toast.makeText(baseContext, "Enter at least one value.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (strGrocery.isEmpty()) {
            expense.setGrocery("0");
        } else {
            expense.setGrocery(strGrocery);
        }
        if (strFood.isEmpty()) {
            expense.setFood("0");
        } else {
            expense.setFood(strFood);
        }
        if (strMedical.isEmpty()) {
            expense.setMedical("0");
        } else {
            expense.setMedical(strMedical);
        }
        if (strTransport.isEmpty()) {
            expense.setTransportation("0");
        } else {
            expense.setTransportation(strTransport);
        }
        if (strMisc.isEmpty()) {
            expense.setMisc("0");
        } else {
            expense.setMisc(strMisc);
        }
        if (strInvest.isEmpty()) {
            expense.setInvestment("0");
        } else {
            expense.setInvestment(strInvest);
        }

        expense.setTimestamp(getCurrentTime());

        addExpenses();


        Log.e("user_data", gson.toJson(expense));

    }

    private void addExpenses() {
        expenseDatabaseRef.child("expense_details").child(getCurrentTime()).setValue(expense).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                closeLoader();
                clearData();
                edtGrocery.requestFocus();
                Toast.makeText(getContext(), "Added", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                closeLoader();
                Log.e("error_expense", e.getMessage());
            }
        });
    }

    private String getCurrentTime() {

        return String.valueOf(System.currentTimeMillis());
    }


    private void clearData(){
        edtGrocery.setText("");
        edtFood.setText("");
        edtTransport.setText("");
        edtMedical.setText("");
        edtMisc.setText("");
        edtInvest.setText("");
    }

    private void init() {

        //Firebase
        expenseDatabase = FirebaseDatabase.getInstance();
        expenseDatabaseRef = expenseDatabase.getReference().child(getUserID());

        //view
        edtFood = getFragView().findViewById(R.id.edt_food);
        edtMedical = getFragView().findViewById(R.id.edt_medical);
        edtInvest = getFragView().findViewById(R.id.edt_investment);
        edtMisc = getFragView().findViewById(R.id.edt_misc);
        edtGrocery = getFragView().findViewById(R.id.edt_groceries);
        edtTransport = getFragView().findViewById(R.id.edt_transport);
        btnAdd = getFragView().findViewById(R.id.btn_add);

        //gson
        gson = new Gson();
        //model class
        expense = new Expense();
        user = gson.fromJson((String) Prefs.getString(getResources().getString(R.string.user), ""), User.class);
    }
}