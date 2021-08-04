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

public class AddTagActivity extends AppCompatActivity {
    private EditText editTag;
    private List<String> mTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);

        editTag = findViewById(R.id.addTagText);
        Toolbar toolbar = findViewById(R.id.toolBarInAddTag);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Add a tag");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mTags = new ArrayList<>();
        mTags = getIntent().getStringArrayListExtra("mTags");

        editTag.requestFocus();
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
                String tag = editTag.getText().toString();
                if(mTags.indexOf(tag) == -1 && !TextUtils.isEmpty(tag)) {
                    Intent intent = new Intent();
                    intent.putExtra("tag", tag);
                    setResult(RESULT_OK, intent);
                    finish();

                }else if (TextUtils.isEmpty(tag)){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddTagActivity.this,R.style.AlertDialogStyle);
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
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddTagActivity.this,R.style.AlertDialogStyle);
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("This tag already exists!\nPlease add another tag!");
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