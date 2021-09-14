package com.example.budgetbuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.model.User;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

public class ActSignUp extends ActBase {

    TextInputLayout edtEmailId, edtPassword, edtName, edtMobile;
    Button btnRegister;
    String uid;
    Gson gson;


    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_sign_up);

        initialization();
        clickListener();

    }

    private void clickListener() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email_id = edtEmailId.getEditText().getText().toString();
                String password = edtPassword.getEditText().getText().toString();
                String name = edtName.getEditText().getText().toString();
                String mobile = edtMobile.getEditText().getText().toString();

                if (credentialValidation(email_id, password, name, mobile)) {

                    user.setUser_email_id(email_id);
                    user.setUser_mobile(mobile);
                    user.setUser_password(password);
                    user.setUser_name(name);

                    Intent  signUpIntent = new Intent(ActSignUp.this,ActExpenseCategories.class);
                    signUpIntent.putExtra("user_detail",gson.toJson(user));
                    signUpIntent.putExtra("from","0");

                    startActivity(signUpIntent);


                }
            }
        });
    }


    private boolean credentialValidation(String email_id, String password, String name, String mobile) {

        if (!utils.isStringValidate(name)) {
            utils.showErrorMsg(edtName,"Enter name");
            utils.yoyoAnimation(edtName);
            return false;
        }

        if (!utils.isStringValidate(mobile)) {
            utils.showErrorMsg(edtName,null);
            utils.showErrorMsg(edtMobile,"Enter mobile number");
            utils.yoyoAnimation(edtMobile);
            return false;
        }

        if (mobile.length() != 10) {
            utils.showErrorMsg(edtName,null);
            utils.showErrorMsg(edtMobile,"Enter valid number");
            utils.yoyoAnimation(edtMobile);
            return false;
        }

        if (!utils.isStringValidate(email_id)) {
            utils.showErrorMsg(edtMobile,null);
            utils.showErrorMsg(edtEmailId,"Enter email id");
            utils.yoyoAnimation(edtEmailId);
            return false;
        }

        if(!utils.isEmailValidate(email_id)){
            utils.showErrorMsg(edtMobile,null);
            utils.showErrorMsg(edtEmailId,"Enter valid email id");
            utils.yoyoAnimation(edtEmailId);
            return false;
        }

        if (password.length() < 6){
            utils.showErrorMsg(edtEmailId,null);
            Toast.makeText(this, "Password length must be more than six letters for security purpose", Toast.LENGTH_LONG).show();
            utils.yoyoAnimation(edtPassword);
            return false;
        }

        if (!utils.isStringValidate(password)) {
            utils.showErrorMsg(edtEmailId,null);
            utils.showErrorMsg(edtPassword,"Enter password");
            utils.yoyoAnimation(edtPassword);
            return false;
        }



        return true;
    }

    private void initialization() {
        //Utils & Model

        user = new User();
        gson = new Gson();
        //EditText
        edtEmailId = findViewById(R.id.edt_email_id);
        edtPassword = findViewById(R.id.edt_password);
        edtName = findViewById(R.id.edt_name);
        edtMobile = findViewById(R.id.edt_mobile);

        //Buttons
        btnRegister = findViewById(R.id.btn_next);

    }
}