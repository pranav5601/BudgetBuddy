package com.example.budgetbuddy.fragments;
import android.widget.TextView;
import com.example.budgetbuddy.R;
public class FragMyProfile extends FragBase {
    TextView txtUserName, txtEmailID, txtMobile;
    @Override
    int getResourceLayout() {
        return R.layout.frag_my_profile;
    }
    @Override
    void setUpView() {
        init();
        setValue();
    }
    private void setValue() {
        txtUserName.setText(getUser().getUser_name());
        txtMobile.setText(getUser().getUser_mobile());
        txtEmailID.setText(getUser().getUser_email_id());
    }
    private void init() {
        txtUserName = getFragView().findViewById(R.id.txt_disp_user_name);
        txtEmailID = getFragView().findViewById(R.id.txt_disp_user_email);
        txtMobile = getFragView().findViewById(R.id.txt_disp_user_phone);
    }
}