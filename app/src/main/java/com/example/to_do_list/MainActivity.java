package com.example.to_do_list;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muddzdev.styleabletoast.StyleableToast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private FragmentManager fragmentManager;
    private ToDoListFragment toDoListFragment;
    private CalendarFragment calendarFragment;
    private SettingFragment settingFragment;
    private MeowBottomNavigation bottomNavigationView;
    private long backPressTime;
    private Toolbar toolbar;
    FragmentTransaction transaction;
    dataBaseHelper dataBaseHelper;
    DrawerLayout drawer;
    Boolean ifShowToDoListToolBar = true;
    Boolean ifShowDate = false;
    Boolean ifShowPriority = false;
    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<String> mLists;
    List<String> mTags;
    List<String> headerTitle;
    List<List<String>> childTitle;
    static int[] icon = {R.drawable.ic_baseline_all_inbox_24,
            R.drawable.ic_baseline_list_24,
            R.drawable.ic_baseline_calendar_today_24,
            R.drawable.ic_baseline_error_outline_small,
            R.drawable.ic_baseline_label_24,
            R.drawable.ic_baseline_settings_24};
    ProgressDialog progressDialog;
    private int clickedExpandListGroup = 0;
    private int clickedExpandListChild = 0;
    public static final String channelId = "channelId";
    public static final String channelName = "ToDoList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_To_Do_List_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataBaseHelper = new dataBaseHelper(MainActivity.this);
        progressDialog = new ProgressDialog(MainActivity.this);

        loadLists();
        loadTags();
        setUpData();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        drawer = findViewById(R.id.drawer_layout);
        expandableListView = findViewById(R.id.expandableList);
        expandableListAdapter = new ExpandableListAdapter(MainActivity.this,this, headerTitle, childTitle);
        expandableListView.setAdapter(expandableListAdapter);



        toolbar =(Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Inbox");

        NavigationView navigationView =(NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if( newState == DrawerLayout.STATE_DRAGGING && drawer.isDrawerOpen(GravityCompat.START) == false ) {
                    expandableListAdapter.updateCounter();
                }
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                int index = parent.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition,childPosition));
                parent.setItemChecked(index, true);
                clickedExpandListGroup = groupPosition;
                clickedExpandListChild = childPosition;
                goToListFragment(clickedExpandListGroup, clickedExpandListChild, childTitle.get(groupPosition).get(childPosition));
                if (drawer.isDrawerOpen(GravityCompat.START))
                    drawer.closeDrawer(GravityCompat.START);
                toolbar.setTitle(childTitle.get(groupPosition).get(childPosition));
                return true;
            }
        });
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if(groupPosition == 5){
                    Intent intent = new Intent(MainActivity.this,ManageListsAndTagsActivity.class);
                    intent.putStringArrayListExtra("mLists", (ArrayList<String>) mLists);
                    intent.putStringArrayListExtra("mTags", (ArrayList<String>) mTags);
                    startActivityForResult(intent,7);
                }
                if(groupPosition == 0){
                    clickedExpandListGroup = 0;
                    clickedExpandListChild = 0;
                    goToListFragment(0, 0, "Inbox");
                    if (drawer.isDrawerOpen(GravityCompat.START))
                        drawer.closeDrawer(GravityCompat.START);
                    toolbar.setTitle("Inbox");
                }
                return false;
            }
        });

        bottomNavigationView.add(new MeowBottomNavigation.Model(1,R.drawable.ic_baseline_playlist_add_check_24));
        bottomNavigationView.add(new MeowBottomNavigation.Model(2,R.drawable.ic_baseline_calendar_today_24));
        bottomNavigationView.show(1,true);
        bottomNavigationView.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()){
                    case 1:
                        goToListFragment(0, 0, "Inbox");
                        break;
                    case 2:
                        goToCalendarFragment();
                        break;
                }
                return null;
            }
        });
        bottomNavigationView.setOnReselectListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                return null;
            }
        });

        goToListFragment(0, 0, "Inbox");
        if(savedInstanceState != null){
            ifShowToDoListToolBar = savedInstanceState.getBoolean("ifToolbarShow");
            if(ifShowToDoListToolBar == false){
                getSupportActionBar().hide();
            }
        }

        createNotificationChannel();
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Remind");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void goToListFragment(int group, int child, String clickedItem) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("mLists", (ArrayList<String>) mLists);
        bundle.putStringArrayList("mTags", (ArrayList<String>) mTags);
        bundle.putInt("group", group);
        bundle.putInt("child", child);
        bundle.putString("clickedItem", clickedItem);

        toDoListFragment = new ToDoListFragment();
        toDoListFragment.setArguments(bundle);
        fragmentManager = getSupportFragmentManager();
        drawer.setDrawerLockMode(drawer.LOCK_MODE_UNLOCKED);
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container,toDoListFragment);
        transaction.commit();
        setSupportActionBar(toolbar);
        toolbar.setTitle("Inbox");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandableListAdapter.updateCounter();
                if (drawer.isDrawerOpen(GravityCompat.START))
                    drawer.closeDrawer(GravityCompat.START);
                else
                    drawer.openDrawer((int) Gravity.START);
            }
        });
        getSupportActionBar().show();
        ifShowToDoListToolBar = true;
    }
    private void goToCalendarFragment(){
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog_view);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("mLists", (ArrayList<String>) mLists);
                bundle.putStringArrayList("mTags", (ArrayList<String>) mTags);
                calendarFragment = new CalendarFragment();
                calendarFragment.setArguments(bundle);
                fragmentManager = getSupportFragmentManager();
                drawer.setDrawerLockMode(drawer.LOCK_MODE_LOCKED_CLOSED);
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container,calendarFragment,"calendarFragment");
                transaction.commit();
                if(ifShowToDoListToolBar == true){
                    getSupportActionBar().hide();
                    ifShowToDoListToolBar = false;
                }
            }
        }, 50);
        checkIfInCalendarFragment();
    }

    private void checkIfInCalendarFragment(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.v("thomas","test");
                Fragment frag = getSupportFragmentManager().findFragmentByTag("calendarFragment");
                if (frag instanceof CalendarFragment) {
                    progressDialog.dismiss();
                    Log.v("thomas","dismiss");
                } else {
                    checkIfInCalendarFragment();
                }
            }
        }, 500);
    }

    private void goToSettingFragment(){
        settingFragment = new SettingFragment();
        fragmentManager = getSupportFragmentManager();
        drawer.setDrawerLockMode(drawer.LOCK_MODE_LOCKED_CLOSED);
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container,settingFragment);
        transaction.commit();
        getSupportActionBar().hide();
        ifShowToDoListToolBar = false;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("ifToolbarShow",ifShowToDoListToolBar);
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            if (backPressTime + 1000 > System.currentTimeMillis()) {
                super.onBackPressed();
            }else{
                StyleableToast.makeText(MainActivity.this,"Press again to exit",R.style.exitToast).show();
                backPressTime = System.currentTimeMillis();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2){
            if(resultCode == RESULT_OK){
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
                                    StyleableToast.makeText(MainActivity.this,"Setting a time in the past will not show the reminder!",R.style.cautiousToast).show();
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
            }
            recreate();
        }else if (requestCode == 7){
            recreate();
        }
    }

    private void startAlarm(Calendar c, int id,String title, String message){
        Log.d("thomas","startAlarm");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, AlertReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, id, intent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),pendingIntent);
    }

    private void cancelAlarm(int id){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);
        Log.v("thomas","canceled id = "+id);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_inbox:
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_date:
                if(ifShowDate == true){
                    hideNavDate();
                }else {
                    showNavDate();
                }
                ifShowDate = !ifShowDate;
                break;
            case R.id.nav_priority:
                if(ifShowPriority == true){
                    hideNavPriority();
                }else {
                    showNavPriority();
                }
                ifShowPriority = !ifShowPriority;
                break;
            case R.id.nav_tag:
                break;
            case R.id.nav_date_today:
                break;
        }
        return true;
    }

    private void hideNavDate(){
        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        nav.getMenu().findItem(R.id.nav_date_today).setVisible(false);
        nav.getMenu().findItem(R.id.nav_date_tomorrow).setVisible(false);
        nav.getMenu().findItem(R.id.nav_date_next_7_days).setVisible(false);
        nav.getMenu().findItem(R.id.nav_date_just_do_it_later).setVisible(false);
        nav.getMenu().findItem(R.id.nav_date_overdue).setVisible(false);
        nav.getMenu().findItem(R.id.nav_date).setActionView(R.layout.arrow_left_icon_in_drawer_item);
    }
    private void showNavDate(){
        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        nav.getMenu().findItem(R.id.nav_date_today).setVisible(true);
        nav.getMenu().findItem(R.id.nav_date_tomorrow).setVisible(true);
        nav.getMenu().findItem(R.id.nav_date_next_7_days).setVisible(true);
        nav.getMenu().findItem(R.id.nav_date_just_do_it_later).setVisible(true);
        nav.getMenu().findItem(R.id.nav_date_overdue).setVisible(true);
        nav.getMenu().findItem(R.id.nav_date).setActionView(R.layout.arrow_down_icon_in_drawer_item);
    }

    private void hideNavPriority(){
        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        nav.getMenu().findItem(R.id.nav_priority_high).setVisible(false);
        nav.getMenu().findItem(R.id.nav_priority_medium).setVisible(false);
        nav.getMenu().findItem(R.id.nav_priority_low).setVisible(false);
        nav.getMenu().findItem(R.id.nav_priority_none).setVisible(false);
        nav.getMenu().findItem(R.id.nav_priority).setActionView(R.layout.arrow_left_icon_in_drawer_item);
    }
    private void showNavPriority(){
        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        nav.getMenu().findItem(R.id.nav_priority_high).setVisible(true);
        nav.getMenu().findItem(R.id.nav_priority_medium).setVisible(true);
        nav.getMenu().findItem(R.id.nav_priority_low).setVisible(true);
        nav.getMenu().findItem(R.id.nav_priority_none).setVisible(true);
        nav.getMenu().findItem(R.id.nav_priority).setActionView(R.layout.arrow_down_icon_in_drawer_item);
    }

    private void loadLists(){
        SharedPreferences sharedPreferences = getSharedPreferences("mLists",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("lists", null);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        mLists = gson.fromJson(json, type);

        if(mLists == null){
            mLists = new ArrayList<>();
        }else {
            Log.v("thomas", String.valueOf(mLists));
        }
    }

    private void loadTags() {
        SharedPreferences sharedPreferences = getSharedPreferences("mTags",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("tags", null);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        mTags = gson.fromJson(json, type);

        if(mTags == null){
            mTags = new ArrayList<>();
        }else {
            Log.v("thomas", String.valueOf(mTags));
        }
    }

    private void setUpData(){
        headerTitle = new ArrayList<>();
        childTitle = new ArrayList<List<String>>();
        headerTitle.add("Inbox");
        headerTitle.add("List");
        headerTitle.add("Date");
        headerTitle.add("Priority");
        headerTitle.add("Tags");
        headerTitle.add("Manage Lists & Tags");
        for(int i=0;i<6;i++){
            childTitle.add(new ArrayList<String>());
        }
        for(String list : mLists){
           childTitle.get(1).add(list);
        }
        for(String tag : mTags){
            childTitle.get(4).add(tag);
        }
        childTitle.get(2).add("Today");
        childTitle.get(2).add("Tomorrow");
        childTitle.get(2).add("Next 7 days");
        childTitle.get(2).add("Just do it later");
        childTitle.get(2).add("Overdue");
        childTitle.get(3).add("High Priority");
        childTitle.get(3).add("Medium Priority");
        childTitle.get(3).add("Low Priority");
        childTitle.get(3).add("No Priority");
    }
}
