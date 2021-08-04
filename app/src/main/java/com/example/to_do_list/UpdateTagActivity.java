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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class UpdateTagActivity extends AppCompatActivity {
    private EditText editText;
    private ArrayList<String> mTags = new ArrayList<>();
    private String primitiveTag,originalTag;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_tag);

        editText = findViewById(R.id.updateTagText);
        Toolbar toolbar = findViewById(R.id.toolBarInUpdateManageTag);
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
                String tag = editText.getText().toString();
                if((mTags.indexOf(tag) == -1 || tag.equals(originalTag)) && !TextUtils.isEmpty(tag)) {
                    Intent intent = new Intent();
                    intent.putExtra("tag", tag);
                    intent.putExtra("originalTag", originalTag);
                    setResult(RESULT_OK, intent);
                    finish();
                }else if (TextUtils.isEmpty(tag)){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpdateTagActivity.this,R.style.AlertDialogStyle);
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Please type in the tag name!");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpdateTagActivity.this,R.style.AlertDialogStyle);
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("This tag already exists!\nPlease change to another tag!");
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
                intent.putExtra("primitiveTag",primitiveTag);
                intent.putExtra("position", position);
                setResult(RESULT_OK,intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getAndSetIntentData() {
        if(getIntent().hasExtra("tag") && getIntent().hasExtra("mTags") && getIntent().hasExtra("position")){
            String str = getIntent().getStringExtra("tag");
            originalTag = primitiveTag = str;
            editText.setText(primitiveTag);
            mTags = getIntent().getStringArrayListExtra("mTags");
            position = getIntent().getIntExtra("position", 0);
        }else {
            Toast.makeText(this,"No Data",Toast.LENGTH_SHORT).show();
        }
    }
}