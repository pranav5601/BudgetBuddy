package com.example.budgetbuddy.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;

public class ActLogin extends ActBase {
    private TextInputLayout edtEmailId, edtPassword;
    private TextView btnForgetPass, btnCreateUser;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private String uid;
    private User user;
    private DatabaseReference loginDatabaseRef;
    private Gson gson;
    private ImageView imgMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        initialization();
        setSpannable();
        clickListeners();
    }

    private void setSpannable() {
        String s1 = "Don't have an account? Create an account.";
        SpannableString ss1 = new SpannableString(s1);
        ss1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), "Don't have an account? ".length() - 1, "Don't have an account? Create an account.".length() - 1, 0);
        ss1.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                startActivity(new Intent(ActLogin.this, ActSignUp.class));
            }
        }, "Don't have an account? ".length(), "Don't have an account? Create an account.".length(), 0);
        btnCreateUser.setText(ss1);
        btnCreateUser.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void clickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_id = edtEmailId.getEditText().getText().toString();
                final String password = edtPassword.getEditText().getText().toString();
                if (credentialValidation(email_id, password)) {
                    showLoader();
                    mAuth.signInWithEmailAndPassword(email_id, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            closeLoader();
                            if (task.isSuccessful()) {
                                final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                                if (current_user != null) {
                                    uid = current_user.getUid();
                                    loginDatabaseRef.child(uid).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            user = snapshot.child("user_details").getValue(User.class);
                                            Prefs.putString(getResources().getString(R.string.user_id), uid);
                                            Prefs.putString(getResources().getString(R.string.user), gson.toJson(user));
                                            Log.e("data", gson.toJson(user));
                                            if (!user.getUser_password().equals(password)) {
                                                resetPassInDatabase(password, user);
                                            }
                                            nextActivity();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("error_database", error.getMessage());
                                        }
                                    });
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            closeLoader();
                            Toast.makeText(ActLogin.this, "Email id or Password may wrong.", Toast.LENGTH_SHORT).show();
                            Log.e("error_login", e.getMessage());
                        }
                    });
                }
            }
        });
        btnForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActLogin.this, ActForgotPass.class));
            }
        });
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ActLogin.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetPassInDatabase(final String password, final User user) {
        loginDatabaseRef.child(uid).child("user_details").child("user_password").setValue(password).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                user.setUser_password(password);
                Prefs.putString(getResources().getString(R.string.user), gson.toJson(user));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("errorResetPass", e.getMessage());
            }
        });
    }

    private void nextActivity() {
        Intent mainIntent = new Intent(ActLogin.this, ActMain.class);
        startActivity(mainIntent);
        finish();
    }

    private void initialization() {
        //Layout
        edtEmailId = findViewById(R.id.edt_email_id);
        edtPassword = findViewById(R.id.edt_password);
        btnForgetPass = findViewById(R.id.btn_forget_pass);
        btnLogin = findViewById(R.id.btn_login);
        btnCreateUser = findViewById(R.id.btn_create_user);
        //temp
        imgMenu = findViewById(R.id.imgMenu);
        //Firebase
        mAuth = FirebaseAuth.getInstance();
        loginDatabaseRef = FirebaseDatabase.getInstance().getReference();
        //model
        user = new User();
        //gson
        gson = new Gson();
    }

    private boolean credentialValidation(String email_id, String password) {
        if (!utils.isStringValidate(email_id)) {
            utils.showErrorMsg(edtEmailId, "Enter email");
            utils.yoyoAnimation(edtEmailId);
            return false;
        }
        if (!utils.isStringValidate(password)) {
            utils.showErrorMsg(edtPassword, "Enter password");
            utils.yoyoAnimation(edtPassword);
            return false;
        }
        return true;
    }
}