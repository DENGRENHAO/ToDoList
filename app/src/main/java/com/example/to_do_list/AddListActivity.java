package com.example.to_do_list;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class AddListActivity extends AppCompatActivity {
    private EditText editList;
    private List<String> mLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        editList = findViewById(R.id.addListText);
        Toolbar toolbar = findViewById(R.id.toolBarInAddList);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Add a list");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mLists = new ArrayList<>();
        mLists = getIntent().getStringArrayListExtra("mLists");

        editList.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
                String list = editList.getText().toString();
                if(mLists.indexOf(list) == -1 && !TextUtils.isEmpty(list)) {
                    Intent intent = new Intent();
                    intent.putExtra("list", list);
                    setResult(RESULT_OK, intent);
                    finish();

                }else if (TextUtils.isEmpty(list)){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddListActivity.this,R.style.AlertDialogStyle);
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Please type in the list name!");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddListActivity.this,R.style.AlertDialogStyle);
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("This list already exists!\nPlease add another list!");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED,intent);
        finish();
    }
}