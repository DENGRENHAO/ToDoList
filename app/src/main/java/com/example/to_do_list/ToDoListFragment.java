package com.example.to_do_list;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;


public class ToDoListFragment extends Fragment {
    private View view;
    private RecyclerView myRecyclerView;
    private ArrayList<Model> Data = new ArrayList<>();
    private MyAdapter adapter;
    private ArrayList<String> mLists;
    private ArrayList<String> mTags;
    private int groupPosition;
    private int childPosition;
    private String clickedItem;
    static ImageView noWorkImage;
    static TextView noWorkText;
    Toolbar toolbar;
    FloatingActionButton fab;
    dataBaseHelper dataBaseHelper;
    SearchView searchView;
    private int yesterdayYear,yesterdayMonth,yesterdayDay,todayYear,todayMonth,todayDay,tomorrowDay,tomorrowMonth,tomorrowYear,nextWeekDay,nextWeekMonth,nextWeekYear;
    private String yesterdayDate,todayDate,tomorrowDate,nextWeekDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_to_do_list, container, false);

        dataBaseHelper = new dataBaseHelper(getActivity());

        Bundle bundle = getArguments();
        if(bundle != null){
            mLists = bundle.getStringArrayList("mLists");
            mTags = bundle.getStringArrayList("mTags");
            groupPosition = bundle.getInt("group");
            childPosition = bundle.getInt("child");
            clickedItem = bundle.getString("clickedItem");
        }else {
            mLists = new ArrayList<>();
            mTags = new ArrayList<>();
            groupPosition = 0;
            childPosition = 0;
            clickedItem = "Inbox";
        }

        noWorkImage = view.findViewById(R.id.noWorkImage);
        noWorkText = view.findViewById(R.id.noWorkText);
        myRecyclerView = (RecyclerView) view.findViewById(R.id.myRecyclerView);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycler_line));
        myRecyclerView.addItemDecoration(divider);

        adapter = new MyAdapter(getActivity(),view.getContext(), Data , mLists , mTags);
        myRecyclerView.setAdapter(adapter);

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),FormInCalendar.class);
                intent.putStringArrayListExtra("mLists", mLists);
                intent.putStringArrayListExtra("mTags", mTags);
                startActivityForResult(intent,1);
            }
        });

        ((CoordinatorLayout.LayoutParams) fab.getLayoutParams()).gravity = Gravity.BOTTOM | Gravity.END;
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)fab.getLayoutParams();
        params.setMargins(0, 0, 60, 200);
        fab.setLayoutParams(params);

        myRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(!recyclerView.canScrollVertically(1) && !recyclerView.canScrollVertically(-1)){
                    fabMoveToBottom();
                }else if (!recyclerView.canScrollVertically(1)) {
                    fabMoveToTop();
                }else {
                    fabMoveToBottom();
                }
            }
        });

        toolbar =(Toolbar) view.findViewById(R.id.toolBar);
        setHasOptionsMenu(true);

        Cursor cursor;
        if (groupPosition == 2){
            Calendar calendar = Calendar.getInstance();
            todayDay = calendar.get(Calendar.DAY_OF_MONTH);
            todayMonth = calendar.get(Calendar.MONTH) + 1;
            todayYear = calendar.get(Calendar.YEAR);
            calendar.add(Calendar.DAY_OF_MONTH,1);
            tomorrowDay = calendar.get(Calendar.DAY_OF_MONTH);
            tomorrowMonth = calendar.get(Calendar.MONTH) + 1;
            tomorrowYear = calendar.get(Calendar.YEAR);
            calendar.add(Calendar.DAY_OF_MONTH,5);
            nextWeekDay = calendar.get(Calendar.DAY_OF_MONTH);
            nextWeekMonth = calendar.get(Calendar.MONTH) + 1;
            nextWeekYear = calendar.get(Calendar.YEAR);
            calendar.add(Calendar.DAY_OF_MONTH,-7);
            yesterdayDay = calendar.get(Calendar.DAY_OF_MONTH);
            yesterdayMonth = calendar.get(Calendar.MONTH) + 1;
            yesterdayYear = calendar.get(Calendar.YEAR);

            if (todayMonth < 10 && todayDay < 10){
                todayDate = todayYear + "-0" + todayMonth + "-0" + todayDay;
            }else if(todayMonth >= 10 && todayDay < 10){
                todayDate = todayYear + "-" + todayMonth + "-0" + todayDay;
            }else if(todayMonth < 10 && todayDay >= 10){
                todayDate = todayYear + "-0" + todayMonth + "-" + todayDay;
            }else {
                todayDate = todayYear + "-" + todayMonth + "-" + todayDay;
            }

            if (tomorrowMonth < 10 && tomorrowDay < 10){
                tomorrowDate = tomorrowYear + "-0" + tomorrowMonth + "-0" + tomorrowDay;
            }else if(tomorrowMonth >= 10 && tomorrowDay < 10){
                tomorrowDate = tomorrowYear + "-" + tomorrowMonth + "-0" + tomorrowDay;
            }else if(tomorrowMonth < 10 && tomorrowDay >= 10){
                tomorrowDate = tomorrowYear + "-0" + tomorrowMonth + "-" + tomorrowDay;
            }else {
                tomorrowDate = tomorrowYear + "-" + tomorrowMonth + "-" + tomorrowDay;
            }
            if (nextWeekMonth < 10 && nextWeekDay < 10){
                nextWeekDate = nextWeekYear + "-0" + nextWeekMonth + "-0" + nextWeekDay;
            }else if(nextWeekMonth >= 10 && nextWeekDay < 10){
                nextWeekDate = nextWeekYear + "-" + nextWeekMonth + "-0" + nextWeekDay;
            }else if(nextWeekMonth < 10 && nextWeekDay >= 10){
                nextWeekDate = nextWeekYear + "-0" + nextWeekMonth + "-" + nextWeekDay;
            }else {
                nextWeekDate = nextWeekYear + "-" + nextWeekMonth + "-" + nextWeekDay;
            }
            if (yesterdayMonth < 10 && yesterdayDay < 10){
                yesterdayDate = yesterdayYear + "-0" + yesterdayMonth + "-0" + yesterdayDay;
            }else if(yesterdayMonth >= 10 && yesterdayDay < 10){
                yesterdayDate = yesterdayYear + "-" + yesterdayMonth + "-0" + yesterdayDay;
            }else if(yesterdayMonth < 10 && yesterdayDay >= 10){
                yesterdayDate = yesterdayYear + "-0" + yesterdayMonth + "-" + yesterdayDay;
            }else {
                yesterdayDate = yesterdayYear + "-" + yesterdayMonth + "-" + yesterdayDay;
            }

            cursor = dataBaseHelper.getAllBySort(groupPosition, childPosition, clickedItem, yesterdayDate, todayDate, tomorrowDate,nextWeekDate);
        }else {
            cursor = dataBaseHelper.getAllBySort(groupPosition, childPosition, clickedItem, null, null,null,null);
        }
        showListForFirstTime();

        if(cursor.getCount() != 0){
            hideNoWorkImage();
        }else {
            showNoWorkImage();
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.to_do_list_search_toolbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void showListForFirstTime(){
        Data.clear();
        Cursor cursor = dataBaseHelper.getAllBySort(groupPosition, childPosition, clickedItem, yesterdayDate, todayDate, tomorrowDate,nextWeekDate);
        if(cursor.getCount()!=0){
            hideNoWorkImage();
            while (cursor.moveToNext()){
                Model model;
                model = new Model(cursor.getInt(0),cursor.getString(1),
                        cursor.getString(2),cursor.getInt(3),cursor.getString(4),
                        cursor.getString(5),cursor.getInt(6),cursor.getInt(7),cursor.getString(8),
                        cursor.getString(9),cursor.getInt(10));
                Data.add(model);
                adapter.notifyDataSetChanged();
            }
        }else {
            showNoWorkImage();
        }
    }

//    void displayData(){
//        Cursor cursor = dataBaseHelper.getAllBySort(groupPosition, childPosition, clickedItem);
//        if(cursor.getCount()!=0){
//            hideNoWorkImage();
//            if (cursor.moveToLast()){
//                Model model;
//                model = new Model(cursor.getInt(0),cursor.getString(1),
//                        cursor.getString(2),cursor.getInt(3),cursor.getString(4),
//                        cursor.getString(5),cursor.getInt(6),cursor.getInt(7),cursor.getString(8),
//                        cursor.getString(9),cursor.getInt(10));
//                Data.add(model);
//                adapter.notifyDataSetChanged();
//            }
//        }else {
//            showNoWorkImage();
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                showListForFirstTime();
                setUpAlarm();
                myRecyclerView.getLayoutManager().smoothScrollToPosition(myRecyclerView,new RecyclerView.State(), myRecyclerView.getAdapter().getItemCount());
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
                        startAlarm(c, model.getId(),model.getTitle(),model.getDescription());
                    }else {
                        StyleableToast.makeText(getContext(),"Setting a time in the past will not show the reminder!",R.style.cautiousToast).show();
                    }
                }
            }
        }
    }

    private void startAlarm(Calendar c, int id,String title, String message){
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlertReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), id, intent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),pendingIntent);
    }

    static void showNoWorkImage(){
        noWorkImage.setVisibility(View.VISIBLE);
        noWorkText.setVisibility(View.VISIBLE);
    }

    static void hideNoWorkImage(){
        noWorkImage.setVisibility(View.GONE);
        noWorkText.setVisibility(View.GONE);
    }

    private void fabMoveToBottom(){
        ((CoordinatorLayout.LayoutParams) fab.getLayoutParams()).gravity = Gravity.BOTTOM | Gravity.END;
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)fab.getLayoutParams();
        params.setMargins(0, 0, 60, 200);
        fab.setLayoutParams(params);
    }

    private void fabMoveToTop(){
        ((CoordinatorLayout.LayoutParams) fab.getLayoutParams()).gravity = Gravity.TOP;
        ((CoordinatorLayout.LayoutParams) fab.getLayoutParams()).gravity = Gravity.END;
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)fab.getLayoutParams();
        params.setMargins(0, 230, 60, 0);
        fab.setLayoutParams(params);
    }

    public int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}