package com.example.budgetbuddy.activities;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.budgetbuddy.R;
import com.example.budgetbuddy.model.User;
import com.google.gson.Gson;

public class ActExpenseCategories extends ActBase {
    User user;
    EditText edtFood, edtTransport, edtGrocery, edtMisc, edtMedical, edtInvest, edtSalary;
    Button btnAdd;
    Gson gson;
    String from;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_expense_categories);
        initialization();
        clickListener();
    }
    private void clickListener() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        Log.e("user_data",gson.toJson(user));
        if (utils.isStringValidate(strSalary)){
            user.setSalary(strSalary);
            toNextAct();
        }else{
            Toast.makeText(this, "Enter Salary", Toast.LENGTH_SHORT).show();
        }
    }
    private void toNextAct() {
        if(from.equals("0")){
            Intent secQueIntent = new Intent(ActExpenseCategories.this,ActSecurityQuestion.class);
            secQueIntent.putExtra("user_detail",gson.toJson(user));
            startActivity(secQueIntent);
        }
    }
    private void initialization() {
        //view
        edtFood = findViewById(R.id.edt_food);
        edtTransport = findViewById(R.id.edt_transport);
        edtGrocery = findViewById(R.id.edt_groceries);
        edtMisc = findViewById(R.id.edt_misc);
        edtMedical = findViewById(R.id.edt_medical);
        edtInvest = findViewById(R.id.edt_investment);
        edtSalary = findViewById(R.id.edt_salary);
        btnAdd = findViewById(R.id.btn_next);
        //model class
        gson = new Gson();
        user =  gson.fromJson((String)getIntent().getSerializableExtra("user_detail"),User.class);
        from = (String)getIntent().getSerializableExtra("from");
    }
}