package com.example.budgetbuddy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;

public class FragChangeExpExpenses extends FragBase {


    User user;
    EditText edtFood, edtTransport, edtGrocery, edtMisc, edtMedical, edtInvest, edtSalary;
    Button btnAdd;
    Gson gson;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference userDataRef;

    @Override
    int getResourceLayout() {
        return R.layout.frag_change_exp_expenses;
    }

    @Override
    void setUpView() {
        init();
        getOldData();
        initListeners();
    }

    private void initListeners() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isDataChange()) {
                    getCurrentData();
                } else {
                    Toast.makeText(baseContext, "There are no changes.", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private boolean isDataChange() {
        String strGrocery = edtGrocery.getText().toString();
        String strFood = edtFood.getText().toString();
        String strMedical = edtMedical.getText().toString();
        String strInvest = edtInvest.getText().toString();
        String strMisc = edtMisc.getText().toString();
        String strTransport = edtTransport.getText().toString();
        String strSalary = edtSalary.getText().toString();

        return !strGrocery.equals(user.getExp_grocery())
                || !strFood.equals(user.getExp_food())
                || !strMedical.equals(user.getExp_medical())
                || !strTransport.equals(user.getExp_transportation())
                || !strMisc.equals(user.getExp_misc())
                || !strInvest.equals(user.getExp_investment())
                || !strSalary.equals(user.getSalary());
    }

    private void getCurrentData() {

        String strGrocery = edtGrocery.getText().toString();
        String strFood = edtFood.getText().toString();
        String strMedical = edtMedical.getText().toString();
        String strInvest = edtInvest.getText().toString();
        String strMisc = edtMisc.getText().toString();
        String strTransport = edtTransport.getText().toString();
        String strSalary = edtSalary.getText().toString();


        if (strGrocery.isEmpty()) {
            user.setExp_grocery("0");
        } else {
            user.setExp_grocery(strGrocery);
        }
        if (strFood.isEmpty()) {
            user.setExp_food("0");
        } else {
            user.setExp_food(strFood);
        }
        if (strMedical.isEmpty()) {
            user.setExp_medical("0");
        } else {
            user.setExp_medical(strMedical);
        }
        if (strTransport.isEmpty()) {
            user.setExp_transportation("0");
        } else {
            user.setExp_transportation(strTransport);
        }
        if (strMisc.isEmpty()) {
            user.setExp_misc("0");
        } else {
            user.setExp_misc(strMisc);
        }
        if (strInvest.isEmpty()) {
            user.setExp_investment("0");
        } else {
            user.setExp_investment(strInvest);
        }


        if (utils.isStringValidate(strSalary)) {
            user.setSalary(strSalary);
            uploadNewData(user);
        } else {
            Toast.makeText(baseContext, "Enter Salary", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadNewData(final User user) {
        Log.e("final_change", gson.toJson(user));

        userDataRef.child(getUserID()).child(getResources().getString(R.string.user_details)).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(baseContext, "Your data has benn changed.", Toast.LENGTH_SHORT).show();
                Prefs.putString(getResources().getString(R.string.user), gson.toJson(user));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(baseContext, "Something went wrong!", Toast.LENGTH_SHORT).show();
                Log.e("uploadNewData", e.getMessage());
            }
        });
    }

    private void getOldData() {
        edtFood.setText(user.getExp_food());
        edtGrocery.setText(user.getExp_grocery());
        edtMedical.setText(user.getExp_medical());
        edtMisc.setText(user.getExp_misc());
        edtInvest.setText(user.getExp_investment());
        edtTransport.setText(user.getExp_transportation());
        edtSalary.setText(user.getSalary());
    }

    private void init() {
        //model
        user = getUser();

        //layout
        edtFood = getFragView().findViewById(R.id.edt_food);
        edtTransport = getFragView().findViewById(R.id.edt_transport);
        edtGrocery = getFragView().findViewById(R.id.edt_groceries);
        edtMisc = getFragView().findViewById(R.id.edt_misc);
        edtMedical = getFragView().findViewById(R.id.edt_medical);
        edtInvest = getFragView().findViewById(R.id.edt_investment);
        edtSalary = getFragView().findViewById(R.id.edt_salary);
        btnAdd = getFragView().findViewById(R.id.btn_change_exp_value);

        //gson
        gson = new Gson();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        userDataRef = mFirebaseDatabase.getReference();

    }
}