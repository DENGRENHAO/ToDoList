package com.example.to_do_list;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateCalDataActivity extends AppCompatActivity {
    EditText title,description,date;
    private Button update,delete;
    String str_id,str_title,str_description,str_time,str_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_cal_data);

        title = findViewById(R.id.updateCalTitle);
        description = findViewById(R.id.updateCalDescription);
        date = findViewById(R.id.updateCalDate);
        update = findViewById(R.id.calUpdate);
        delete = findViewById(R.id.calDelete);

        getAndSetIntentData();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDBHelper calendarDBHelper = new CalendarDBHelper(UpdateCalDataActivity.this);
                str_title = title.getText().toString();
                str_description = description.getText().toString();
                str_date = date.getText().toString();
                calendarDBHelper.updateDataInCal(str_title,str_description,str_time,str_date,str_id);

                Log.v("thomas","clicked");
                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDBHelper calendarDBHelper = new CalendarDBHelper(UpdateCalDataActivity.this);
                calendarDBHelper.deleteOneRowInCal(str_id);

                finish();
            }
        });
    }

    void getAndSetIntentData(){
        if(getIntent().hasExtra("id")&&getIntent().hasExtra("title")&&
                getIntent().hasExtra("description")&&getIntent().hasExtra("time")&&getIntent().hasExtra("date")){
            str_id = getIntent().getStringExtra("id");
            str_title = getIntent().getStringExtra("title");
            str_description = getIntent().getStringExtra("description");
            str_time = getIntent().getStringExtra("time");
            str_date = getIntent().getStringExtra("date");

            title.setText(str_title);
            description.setText(str_description);
            date.setText(str_date);
        }
    }
}