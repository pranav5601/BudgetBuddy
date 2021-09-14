package com.example.budgetbuddy.fragments;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.budgetbuddy.R;
import com.example.budgetbuddy.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pixplicity.easyprefs.library.Prefs;
public class FragSetting extends FragBase {
    TextView txtUserName, btnMyProfile, btnChangePass, btnChangeExp, btnReminder;
    User user;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    DatabaseReference changePassRef;
    @Override
    int getResourceLayout() {
        return R.layout.frag_setting;
    }
    @Override
    void setUpView() {
        init();
        clickListeners();
    }
    private void clickListeners() {
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                utils.addFragmentToActivity(getFragmentManager(), new FragChangePass(), R.id.fragMainContainer);
            }
        });
        btnChangeExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.addFragmentToActivity(getFragmentManager(), new FragChangeExpExpenses(), R.id.fragMainContainer);
            }
        });
        btnReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.addFragmentToActivity(getFragmentManager(),new FragReminder(),R.id.fragMainContainer);
            }
        });
        btnMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utils.addFragmentToActivity(getFragmentManager(),new FragMyProfile(),R.id.fragMainContainer);
            }
        });
    }
    private void init() {
        //model class
//        user = getUser();
        //layout
        txtUserName = getFragView().findViewById(R.id.txt_user_name);
        txtUserName.setText("Welcome " + getUser().getUser_name());
        btnChangePass = getFragView().findViewById(R.id.btn_change_pass);
        btnChangeExp = getFragView().findViewById(R.id.btn_change_exp);
        btnMyProfile = getFragView().findViewById(R.id.btn_my_profile);
        btnReminder = getFragView().findViewById(R.id.btn_reminder);
        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        changePassRef = mDatabase.getReference();
    }
}