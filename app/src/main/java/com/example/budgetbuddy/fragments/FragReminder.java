package com.example.budgetbuddy.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.budgetbuddy.helper.MyAlarm;
import com.example.budgetbuddy.R;
import com.pixplicity.easyprefs.library.Prefs;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class FragReminder extends FragBase {

    private Calendar mCalendar;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private Switch mRepeatSwitch;
    private String mTime;
    private LinearLayout lytReminder;
    private String mActive;
    private LinearLayout lytSelectTime;
    private ImageView switchReminder;

    private TextView txtTime;
    private Button btnSave;


    @Override
    int getResourceLayout() {
        return R.layout.frag_reminder;
    }

    @Override
    void setUpView() {

        init();
        clickListeners();

//        checkSwitchStatus();
        checkForData();

    }

    private void checkForData() {
        String strSwitchStatus = Prefs.getString("switch_status", "");
        String reminderTime = Prefs.getString("reminder_time", "");

        if (strSwitchStatus.equals("")) {
            switchReminder.setSelected(false);
            mActive = "false";
            lytReminder.setVisibility(View.GONE);
            btnSave.setEnabled(false);
        } else {
            mActive = strSwitchStatus;
            if (mActive.equals("true")) {
                lytReminder.setVisibility(View.VISIBLE);
                btnSave.setEnabled(true);
                switchReminder.setSelected(true);
            } else {
                lytReminder.setVisibility(View.GONE);
                btnSave.setEnabled(false);
                switchReminder.setSelected(false);
            }
        }

        if (!reminderTime.isEmpty()) {
            txtTime.setText("App will remind you every day at " + reminderTime);
        } else {
            txtTime.setText("App will remind you every day at 8:00 PM");
        }

        if (Prefs.getInt("hours", 0) == 0) {
            mHour = 20;
        }
    }

    private void checkSwitchStatus() {
        if (switchReminder.isSelected()) {
            mActive = "true";
            lytReminder.setVisibility(View.VISIBLE);

            mHour = 8;
            mMinute = 0;
            btnSave.setEnabled(true);
        } else {
            mActive = "false";
            cancelAlarm();
            lytReminder.setVisibility(View.GONE);
            btnSave.setEnabled(false);
        }
    }

    private void clickListeners() {
        lytSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setTime(view);
            }
        });


        switchReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchReminder.setSelected(!switchReminder.isSelected());


                checkSwitchStatus();

                Log.e("switch", String.valueOf(switchReminder.isSelected()));
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Prefs.putString("switch_status", mActive);
                Prefs.putString("reminder_time", mTime);
                Prefs.putInt("hours", mHour);
                Prefs.putInt("minute", mMinute);
                startAlarm();
            }
        });

    }

    private void init() {
        switchReminder = getFragView().findViewById(R.id.switch_reminder);
        txtTime = getFragView().findViewById(R.id.txt_time);
        lytSelectTime = getFragView().findViewById(R.id.lyt_select_time);
        btnSave = getFragView().findViewById(R.id.btnSave);
        lytReminder = getFragView().findViewById(R.id.lyt_reminder);
        //calender
        mCalendar = Calendar.getInstance();
    }

    public void setTime(View v) {

        Calendar now = Calendar.getInstance();

        int storedHr = Prefs.getInt("hours", 0);
        int storedMin = Prefs.getInt("minute", 0);
        if (storedHr != 0 && storedMin != 0) {
            mHour = storedHr;
            mMinute = storedMin;
        }

        TimePickerDialog tpd = TimePickerDialog.newInstance(
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;

                        if (hourOfDay > 12 && minute < 10) {
                            mTime = (hourOfDay - 12) + ":" + "0" + minute + " PM";
                        } else if (hourOfDay < 13 && minute < 10) {
                            mTime = hourOfDay + ":" + "0" + minute + " AM";
                        } else if (hourOfDay > 12) {
                            mTime = (hourOfDay - 12) + ":" + minute + " PM";
                        } else {
                            mTime = (hourOfDay) + ":" + minute + " AM";
                        }

                        txtTime.setText("App will remind you every day at " + mTime);

                    }
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tpd.setThemeDark(false);
        tpd.show(getActivity().getFragmentManager(), "Timepickerdialog");
    }


    private void startAlarm() {
        if (mHour != 0) {
            mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
            mCalendar.set(Calendar.MINUTE, mMinute);
            mCalendar.set(Calendar.SECOND, 0);
        } else {
            mCalendar.set(Calendar.HOUR_OF_DAY, 20);
            mCalendar.set(Calendar.MINUTE, 0);
            mCalendar.set(Calendar.SECOND, 0);
        }
        AlarmManager alarmManager = (AlarmManager) baseContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(baseContext, MyAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(baseContext, 1, intent, 0);
        if (mCalendar.before(Calendar.getInstance())) {
            mCalendar.add(Calendar.DATE, 1);
        }
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        Log.e("timeStamp", String.valueOf(mCalendar.get(Calendar.HOUR_OF_DAY)));
        alarmManager.setRepeating(AlarmManager.RTC, mCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(baseContext, "Saved!", Toast.LENGTH_SHORT).show();
    }

    public void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) baseContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(baseContext, MyAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(baseContext, 1, intent, 0);
        if (Prefs.contains("switch_status")) {
            Prefs.putString("switch_status", "");
        }
        alarmManager.cancel(pendingIntent);
        Toast.makeText(baseContext, "Reminder off", Toast.LENGTH_SHORT).show();
//        mTextView.setText("Alarm canceled");
    }
}