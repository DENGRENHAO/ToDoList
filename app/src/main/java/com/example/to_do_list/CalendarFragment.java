package com.example.to_do_list;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class CalendarFragment extends Fragment {
    private View view;
    List<EventDay> events = new ArrayList<>();
    Calendar calendar = Calendar.getInstance();
    Calendar clickedDayCalendar;
    List<Calendar> calendars = new ArrayList<>();
    Toolbar toolbar;
    String[] calendarMonth;
    private ArrayList<String> mLists;
    private ArrayList<String> mTags;
    private CalendarView calendarView;
    private int todayYear,todayMonth,todayDay,previousSelectedYear,previousSelectedMonth,previousSelectedDay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendar, container, false);

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Bundle bundle = getArguments();
        if(bundle != null){
            mLists = bundle.getStringArrayList("mLists");
            mTags = bundle.getStringArrayList("mTags");
        }else {
            mLists = new ArrayList<>();
            mTags = new ArrayList<>();
        }

        calendarView = (CalendarView) view.findViewById(R.id.calendarView);

        setEventDays();

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                clickedDayCalendar = eventDay.getCalendar();
                calendars.add(clickedDayCalendar);
                previousSelectedYear = clickedDayCalendar.get(Calendar.YEAR);
                previousSelectedMonth = clickedDayCalendar.get(Calendar.MONTH);
                previousSelectedDay = clickedDayCalendar.get(Calendar.DAY_OF_MONTH);

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String str = df.format(clickedDayCalendar.getTime());
                Intent intent = new Intent(getActivity(),ListInCalendar.class);
                intent.putExtra("date",str);
                intent.putStringArrayListExtra("mLists", mLists);
                intent.putStringArrayListExtra("mTags", mTags);
                startActivityForResult(intent,15);
            }
        });

        calendarView.setOnPreviousPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                setToolbarTitle();
            }
        });

        calendarView.setOnForwardPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                setToolbarTitle();
            }
        });

        calendarMonth=getContext().getResources().getStringArray(R.array.material_calendar_months_array);
        toolbar = view.findViewById(R.id.toolBarInCalendarLayout);
        setHasOptionsMenu(true);

        if(savedInstanceState == null){
            todayYear = calendar.get(Calendar.YEAR);
            todayMonth = calendar.get(Calendar.MONTH);
            todayDay = calendar.get(Calendar.DAY_OF_MONTH);
            previousSelectedDay = todayDay;
            previousSelectedMonth = todayMonth;
            previousSelectedYear = todayYear;
            setToolbarTitle();
        }else{
            previousSelectedYear = savedInstanceState.getInt("previousSelectedYear");
            previousSelectedMonth = savedInstanceState.getInt("previousSelectedMonth");
            previousSelectedDay = savedInstanceState.getInt("previousSelectedDay");
            todayYear = savedInstanceState.getInt("todayYear");
            todayMonth = savedInstanceState.getInt("todayMonth");
            todayDay = savedInstanceState.getInt("todayDay");
            calendar.set(previousSelectedYear, previousSelectedMonth, previousSelectedDay);
            try {
                calendarView.setDate(calendar);
            } catch (OutOfDateRangeException e) {
                e.printStackTrace();
            }
            setToolbarTitle();
        }
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 15){
            if(resultCode == RESULT_OK){
                setEventDays();
            }
        }
    }

    private void setEventDays(){
        dataBaseHelper dataBaseHelper = new dataBaseHelper(getContext());
        Cursor cursor = dataBaseHelper.getAll();
        events.clear();
        if(cursor.getCount()!=0){
            while (cursor.moveToNext()){
                Model model;
                model = new Model(cursor.getInt(0),cursor.getString(1),
                        cursor.getString(2),cursor.getInt(3),cursor.getString(4),
                        cursor.getString(5),cursor.getInt(6),cursor.getInt(7),cursor.getString(8),
                        cursor.getString(9),cursor.getInt(10));
                if(model.getDate() != null){
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = null;
                    try {
                        date = (Date)formatter.parse(model.getDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar cal = Calendar.getInstance();
                    if(date != null){
                        cal.setTime(date);
                    }
                    events.add(new EventDay(cal,R.drawable.ic_baseline_event_note_24));
                }
            }
        }
        calendarView.setEvents(events);
        Log.v("thomas","setEventDays");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("previousSelectedYear",previousSelectedYear);
        outState.putInt("previousSelectedMonth",previousSelectedMonth);
        outState.putInt("previousSelectedDay",previousSelectedDay);
        outState.putInt("todayYear",todayYear);
        outState.putInt("todayMonth",todayMonth);
        outState.putInt("todayDay",todayDay);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.calendar_toobar_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_goToToday:
                gotoToday();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void gotoToday(){
        calendar.set(todayYear, todayMonth, todayDay);
        try {
            calendarView.setDate(calendar);
            previousSelectedDay = todayDay;
            previousSelectedMonth = todayMonth;
            previousSelectedYear = todayYear;
            setToolbarTitle();
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }
    }

    private void setToolbarTitle() {
        Calendar c = calendarView.getCurrentPageDate();
        toolbar.setTitle(c.get(Calendar.YEAR) + " " + calendarMonth[c.get(Calendar.MONTH)]);
    }

    @Override
    public void onStart() {
        calendar.set(previousSelectedYear, previousSelectedMonth, previousSelectedDay);
        try {
            calendarView.setDate(calendar);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }
        super.onStart();
    }
}