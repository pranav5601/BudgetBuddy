package com.example.budgetbuddy.activities;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.model.SecQuestion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActForgotPass extends ActBase {
    FirebaseDatabase mDatabase;
    DatabaseReference fgDatabaseRef;
    LinearLayout ansLayout, emailLayout;
    TextInputLayout edtAnswer, edtFgEmail;
    Button btnNext, btnDone;
    FirebaseAuth mAuth;
    SecQuestion secQuestion;
    String mainEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_forgot_pass);
        initialization();
        initListeners();
    }

    private void initListeners() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainEmail = edtFgEmail.getEditText().getText().toString();
                final String tempEmail = mainEmail.replace(".", "");
                if (emailValidation(mainEmail)) {
                    showLoader();
                    fgDatabaseRef.child("sec_question").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child(tempEmail).exists()) {
                                if (!mainEmail.isEmpty()) {
                                    emailLayout.setVisibility(View.GONE);
                                    ansLayout.setVisibility(View.VISIBLE);
                                    secQuestion = snapshot.child(tempEmail).getValue(SecQuestion.class);
                                    Log.e("secQuestion", gson.toJson(secQuestion));
                                    closeLoader();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("error_fg", error.getMessage());
                            closeLoader();
                        }
                    });
                }
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answer = edtAnswer.getEditText().getText().toString();
                if (answerValidation(answer)) {
                    if (answer.equals(secQuestion.getAnswer())) {
                        mAuth.sendPasswordResetEmail(mainEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ActForgotPass.this, "A password reset link has sent to your email account. ", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("error_fg", e.getMessage());
                            }
                        });
                    }
                }
            }
        });
    }

    private boolean answerValidation(String answer) {
        if (!utils.isStringValidate(answer)) {
            utils.showErrorMsg(edtAnswer, "Enter answer");
            utils.yoyoAnimation(edtAnswer);
            return false;
        }
        return true;
    }

    private void initialization() {
        //firebase
        mDatabase = FirebaseDatabase.getInstance();
        fgDatabaseRef = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        //layout
        ansLayout = findViewById(R.id.ans_layout);
        emailLayout = findViewById(R.id.enter_email_layout);
        edtAnswer = findViewById(R.id.edt_fg_ans);
        edtFgEmail = findViewById(R.id.edt_fg_email_id);
        btnDone = findViewById(R.id.btn_fg_done);
        btnNext = findViewById(R.id.btn_fg_next);
        //utils
    }

    private boolean emailValidation(String email) {
        if (!utils.isStringValidate(email)) {
            edtFgEmail.setError("Enter your email");
            utils.yoyoAnimation(edtFgEmail);
            return false;
        }
        if (!utils.isEmailValidate(email)) {
            edtFgEmail.setError("Enter valid email id");
            utils.yoyoAnimation(edtFgEmail);
            return false;
        }
        return true;
    }
}