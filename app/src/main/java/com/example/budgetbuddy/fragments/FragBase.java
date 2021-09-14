package com.example.budgetbuddy.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.helper.Loader;
import com.example.budgetbuddy.helper.Utils;
import com.example.budgetbuddy.model.User;
import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;

abstract class FragBase extends Fragment {

    public View view;
    Context baseContext;
    Loader loader;
    public Gson gson = new Gson();
    public Utils utils = new Utils();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(getResourceLayout(), container, false);
        this.view = view;
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        baseContext = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpView();
    }

    public void showLoader(String name){
        if(loader == null)
            loader = new Loader(baseContext);
        Log.e("loader_from",name);
        loader.show();
    }

    public void closeLoader(){
        if(loader == null)
            loader = new Loader(baseContext);
        loader.dismiss();
    }

    public View getFragView(){
        return view;
    }

    public String getUserID(){
        return Prefs.getString(getResources().getString(R.string.user_id),"");
    }

    public User getUser(){
        return gson.fromJson(Prefs.getString(getResources().getString(R.string.user),""),User.class);
    }

    abstract int getResourceLayout();
    abstract void setUpView();
}
