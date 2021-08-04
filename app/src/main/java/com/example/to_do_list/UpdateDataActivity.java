package com.example.to_do_list;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class UpdateDataActivity extends AppCompatActivity {
    EditText title,description,date;
    private Button update,delete;
    String str_id,str_title,str_description,str_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data);

        title = findViewById(R.id.updateTitle);
        description = findViewById(R.id.updateDescription);
        date = findViewById(R.id.updateDate);
        update = findViewById(R.id.update);
        delete = findViewById(R.id.delete);
        getAndSetIntentData();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataBaseHelper dataBaseHelper = new dataBaseHelper(UpdateDataActivity.this);
                str_title = title.getText().toString();
                str_description = description.getText().toString();
                str_date = date.getText().toString();
//                dataBaseHelper.updateData(str_title,str_description,str_date,str_id);

                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataBaseHelper dataBaseHelper = new dataBaseHelper(UpdateDataActivity.this);
                dataBaseHelper.deleteOneRow(str_id,"delete");

                finish();
            }
        });

        setToolbar();

    }
    void getAndSetIntentData(){
        if(getIntent().hasExtra("id")&&getIntent().hasExtra("title")&&
                getIntent().hasExtra("description")&&getIntent().hasExtra("date")){
            str_id = getIntent().getStringExtra("id");
            str_title = getIntent().getStringExtra("title");
            str_description = getIntent().getStringExtra("description");
            str_date = getIntent().getStringExtra("date");

            title.setText(str_title);
            description.setText(str_description);
            date.setText(str_date);
        }
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar_in_updateLayout);
        if (toolbar != null) {
            setTitle(str_title);
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }
}