package com.example.to_do_list;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ManageListsAndTagsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private FragmentManager fragmentManager;
    private ManagingListsFragment listsFragment;
    private ManagingTagsFragment tagsFragment;
    private BottomNavigationView bottomNavigationView;
    private int position = -1;
    MeowBottomNavigation bottomNavigation;
    FragmentTransaction transaction;
    Boolean ifManagingLists = true;
    ArrayList<String> mLists;
    ArrayList<String> mTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_lists_and_tags);

        mLists = new ArrayList<>();
        mLists = getIntent().getStringArrayListExtra("mLists");
        mTags = new ArrayList<>();
        mTags = getIntent().getStringArrayListExtra("mTags");

//        bottomNavigationView = findViewById(R.id.bottomNavigationViewInManaging);
        Toolbar toolbar = findViewById(R.id.toolBarInManagingActivity);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.addInManaging:
                        if(ifManagingLists == true){
                            Intent intent = new Intent(ManageListsAndTagsActivity.this,AddListActivity.class);
                            intent.putStringArrayListExtra("mLists", (ArrayList<String>) mLists);
                            startActivityForResult(intent,8);
                        }else {
                            Intent intent = new Intent(ManageListsAndTagsActivity.this,AddTagActivity.class);
                            intent.putStringArrayListExtra("mTags", (ArrayList<String>) mTags);
                            startActivityForResult(intent,9);
                        }
                        break;
                }
                return false;
            }
        });
        bottomNavigation = findViewById(R.id.bottomNavigationViewInManaging);
        bottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.ic_baseline_list_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.ic_baseline_label_24));
        bottomNavigation.show(1,true);
        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()){
                    case 1:
                        toolbar.setTitle("Manage your lists");
                        goToListFragment();
                        break;
                    case 2:
                        toolbar.setTitle("Manage your tags");
                        goToTagFragment();
                        break;
                }
                return null;
            }
        });
        bottomNavigation.setOnReselectListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                return null;
            }
        });

//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()){
//                    case R.id.item_list_in_managing:
//                        toolbar.setTitle("Manage your lists");
//                        goToListFragment();
//                        break;
//                    case R.id.item_tags_in_managing:
//                        toolbar.setTitle("Manage your tags");
//                        goToTagFragment();
//                        break;
//                }
//                return true;
//            }
//        });

        if(savedInstanceState == null) {
            toolbar.setTitle("Manage your lists");
            goToListFragment();
        }
    }

    private void goToListFragment() {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("mLists",mLists);
        listsFragment = new ManagingListsFragment();
        listsFragment.setArguments(bundle);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.containerInManagingActivity,listsFragment);
        transaction.commit();
        ifManagingLists = true;
    }

    private void goToTagFragment() {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("mTags", mTags);
        tagsFragment = new ManagingTagsFragment();
        tagsFragment.setArguments(bundle);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.containerInManagingActivity,tagsFragment);
        transaction.commit();
        ifManagingLists = false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 8){
            if(resultCode == RESULT_OK){
                String list = data.getStringExtra("list");
                mLists.add(list);
                saveLists();
                RecyclerView myRecyclerView = (RecyclerView) findViewById(R.id.myRecyclerViewInManagingList);
                myRecyclerView.getLayoutManager().smoothScrollToPosition(myRecyclerView,new RecyclerView.State(), myRecyclerView.getAdapter().getItemCount());
            }
        }else if(requestCode == 9){
            if(resultCode == RESULT_OK){
                String tag = data.getStringExtra("tag");
                mTags.add(tag);
                saveTags();
                RecyclerView myRecyclerView = (RecyclerView) findViewById(R.id.myRecyclerViewInManagingTags);
                myRecyclerView.getLayoutManager().smoothScrollToPosition(myRecyclerView,new RecyclerView.State(), myRecyclerView.getAdapter().getItemCount());
            }
        }
        if(requestCode == 10){
            if(resultCode == RESULT_OK){
                if(data.hasExtra("list") && data.hasExtra("originalList")){
                    listsFragment.updateList(data.getStringExtra("originalList"), data.getStringExtra("list"));
                    Log.v("thomas",data.getStringExtra("originalList") +  data.getStringExtra("list"));
                }else if(data.hasExtra("primitiveList") && data.hasExtra("position")){
                    listsFragment.deleteList(data.getStringExtra("primitiveList"), data.getIntExtra("position", 0));
                }
            }
        }
        if(requestCode == 11){
            if(resultCode == RESULT_OK){
                if(data.hasExtra("tag") && data.hasExtra("originalTag")){
                    tagsFragment.updateTag(data.getStringExtra("originalTag"), data.getStringExtra("tag"));
                    Log.v("thomas",data.getStringExtra("originalTag") +  data.getStringExtra("tag"));
                }else if(data.hasExtra("primitiveTag") && data.hasExtra("position")){
                    tagsFragment.deleteTag(data.getStringExtra("primitiveTag"), data.getIntExtra("position", 0));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK,intent);
        finish();
    }

    private void saveLists(){
        SharedPreferences sharedPreferences = getSharedPreferences("mLists",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mLists);
        editor.putString("lists", json);
        editor.apply();
        ManagingListsFragment.hideNoListImage();
    }

    private void saveTags(){
        SharedPreferences sharedPreferences = getSharedPreferences("mTags",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mTags);
        editor.putString("tags", json);
        editor.apply();
        ManagingTagsFragment.hideNoTagImage();
    }
}