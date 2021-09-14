package com.example.budgetbuddy.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.budgetbuddy.R;
import com.example.budgetbuddy.adapter.AdapterMenu;
import com.example.budgetbuddy.fragments.FragAddExpenses;
import com.example.budgetbuddy.fragments.FragMain;
import com.example.budgetbuddy.fragments.FragSetting;
import com.example.budgetbuddy.fragments.FragSuggestion;
import com.example.budgetbuddy.fragments.FragTally;
import com.example.budgetbuddy.helper.Utils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.pixplicity.easyprefs.library.Prefs;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

public class ActMain extends ActBase implements AdapterMenu.OnRcvListener {


    Utils utils;
    MenuDrawer drawer;
    ImageView imgMenu, imgAdd;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    Fragment fragReminder;
    AppBarLayout mAppbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        initialization();
        initHomeFragment();
        initMenu();
        clickListeners();
    }


    private void initialization() {
        //helper
        utils = new Utils();
        //view
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        imgMenu = findViewById(R.id.imgMainMenu);
        imgAdd = findViewById(R.id.imgAdd);
        mAppbar = findViewById(R.id.appbarLayout);


        //Firebase
        mAuth = FirebaseAuth.getInstance();

        //fragment



    }

    private void initHomeFragment() {

        utils.initHomeFrag(getSupportFragmentManager(), new FragMain(), R.id.fragMainContainer);

    }

    private void initMenu() {
        drawer = MenuDrawer.attach(this, MenuDrawer.Type.OVERLAY, Position.LEFT);
        drawer.setContentView(R.layout.act_main);
        drawer.setMenuView(R.layout.menu_drawer);
        drawer.setMinimumWidth(100);

        String[] arrMenu = getResources().getStringArray(R.array.arrMenu);
        RecyclerView rcvMenu = findViewById(R.id.rcvMenu);
        rcvMenu.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcvMenu.setAdapter(new AdapterMenu(this, arrMenu, this));


    }

    private void clickListeners() {


        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openMenu();
                Toast.makeText(ActMain.this, "Open Menu", Toast.LENGTH_SHORT).show();
            }
        });

       /* imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ActMain.this, "Add", Toast.LENGTH_SHORT).show();
            }
        });*/
    }



    @Override
    public void onMenuClick(int position) {
        switch (position) {
            case 0:
                clearFragments();
                drawer.closeMenu();
//                changeFragment(getSupportFragmentManager(), new FragMain(), R.id.fragMainContainer);
//                Toast.makeText(this, getResources().getStringArray(R.array.arrMenu)[position], Toast.LENGTH_SHORT).show();
                break;

            case 1:
                changeFragment(getSupportFragmentManager(), new FragTally(), R.id.fragMainContainer);
//                Toast.makeText(this, getResources().getStringArray(R.array.arrMenu)[position], Toast.LENGTH_SHORT).show();
                break;
            case 2:
                changeFragment(getSupportFragmentManager(), new FragAddExpenses(), R.id.fragMainContainer);
//                Toast.makeText(this, getResources().getStringArray(R.array.arrMenu)[position], Toast.LENGTH_SHORT).show();
                break;

            case 3:
                changeFragment(getSupportFragmentManager(), new FragSuggestion(), R.id.fragMainContainer);
                Toast.makeText(this, getResources().getStringArray(R.array.arrMenu)[position], Toast.LENGTH_SHORT).show();
                break;
            case 4:
                changeFragment(getSupportFragmentManager(), new FragSetting(), R.id.fragMainContainer);
//                Toast.makeText(this, getResources().getStringArray(R.array.arrMenu)[position], Toast.LENGTH_SHORT).show();
                break;
            case 5:
                callLogout();

        }
    }

    private void callLogout() {
        Intent loginIntent = new Intent(ActMain.this, ActLogin.class);
        Prefs.clear();
        mAuth.signOut();
        startActivity(loginIntent);
        finish();
    }


    private void changeFragment(FragmentManager supportFragmentManager, Fragment fragment, int containerUser) {
//        if (!checkFragmentAvailableOrNot(fragment.javaClass.simpleName)) {
//
//        }
        clearFragments();
        utils.addFragmentToActivity(supportFragmentManager, fragment, containerUser);
        drawer.closeMenu();
    }
    private void clearFragments() {

        for (int i = 0; i <= getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }
    }
}