package com.example.budgetbuddy.activities;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.helper.Loader;
import com.example.budgetbuddy.helper.Utils;
import com.example.budgetbuddy.model.User;
import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;

public class ActBase extends AppCompatActivity {
    Loader loader;
    public Utils utils = new Utils();
    public Gson gson = new Gson();

    Activity getActivity() {
        return this;
    }

    public String getUserId() {
        return Prefs.getString(getResources().getString(R.string.user_id), "");
    }

    public User getUser() {
        return gson.fromJson(Prefs.getString(getResources().getString(R.string.user), ""), User.class);
    }

    public void showLoader() {
        if (loader == null)
            loader = new Loader(getActivity());
        loader.show();
    }

    public void closeLoader() {
        loader.dismiss();
    }
}