package com.example.to_do_list;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ManagingListsFragment extends Fragment {
    private View view;
    private RecyclerView myRecyclerView;
    private ArrayList<String> Data;
    private ListAdapter adapter;
    static private ImageView noListImage;
    static private TextView noListText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_managing_lists, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolBarInManagingActivity);
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        Bundle bundle = getArguments();
        if(bundle != null){
            Data = bundle.getStringArrayList("mLists");
        }

        noListImage = view.findViewById(R.id.noWorkImageInList);
        noListText = view.findViewById(R.id.noWorkTextInList);
        myRecyclerView = view.findViewById(R.id.myRecyclerViewInManagingList);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycler_line));
        myRecyclerView.addItemDecoration(divider);
        adapter = new ListAdapter(getActivity(),getContext(), Data);
        myRecyclerView.setAdapter(adapter);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("mLists",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("lists", null);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        ArrayList<String> mLists = gson.fromJson(json, type);
        //if(!mLists.isEmpty()){
        if(mLists != null){
            hideNoListImage();
        }else {
            showNoListImage();
        }

        return view;
    }

    public void updateList(String originalList, String list){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("mLists",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("lists", null);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        ArrayList<String> mLists = gson.fromJson(json, type);
        if(mLists == null){
            mLists = new ArrayList<>();
        }else {
            int position = mLists.indexOf(originalList);
            mLists.set(position, list);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gso = new Gson();
            String jso = gso.toJson(mLists);
            editor.putString("lists", jso);
            editor.apply();
            Data = mLists;
            adapter.updateList(position, list);
        }
    }

    public void deleteList(String data, int position){
        adapter.deleteOneList(data,position);
    }

    static void showNoListImage(){
        noListImage.setVisibility(View.VISIBLE);
        noListText.setVisibility(View.VISIBLE);
    }

    static void hideNoListImage(){
        noListImage.setVisibility(View.GONE);
        noListText.setVisibility(View.GONE);
    }
}