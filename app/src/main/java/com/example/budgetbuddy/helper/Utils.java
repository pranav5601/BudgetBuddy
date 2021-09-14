package com.example.budgetbuddy.helper;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.budgetbuddy.R;
import com.google.android.material.textfield.TextInputLayout;
public class Utils {
    public Boolean isStringValidate(String text) {
        return !(text == null || text.isEmpty() || text.equals("null"));
    }
    public void yoyoAnimation(View view) {
        YoYo.with(Techniques.Shake).duration(700).playOn(view);
    }
    public boolean isEmailValidate(String email_id){
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return email_id.matches(EMAIL_PATTERN);
    }
    public void initHomeFrag(FragmentManager manager, Fragment fragment, int frameId) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(frameId, fragment, fragment.getClass().getSimpleName());
        transaction.commit();
    }
    public void addFragmentToActivity(FragmentManager manager, Fragment fragment, int frameId) {
        FragmentTransaction transaction = manager.beginTransaction();
        Log.e("name", fragment.getClass().getSimpleName());
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.add(frameId, fragment);
        transaction.commit();
    }
    public void showErrorMsg(TextInputLayout edtView, String msg){
        edtView.setError(msg);
    }
}