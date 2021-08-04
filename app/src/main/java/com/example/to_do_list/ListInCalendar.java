package com.example.to_do_list;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.ArrayList;
import java.util.Calendar;

public class ListInCalendar extends AppCompatActivity {
    private RecyclerView myRecyclerView;
    private ArrayList<Model> Data = new ArrayList<>();
    private CalendarListAdapter adapter;
    private String date;
    private ArrayList<String> mLists;
    private ArrayList<String> mTags;
    FloatingActionButton fab;
    dataBaseHelper dataBaseHelper;
    static private ImageView noWorkImage;
    static private TextView noWorkText;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_in_calendar);

        date = getIntent().getStringExtra("date");
        Log.v("thomas",date);
        mLists = getIntent().getStringArrayListExtra("mLists");
        mTags = getIntent().getStringArrayListExtra("mTags");

        toolbar = findViewById(R.id.toolBarInCalList);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(date);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        noWorkImage = findViewById(R.id.noWorkImageInCal);
        noWorkText = findViewById(R.id.noWorkTextInCal);
        dataBaseHelper = new dataBaseHelper(this);
        myRecyclerView = findViewById(R.id.myRecyclerViewInCal);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(ListInCalendar.this));
        DividerItemDecoration divider = new DividerItemDecoration(ListInCalendar.this,DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(ListInCalendar.this, R.drawable.recycler_line));
        myRecyclerView.addItemDecoration(divider);
        adapter = new CalendarListAdapter(ListInCalendar.this,this,Data,mLists,mTags);
        myRecyclerView.setAdapter(adapter);

        fab = (FloatingActionButton) findViewById(R.id.fabInCal);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListInCalendar.this,FormInCalendar.class);
                intent.putExtra("date",date);
                intent.putStringArrayListExtra("mLists", mLists);
                intent.putStringArrayListExtra("mTags", mTags);
                startActivityForResult(intent,3);
            }
        });

        showAllCalList();
    }

    private void showAllCalList() {
        Cursor cursor = dataBaseHelper.getAllInCal(date);
        if(cursor.getCount()!=0){
            while (cursor.moveToNext()){
                Model model = new Model(cursor.getInt(0),cursor.getString(1),
                        cursor.getString(2),cursor.getInt(3),cursor.getString(4),
                        cursor.getString(5),cursor.getInt(6),cursor.getInt(7),cursor.getString(8),
                        cursor.getString(9),cursor.getInt(10));
                Data.add(model);
                adapter.notifyDataSetChanged();
            }
            hideNoWorkImage();
        }else {
            showNoWorkImage();
        }
    }

    void displayDataInCal(){
        Cursor cursor = dataBaseHelper.getAll();
        if(cursor.getCount()!=0){
            if (cursor.moveToLast()){
                Model model = new Model(cursor.getInt(0),cursor.getString(1),
                        cursor.getString(2),cursor.getInt(3),cursor.getString(4),
                        cursor.getString(5),cursor.getInt(6),cursor.getInt(7),cursor.getString(8),
                        cursor.getString(9),cursor.getInt(10));
                if(model.getDate().equals(date)){
                    Data.add(model);
                    adapter.notifyDataSetChanged();
                }
            }
            hideNoWorkImage();
        }else {
            showNoWorkImage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 3){
            if(resultCode == RESULT_OK){
                displayDataInCal();
                setUpAlarm();
                myRecyclerView.getLayoutManager().smoothScrollToPosition(myRecyclerView,new RecyclerView.State(), myRecyclerView.getAdapter().getItemCount());
            }
        }else if(requestCode == 4){
            if (resultCode == RESULT_OK){
                int id = data.getIntExtra("id", -1);
                Cursor cursor = dataBaseHelper.getAll();
                if(cursor.getCount()!=0){
                    int temp;
                    while (cursor.moveToNext()){
                        temp = 0;
                        Model model;
                        model = new Model(cursor.getInt(0),cursor.getString(1),
                                cursor.getString(2),cursor.getInt(3),cursor.getString(4),
                                cursor.getString(5),cursor.getInt(6),cursor.getInt(7),cursor.getString(8),
                                cursor.getString(9),cursor.getInt(10));
                        if(model.getId() == id) {
                            cancelAlarm(id);
                            if (model.getIfRemind() == 1) {
                                int hour=0,min=0,year,month,day;

                                if(model.getIfAllDay() == 0){
                                    String[] time = model.getTime().split(":");
                                    hour = Integer.valueOf(time[0]);
                                    min = Integer.valueOf(time[1]);
                                }
                                String[] date = model.getDate().split("-");
                                year = Integer.valueOf(date[0]);
                                month = Integer.valueOf(date[1]);
                                day = Integer.valueOf(date[2]);
                                Calendar c = Calendar.getInstance();
                                if(model.getIfAllDay() == 1){
                                    c.set(Calendar.HOUR_OF_DAY, 0);
                                    c.set(Calendar.MINUTE, 0);
                                    c.set(Calendar.SECOND, 0);
                                    c.set(Calendar.YEAR, year);
                                    c.set(Calendar.MONTH, month-1);
                                    c.set(Calendar.DAY_OF_MONTH, day);
                                }else {
                                    switch (model.getRemindBefore()){
                                        case 0:
                                            c.set(Calendar.HOUR_OF_DAY, hour);
                                            c.set(Calendar.MINUTE, min);
                                            c.set(Calendar.SECOND, 0);
                                            c.set(Calendar.YEAR, year);
                                            c.set(Calendar.MONTH, month-1);
                                            c.set(Calendar.DAY_OF_MONTH, day);
                                            break;
                                        case 1:
                                            c.set(Calendar.HOUR_OF_DAY, hour);
                                            c.set(Calendar.MINUTE, min-5);
                                            c.set(Calendar.SECOND, 0);
                                            c.set(Calendar.YEAR, year);
                                            c.set(Calendar.MONTH, month-1);
                                            c.set(Calendar.DAY_OF_MONTH, day);
                                            break;
                                        case 2:
                                            c.set(Calendar.HOUR_OF_DAY, hour);
                                            c.set(Calendar.MINUTE, min-15);
                                            c.set(Calendar.SECOND, 0);
                                            c.set(Calendar.YEAR, year);
                                            c.set(Calendar.MONTH, month-1);
                                            c.set(Calendar.DAY_OF_MONTH, day);
                                            break;
                                        case 3:
                                            c.set(Calendar.HOUR_OF_DAY, hour);
                                            c.set(Calendar.MINUTE, min-30);
                                            c.set(Calendar.SECOND, 0);
                                            c.set(Calendar.YEAR, year);
                                            c.set(Calendar.MONTH, month-1);
                                            c.set(Calendar.DAY_OF_MONTH, day);
                                            break;
                                        case 4:
                                            c.set(Calendar.HOUR_OF_DAY, hour-1);
                                            c.set(Calendar.MINUTE, min);
                                            c.set(Calendar.SECOND, 0);
                                            c.set(Calendar.YEAR, year);
                                            c.set(Calendar.MONTH, month-1);
                                            c.set(Calendar.DAY_OF_MONTH, day);
                                            break;
                                        case 5:
                                            c.set(Calendar.HOUR_OF_DAY, hour-2);
                                            c.set(Calendar.MINUTE, min);
                                            c.set(Calendar.SECOND, 0);
                                            c.set(Calendar.YEAR, year);
                                            c.set(Calendar.MONTH, month-1);
                                            c.set(Calendar.DAY_OF_MONTH, day);
                                            break;
                                        case 6:
                                            c.set(Calendar.HOUR_OF_DAY, hour);
                                            c.set(Calendar.MINUTE, min);
                                            c.set(Calendar.SECOND, 0);
                                            c.set(Calendar.YEAR, year);
                                            c.set(Calendar.MONTH, month-1);
                                            c.set(Calendar.DAY_OF_MONTH, day-1);
                                            break;
                                        case 7:
                                            c.set(Calendar.HOUR_OF_DAY, hour);
                                            c.set(Calendar.MINUTE, min);
                                            c.set(Calendar.SECOND, 0);
                                            c.set(Calendar.YEAR, year);
                                            c.set(Calendar.MONTH, month-1);
                                            c.set(Calendar.DAY_OF_MONTH, day-2);
                                            break;
                                    }
                                }
                                if (!c.before(Calendar.getInstance())) {
                                    startAlarm(c, model.getId(),model.getTitle(),model.getDescription());
                                } else {
                                    Log.d("thomas", "calendar = " + c);
                                    StyleableToast.makeText(ListInCalendar.this,"Setting a date in the past will not show a reminder!",R.style.cautiousToast).show();
                                }
                            }
                            temp = 1;
                            break;
                        }
                        if(temp == 1){
                            break;
                        }
                    }
                }
                startActivity(getIntent());
                finish();
                overridePendingTransition(0, 0);
            }
        }
    }

    private void setUpAlarm(){
        Cursor cursor = dataBaseHelper.getAll();
        if(cursor.getCount()!=0){
            if (cursor.moveToLast()){
                Model model;
                model = new Model(cursor.getInt(0),cursor.getString(1),
                        cursor.getString(2),cursor.getInt(3),cursor.getString(4),
                        cursor.getString(5),cursor.getInt(6),cursor.getInt(7),cursor.getString(8),
                        cursor.getString(9),cursor.getInt(10));
                if(model.getIfRemind() == 1){
                    int hour=0,min=0,year,month,day;

                    if(model.getIfAllDay() == 0){
                        String[] time = model.getTime().split(":");
                        hour = Integer.valueOf(time[0]);
                        min = Integer.valueOf(time[1]);
                    }
                    String[] date = model.getDate().split("-");
                    year = Integer.valueOf(date[0]);
                    month = Integer.valueOf(date[1]);
                    day = Integer.valueOf(date[2]);
                    Calendar c = Calendar.getInstance();
                    if(model.getIfAllDay() == 1){
                        c.set(Calendar.HOUR_OF_DAY, 0);
                        c.set(Calendar.MINUTE, 0);
                        c.set(Calendar.SECOND, 0);
                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, month-1);
                        c.set(Calendar.DAY_OF_MONTH, day);
                    }else {
                        switch (model.getRemindBefore()){
                            case 0:
                                c.set(Calendar.HOUR_OF_DAY, hour);
                                c.set(Calendar.MINUTE, min);
                                c.set(Calendar.SECOND, 0);
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, month-1);
                                c.set(Calendar.DAY_OF_MONTH, day);
                                break;
                            case 1:
                                c.set(Calendar.HOUR_OF_DAY, hour);
                                c.set(Calendar.MINUTE, min-5);
                                c.set(Calendar.SECOND, 0);
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, month-1);
                                c.set(Calendar.DAY_OF_MONTH, day);
                                break;
                            case 2:
                                c.set(Calendar.HOUR_OF_DAY, hour);
                                c.set(Calendar.MINUTE, min-15);
                                c.set(Calendar.SECOND, 0);
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, month-1);
                                c.set(Calendar.DAY_OF_MONTH, day);
                                break;
                            case 3:
                                c.set(Calendar.HOUR_OF_DAY, hour);
                                c.set(Calendar.MINUTE, min-30);
                                c.set(Calendar.SECOND, 0);
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, month-1);
                                c.set(Calendar.DAY_OF_MONTH, day);
                                break;
                            case 4:
                                c.set(Calendar.HOUR_OF_DAY, hour-1);
                                c.set(Calendar.MINUTE, min);
                                c.set(Calendar.SECOND, 0);
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, month-1);
                                c.set(Calendar.DAY_OF_MONTH, day);
                                break;
                            case 5:
                                c.set(Calendar.HOUR_OF_DAY, hour-2);
                                c.set(Calendar.MINUTE, min);
                                c.set(Calendar.SECOND, 0);
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, month-1);
                                c.set(Calendar.DAY_OF_MONTH, day);
                                break;
                            case 6:
                                c.set(Calendar.HOUR_OF_DAY, hour);
                                c.set(Calendar.MINUTE, min);
                                c.set(Calendar.SECOND, 0);
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, month-1);
                                c.set(Calendar.DAY_OF_MONTH, day-1);
                                break;
                            case 7:
                                c.set(Calendar.HOUR_OF_DAY, hour);
                                c.set(Calendar.MINUTE, min);
                                c.set(Calendar.SECOND, 0);
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, month-1);
                                c.set(Calendar.DAY_OF_MONTH, day-2);
                                break;
                        }
                    }
                    if(!c.before(Calendar.getInstance())){
                        Log.d("thomas","instance="+Calendar.getInstance());
                        Log.d("thomas","c = "+c);
                        startAlarm(c, model.getId(),model.getTitle(),model.getDescription());
                    }else {
                        StyleableToast.makeText(ListInCalendar.this,"Setting a time in the past will not show the reminder!",R.style.cautiousToast).show();
                    }
                }
            }
        }
    }

    private void startAlarm(Calendar c, int id,String title, String message){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ListInCalendar.this, AlertReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ListInCalendar.this, id, intent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),pendingIntent);
    }

    private void cancelAlarm(int id){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ListInCalendar.this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ListInCalendar.this, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);
        Log.v("thomas","canceled id = "+id);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    static void showNoWorkImage(){
        noWorkImage.setVisibility(View.VISIBLE);
        noWorkText.setVisibility(View.VISIBLE);
    }

    static void hideNoWorkImage(){
        noWorkImage.setVisibility(View.GONE);
        noWorkText.setVisibility(View.GONE);
    }
}