package com.example.budgetbuddy;

import android.app.Application;
import android.content.ContextWrapper;

import com.google.firebase.database.FirebaseDatabase;
import com.pixplicity.easyprefs.library.Prefs;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }
}
