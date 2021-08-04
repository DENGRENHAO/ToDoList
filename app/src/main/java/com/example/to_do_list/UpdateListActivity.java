package com.example.to_do_list;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class UpdateListActivity extends AppCompatActivity {
    private EditText editText;
    private ArrayList<String> mLists = new ArrayList<>();
    private String primitiveList,originalList;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_list);

        editText = findViewById(R.id.updateListText);
        Toolbar toolbar = findViewById(R.id.toolBarInUpdateManageList);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getAndSetIntentData();

        editText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar_in_update_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.okInUpdateList:
                String list = editText.getText().toString();
                Log.v("thomas",originalList);
                Log.v("thomas",list);
                if((mLists.indexOf(list) == -1 || list.equals(originalList)) && !TextUtils.isEmpty(list)) {
                    Log.v("thomas", "ok");
                    Intent intent = new Intent();
                    intent.putExtra("list", list);
                    intent.putExtra("originalList", originalList);
                    setResult(RESULT_OK, intent);
                    finish();
                }else if (TextUtils.isEmpty(list)){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpdateListActivity.this,R.style.AlertDialogStyle);
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
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpdateListActivity.this,R.style.AlertDialogStyle);
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("This list already exists!\nPlease change to another list!");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
                return true;
            case R.id.deleteInUpdateList:
                Intent intent = new Intent();
                intent.putExtra("primitiveList",primitiveList);
                intent.putExtra("position", position);
                setResult(RESULT_OK,intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void getAndSetIntentData(){
        if(getIntent().hasExtra("list") && getIntent().hasExtra("mLists") && getIntent().hasExtra("position")){
            String str = getIntent().getStringExtra("list");
            originalList = primitiveList = str;
            editText.setText(primitiveList);
            mLists = getIntent().getStringArrayListExtra("mLists");
            position = getIntent().getIntExtra("position", 0);
        }else {
            Toast.makeText(this,"No Data",Toast.LENGTH_SHORT).show();
        }
    }
}