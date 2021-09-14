package com.example.budgetbuddy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pixplicity.easyprefs.library.Prefs;

public class FragChangePass extends FragBase {
    private TextInputLayout edtOldPass, edtNewPass, edtReEnterPass;
    private Button btnChange;
    User user;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference changePassRef;
    private LinearLayout lytNewPass;

    @Override
    int getResourceLayout() {
        return R.layout.frag_change_pass;
    }

    @Override
    void setUpView() {
        init();
        clickListeners();
    }

    private void clickListeners() {
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPass = edtOldPass.getEditText().getText().toString();
                final String newPass = edtNewPass.getEditText().getText().toString();
                String rePass = edtReEnterPass.getEditText().getText().toString();
                if (btnChange.getText().equals(getResources().getString(R.string.next))) {
                    if (checkOldPass(oldPass)) {
                        btnChange.setText(R.string.change);
                        edtOldPass.setVisibility(View.GONE);
                        lytNewPass.setVisibility(View.VISIBLE);
                    }
                } else if (btnChange.getText().equals(getResources().getString(R.string.change))) {
                    if (checkNewPass(newPass, rePass)) {
                        showLoader("FragChangePass");
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            firebaseUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(baseContext, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                    if (getFragmentManager() != null) {
                                        getFragmentManager().popBackStack();
                                        user.setUser_password(newPass);
                                        Prefs.putString(getResources().getString(R.string.user), gson.toJson(user));
                                        closeLoader();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    closeLoader();
                                    Log.e("error_pass_change", e.getMessage());
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private boolean checkNewPass(String newPass, String rePass) {
        if (!utils.isStringValidate(newPass)) {
            utils.yoyoAnimation(edtNewPass);
            utils.showErrorMsg(edtNewPass, "Enter new password");
            return false;
        }
        if (!utils.isStringValidate(rePass)) {
            utils.yoyoAnimation(edtReEnterPass);
            utils.showErrorMsg(edtReEnterPass, "Re-Enter new password");
            return false;
        }
        if (!newPass.equals(rePass)) {
            utils.yoyoAnimation(edtOldPass);
            utils.showErrorMsg(edtOldPass, "Password is wrong");
            return false;
        }
        return true;
    }

    private void init() {
        //Layout
        edtNewPass = getFragView().findViewById(R.id.edt_new_pass);
        edtOldPass = getFragView().findViewById(R.id.edt_old_pass);
        edtReEnterPass = getFragView().findViewById(R.id.edt_verify_pass);
        btnChange = getFragView().findViewById(R.id.btn_change);
        btnChange.setText(getResources().getString(R.string.next));
        lytNewPass = getFragView().findViewById(R.id.lyt_new_pass);
        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        changePassRef = mDatabase.getReference();
        //model class
        user = getUser();
    }

    private boolean checkOldPass(String oldPass) {
        if (!utils.isStringValidate(oldPass)) {
            utils.yoyoAnimation(edtOldPass);
            utils.showErrorMsg(edtOldPass, "Enter password");
            return false;
        }
        if (!oldPass.equals(getUser().getUser_password())) {
            utils.yoyoAnimation(edtOldPass);
            utils.showErrorMsg(edtOldPass, "Password is wrong");
            return false;
        }
        return true;
    }
}