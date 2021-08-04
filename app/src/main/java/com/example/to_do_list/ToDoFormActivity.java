package com.example.to_do_list;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ToDoFormActivity extends AppCompatActivity {
    EditText title,description,date;
    Button add;
    dataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_form);

        setTitle("Form");

        add = findViewById(R.id.add);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        date = findViewById(R.id.date);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Model model;

//                try {
//                    model = new Model(-1,title.getText().toString(),description.getText().toString(),Integer.parseInt(date.getText().toString()));
//                } catch (Exception e) {
//                    Toast.makeText(ToDoFormActivity.this,"ERROR:No date filled", Toast.LENGTH_SHORT).show();
//                    model = new Model(-1,"error","something is wrong",0);
//                }

                dataBaseHelper dataBaseHelper = new dataBaseHelper(ToDoFormActivity.this);

//                boolean ifSuccess = dataBaseHelper.addOne(model);

                setResult(RESULT_OK);
                finish();
            }
        });

        setToolbar();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar_in_updateLayout);
        if (toolbar != null) {
            setTitle("New Work");
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