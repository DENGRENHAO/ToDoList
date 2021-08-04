package com.example.to_do_list;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class FormInCalendar extends AppCompatActivity {
    EditText title,description;
    TextView setDate,setTime;
    TextView remindMeBefore,priorityView,tagView,dateView,remindBeforeView;
    private LinearLayout remindLayout;
    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListener;
    private int previousSelectedDay,previousSelectedMonth,previousSelectedYear,previousSelectedMin,previousSelectedHour;
    private String strDate;
    private ArrayList<PriorityItem> mPriorityList;
    private ArrayList<ListItem> mToolbarList;
    private PriorityAdapter priorityAdapter;
    private ListSpinnerAdapter listAdapter;
    private Switch remindSwitch,allDaySwitch;
    private Boolean ifHideDateAndTime = false;
    private int ifRemind = 0;
    private int ifAllDay = 0;
    private int selectedPriority = 0;
    private String selectedList = "Inbox";
    private String selectedTag = null;
    private String date = null;
    private String time = null;
    public int checkedList = -1;
    public int prevCheckedList = -1;
    public int checkedRemindMe = 0;
    public int prevCheckedRemindMe = 0;
    private String id;
    private ArrayList<String> mLists;
    private ArrayList<String> mTags;
    private String str[] = {"At time", "5 minutes before", "15 minutes before", "30 minutes before",
            "1 hour before", "2 hours before", "1 day before", "2 days before"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_in_calendar);

        priorityView = findViewById(R.id.priorityView);
        tagView = findViewById(R.id.tagView);
        dateView = findViewById(R.id.dateView);
        remindBeforeView = findViewById(R.id.remindBeforeView);

        title = findViewById(R.id.titleInCal);
        description = findViewById(R.id.descriptionInCal);
        setDate = findViewById(R.id.setDate);
        setTime = findViewById(R.id.setTime);
        remindMeBefore = findViewById(R.id.remindMeBefore);
        remindLayout = findViewById(R.id.remindLayout);
        remindSwitch = findViewById(R.id.remindSwitch);
        allDaySwitch = findViewById(R.id.allDaySwitch);

        Toolbar toolbar = findViewById(R.id.toolBar_in_calFormLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyBoard();
                showDatePickerDialog(savedInstanceState);
            }
        });
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyBoard();
                showDatePickerDialog(savedInstanceState);
            }
        });

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyBoard();
                showTimePickerDialog(savedInstanceState);
            }
        });
        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                previousSelectedHour = hourOfDay;
                previousSelectedMin = minute;
                setTimeText(hourOfDay + ":" + minute);;
                time = hourOfDay + ":" + minute;
            }
        };

        mLists = getIntent().getStringArrayListExtra("mLists");
        mTags = getIntent().getStringArrayListExtra("mTags");
        if(getIntent().hasExtra("id")){
            id = getIntent().getStringExtra("id");
        }
        if(getIntent().hasExtra("title")){
            title.setText(getIntent().getStringExtra("title"));
        }
        if(getIntent().hasExtra("description")){
            description.setText(getIntent().getStringExtra("description"));
        }
        if(getIntent().hasExtra("priority")){
            selectedPriority = getIntent().getIntExtra("priority", 0);
        }
        if(getIntent().hasExtra("list")){
            selectedList = getIntent().getStringExtra("list");
        }
        if(getIntent().hasExtra("tag")){
            selectedTag = getIntent().getStringExtra("tag");
        }
        if(getIntent().hasExtra("ifRemind")){
            ifRemind = getIntent().getIntExtra("ifRemind", 0);
        }
        if(getIntent().hasExtra("ifAllDay")){
            ifAllDay = getIntent().getIntExtra("ifAllDay", 0);
        }
        if(getIntent().hasExtra("date")){
            date = strDate = getIntent().getStringExtra("date");
            if(!TextUtils.isEmpty(date)){
                setDateText(date);
            }else {
                setDateText("Select a date or just do it later");
            }
        }else {
            setDateText("Select a date or just do it later");
        }
        if(getIntent().hasExtra("time")){
            time = getIntent().getStringExtra("time");
            if(!TextUtils.isEmpty(time)){
                setTimeText(time);
            }
        }
        if(getIntent().hasExtra("remindBefore")){
            prevCheckedRemindMe = checkedRemindMe = getIntent().getIntExtra("remindBefore", 0);
            if(checkedRemindMe >= 0){
                remindMeBefore.setText("Remind me " + str[checkedRemindMe]);
            }
        }

        if(savedInstanceState != null){
            previousSelectedYear = savedInstanceState.getInt("year");
            previousSelectedMonth = savedInstanceState.getInt("month");
            previousSelectedDay = savedInstanceState.getInt("day");
            previousSelectedMin = savedInstanceState.getInt("min");
            previousSelectedHour = savedInstanceState.getInt("hour");
            if(previousSelectedDay != 0 && previousSelectedMonth != 0 && previousSelectedYear != 0){
                setDateText(previousSelectedYear + "-" + previousSelectedMonth + "-" + previousSelectedDay);
            }
            if(previousSelectedHour != 0 && previousSelectedMin != 0){
                setTimeText(previousSelectedHour + ":" + previousSelectedMin);
            }

            ifHideDateAndTime = savedInstanceState.getBoolean("ifHideDateAndTime");
//            ifAllDay = savedInstanceState.getInt("ifAllDay");
            if(ifHideDateAndTime == true){
                allDaySwitch.setVisibility(View.GONE);
                setTime.setVisibility(View.GONE);
                remindMeBefore.setVisibility(View.GONE);
                remindLayout.setVisibility(View.GONE);
            }else if(ifAllDay == 1){
                setTime.setVisibility(View.GONE);
                remindLayout.setVisibility(View.GONE);
            }
        }else {
            if(ifRemind == 0){
                allDaySwitch.setVisibility(View.GONE);
                setTime.setVisibility(View.GONE);
                remindMeBefore.setVisibility(View.GONE);
                remindLayout.setVisibility(View.GONE);
                ifHideDateAndTime = true;
            }else if(ifAllDay == 1){
                setTime.setVisibility(View.GONE);
                remindLayout.setVisibility(View.GONE);
            }
        }

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                previousSelectedYear = year;
                previousSelectedMonth = month;
                previousSelectedDay = dayOfMonth;
                setDateText(year + "-" + month + "-" + dayOfMonth);
                String sMonth,sDay;
                if(month < 10){
                    sMonth = "0" + month;
                }else {
                    sMonth = month + "";
                }
                if(dayOfMonth < 10){
                    sDay = "0" + dayOfMonth;
                }else {
                    sDay = dayOfMonth + "";
                }
                date = year + "-" + sMonth + "-" + sDay;
                if(ifAllDay == 0 && ifRemind == 1){
                    showTimePickerDialog(savedInstanceState);
                }
            }


        };

        initList();
        Spinner listSpinner = findViewById(R.id.listSpinner);
        listAdapter = new ListSpinnerAdapter(this, mToolbarList);
        listSpinner.setAdapter(listAdapter);

        Log.v("thomas","selectedList = "+ selectedList);
        listSpinner.setSelection(mLists.indexOf(selectedList)+1);
        Log.v("thomas",""+mLists.indexOf(selectedList));
        listSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ListItem clickedItem = (ListItem) parent.getItemAtPosition(position);
                String clickedListName = clickedItem.getmListName();
                selectedList = clickedListName;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        initPriority();
        Spinner prioritySpinner = findViewById(R.id.prioritySpinner);
        priorityAdapter = new PriorityAdapter(this, mPriorityList);
        prioritySpinner.setAdapter(priorityAdapter);

        prioritySpinner.setSelection(selectedPriority);
        prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PriorityItem clickedItem = (PriorityItem) parent.getItemAtPosition(position);
                String clickedPriorityName = clickedItem.getmPriorityName();
                selectedPriority = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        remindMeBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyBoard();
                checkedRemindMe = prevCheckedRemindMe;
                setRemindBeforeList();
            }
        });
        remindBeforeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyBoard();
                checkedRemindMe = prevCheckedRemindMe;
                setRemindBeforeList();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(FormInCalendar.this,R.style.AlertDialogStyle);
        builder.setTitle("Choose your tags");
        String tags[] = new String[mTags.size()];
        for(int i=0 ; i< mTags.size();i++){
            tags[i] = mTags.get(i);
        }
        boolean[] ifTagChecked = new boolean[mTags.size()];
        boolean[] prevIfTagChecked = new boolean[mTags.size()];

        TextView tagTextView  = findViewById(R.id.tagTextView);
        tagTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyBoard();
                for(int i=0;i<mTags.size();i++){
                    ifTagChecked[i] = prevIfTagChecked[i];
                }
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        tagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyBoard();
                for(int i=0;i<mTags.size();i++){
                    ifTagChecked[i] = prevIfTagChecked[i];
                }
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        if(!TextUtils.isEmpty(selectedTag)){
            Gson gson = new Gson();
            String json = selectedTag;
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
            ArrayList<String> selectedTags = gson.fromJson(json, type);
            int n = 0;
            for(int i=0;i<selectedTags.size();i++){
                if(mTags.indexOf(selectedTags.get(i)) != -1){
                    ifTagChecked[mTags.indexOf(selectedTags.get(i))] = prevIfTagChecked[mTags.indexOf(selectedTags.get(i))] = true;
                    n++;
                }
            }
            if(n > 0){
                if(n == 1){
                    tagTextView.setText("You've selected 1 tag");
                }else {
                    tagTextView.setText("You've selected " + n + " tags");
                }
            }else {
                tagTextView.setText("Select your tags");
            }
        }

        builder.setMultiChoiceItems(tags, ifTagChecked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<String> checkedTags = new ArrayList<>();
                int n = 0;
                for(int i=0;i<mTags.size();i++){
                    if(ifTagChecked[i] == true){
                        checkedTags.add(mTags.get(i));
                        n++;
                    }
                    Gson gson = new Gson();
                    selectedTag = gson.toJson(checkedTags);
                    prevIfTagChecked[i] = ifTagChecked[i];
                }
                if(n > 0){
                    if(n == 1){
                        tagTextView.setText("You've selected 1 tag");
                    }else {
                        tagTextView.setText("You've selected " + n + " tags");
                    }
                }else {
                    tagTextView.setText("Select your tags");
                }

            }
        });
        builder.setNegativeButton("Cancel",null);

        if(ifRemind == 1){
            remindSwitch.setChecked(true);
        }
        remindSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    ifRemind = 1;
                    if(ifHideDateAndTime == true){
                        allDaySwitch.setVisibility(View.VISIBLE);
                        remindMeBefore.setVisibility(View.VISIBLE);
                        remindLayout.setVisibility(View.VISIBLE);
                        ifHideDateAndTime = false;
                        if(ifAllDay == 1){
                            setTime.setVisibility(View.GONE);
                            remindLayout.setVisibility(View.GONE);
                        }else {
                            setTime.setVisibility(View.VISIBLE);
                            remindLayout.setVisibility(View.VISIBLE);
                        }
                        showDatePickerDialog(savedInstanceState);
                    }
                }else{
                    ifRemind = 0;
                    allDaySwitch.setVisibility(View.GONE);
                    setTime.setVisibility(View.GONE);
                    remindMeBefore.setVisibility(View.GONE);
                    remindLayout.setVisibility(View.GONE);
                    ifHideDateAndTime = true;
                }
            }
        });
        remindSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyBoard();
            }
        });

        if(ifAllDay == 1){
            allDaySwitch.setChecked(true);
        }
        allDaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    setTime.setVisibility(View.GONE);
                    remindLayout.setVisibility(View.GONE);
                    ifAllDay = 1;
                }else {
                    setTime.setVisibility(View.VISIBLE);
                    remindLayout.setVisibility(View.VISIBLE);
                    showTimePickerDialog(savedInstanceState);
                    ifAllDay = 0;
                }
            }
        });
        allDaySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyBoard();
            }
        });

        title.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(title, InputMethodManager.SHOW_IMPLICIT);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("day",previousSelectedDay);
        outState.putInt("month",previousSelectedMonth);
        outState.putInt("year",previousSelectedYear);
        outState.putInt("hour",previousSelectedHour);
        outState.putInt("min",previousSelectedMin);
        outState.putBoolean("ifHideDateAndTime",ifHideDateAndTime);
    }

    private void initList(){
        mToolbarList = new ArrayList<>();
        mToolbarList.add(new ListItem("Inbox", R.drawable.ic_baseline_all_inbox_35));
        for(String list : mLists){
            mToolbarList.add(new ListItem(list, R.drawable.ic_baseline_list_35));
        }
    }

    private void initPriority(){
        mPriorityList = new ArrayList<>();
        mPriorityList.add(new PriorityItem("No Priority",R.drawable.ic_baseline_error_outline_24));
        mPriorityList.add(new PriorityItem("High Priority",R.drawable.ic_baseline_error_outline_red_24));
        mPriorityList.add(new PriorityItem("Medium Priority",R.drawable.ic_baseline_error_outline_orange_24));
        mPriorityList.add(new PriorityItem("Low Priority",R.drawable.ic_baseline_error_outline_normal_24));
    }

    private void showDatePickerDialog(Bundle savedInstanceState){
        int year,month,day;
        if(savedInstanceState != null || (previousSelectedDay != 0 && previousSelectedMonth != 0 && previousSelectedYear != 0)){
            year = previousSelectedYear;
            month = previousSelectedMonth - 1;
            day = previousSelectedDay;
        }else if(TextUtils.isEmpty(strDate)){
            Log.v("thomas","strDate is empty");
            Calendar cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
        }else {
            String Date[] = strDate.split("-");
            year = Integer.parseInt(Date[0]);
            month = Integer.parseInt(Date[1]) - 1;
            day = Integer.parseInt(Date[2]);
        }
        DatePickerDialog dialog = new DatePickerDialog(FormInCalendar.this,
                AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                dateSetListener,
                year,month,day);
        dialog.show();
    }
    private void showTimePickerDialog(Bundle savedInstanceState){
        int hour,min;
        if(savedInstanceState != null || (previousSelectedHour != 0 && previousSelectedMin != 0)){
            hour = previousSelectedHour;
            min = previousSelectedMin;
        }else if(TextUtils.isEmpty(time)){
            Calendar cal = Calendar.getInstance();
            hour = cal.get(Calendar.HOUR_OF_DAY);
            min = cal.get(Calendar.MINUTE);
        }else {
            String Time[] = time.split(":");
            hour = Integer.parseInt(Time[0]);
            min = Integer.parseInt(Time[1]);
        }
        TimePickerDialog dialog = new TimePickerDialog(FormInCalendar.this,
                AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                timeSetListener,
                hour,min,android.text.format.DateFormat.is24HourFormat(FormInCalendar.this));
        dialog.show();
    }

    private void setRemindBeforeList(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this,R.style.AlertDialogStyle);
        mBuilder.setTitle("Remind me ... ?");
        mBuilder.setSingleChoiceItems(str, checkedRemindMe, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkedRemindMe = which;
            }
        });
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                prevCheckedRemindMe = checkedRemindMe;
                if(checkedRemindMe != 0){
                    remindMeBefore.setText("Remind me " + str[checkedRemindMe]);
                }else {
                    remindMeBefore.setText("Remind me at time");
                }
                dialog.dismiss();
            }
        });
        mBuilder.setNegativeButton("Cancel", null);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar_in_add_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.okInAddList:
                if(TextUtils.isEmpty(title.getText().toString())) {
                    androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(FormInCalendar.this,R.style.AlertDialogStyle);
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Please type in your title");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    Log.v("thomas", "not ok");
                }else if (ifRemind == 1 && date == null || ifRemind == 1 && time == null && ifAllDay == 0) {
                    if(date == null){
                        Log.d("thomas","date==null");
                        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(FormInCalendar.this,R.style.AlertDialogStyle);
                        alertDialog.setTitle("Reminder Alert");
                        alertDialog.setMessage("Please select your date to set reminder");
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    }else if (time == null && ifAllDay == 0){
                        Log.d("thomas","time==null");
                        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(FormInCalendar.this,R.style.AlertDialogStyle);
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage("Please select your time to set reminder");
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    }
                }else {
                    Model model = new Model(-1,title.getText().toString(),description.getText().toString(),selectedPriority,selectedList,
                            selectedTag,ifRemind,ifAllDay,date,time,prevCheckedRemindMe);
                    Log.v("thomas","date last = "+ date);
                    dataBaseHelper dataBaseHelper = new dataBaseHelper(FormInCalendar.this);

                    if(getIntent().hasExtra("title")){
                        dataBaseHelper.updateData(model, id);
                    }else {
                        dataBaseHelper.addOne(model);
                    }

                    Intent intent = new Intent();
                    if(id != null){
                        intent.putExtra("id", Integer.valueOf(id));
                    }
                    setResult(RESULT_OK,intent);
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void closeKeyBoard(){
        View mView = this.getCurrentFocus();
        if(mView != null){
            InputMethodManager manager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(mView.getWindowToken(), 0);
        }
    }


    private void setDateText(String date){
        if(date.equals("Select a date or just do it later")){
            setDate.setText("Select a date or just do it later");
        }else {
            String[] str = date.split("-");
            String month, dayOfWeek;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.valueOf(str[0]),Integer.valueOf(str[1])-1,Integer.valueOf(str[2]));
            int day = calendar.get(Calendar.DAY_OF_WEEK);

            switch (Integer.valueOf(str[1])){
                case 1:
                    month = "Jan";
                    break;
                case 2:
                    month = "Feb";
                    break;
                case 3:
                    month = "Mar";
                    break;
                case 4:
                    month = "Apr";
                    break;
                case 5:
                    month = "May";
                    break;
                case 6:
                    month = "Jun";
                    break;
                case 7:
                    month = "Jul";
                    break;
                case 8:
                    month = "Aug";
                    break;
                case 9:
                    month = "Sep";
                    break;
                case 10:
                    month = "Oct";
                    break;
                case 11:
                    month = "Nov";
                    break;
                case 12:
                    month = "Dec";
                    break;
                default:
                    month = "Jan";
                    break;
            }
            switch (day){
                case 2:
                    dayOfWeek = "Monday";
                    break;
                case 3:
                    dayOfWeek = "Tuesday";
                    break;
                case 4:
                    dayOfWeek = "Wednesday";
                    break;
                case 5:
                    dayOfWeek = "Thursday";
                    break;
                case 6:
                    dayOfWeek = "Friday";
                    break;
                case 7:
                    dayOfWeek = "Saturday";
                    break;
                case 1:
                    dayOfWeek = "Sunday";
                    break;
                default:
                    dayOfWeek = "Monday";
                    break;
            }
            Log.d("thomas","dayofweek=" + day);
            setDate.setText(dayOfWeek + ", " + Integer.valueOf(str[2]) + " " + month);
        }
    }

    private void setTimeText(String time){
        String[] str = time.split(":");
        String hour, min, AmOrPm;
        if (android.text.format.DateFormat.is24HourFormat(FormInCalendar.this)){
            int h = Integer.valueOf(str[0]);
            int m = Integer.valueOf(str[1]);
            hour = str[0];
            min = str[1];
            if(h < 10){
                hour = "0" + hour;
            }
            if(m < 10){
                min = "0" + min;
            }
            setTime.setText("@ " + hour + ":" + min);
        }else {
            int h = Integer.valueOf(str[0]);
            if(h == 0){
                hour = "12";
                AmOrPm = "AM";
            }else if(h < 12) {
                AmOrPm = "AM";
                hour = str[0];
                if(h < 10){
                    hour = "0" + hour;
                }
            }else if(h == 12){
                AmOrPm = "PM";
                hour = "12";
            }else {
                AmOrPm = "PM";
                h -= 12;
                hour = String.valueOf(h);
                if(h < 10){
                    hour = "0" + hour;
                }
            }
            min = str[1];
            if(Integer.valueOf(str[1]) < 10){
                min = "0" + min;
            }

            setTime.setText("@ " + hour + ":" + min + " " + AmOrPm);
        }
    }
}