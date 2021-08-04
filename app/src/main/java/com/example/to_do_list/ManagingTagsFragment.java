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

public class ManagingTagsFragment extends Fragment {
    private View view;
    private RecyclerView myRecyclerView;
    private ArrayList<String> Data;
    private TagAdapter adapter;
    static private ImageView noTagImage;
    static private TextView noTagText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_managing_tags, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolBarInManagingActivity);
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        Bundle bundle = getArguments();
        if(bundle != null){
            Data = bundle.getStringArrayList("mTags");
        }

        noTagImage = view.findViewById(R.id.noWorkImageInTag);
        noTagText = view.findViewById(R.id.noWorkTextInTag);

        myRecyclerView = view.findViewById(R.id.myRecyclerViewInManagingTags);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycler_line));
        myRecyclerView.addItemDecoration(divider);
        adapter = new TagAdapter(getActivity(),getContext(), Data);
        myRecyclerView.setAdapter(adapter);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("mTags",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("tags", null);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        ArrayList<String> mTags = gson.fromJson(json, type);
        if(mTags != null){
            hideNoTagImage();
        }else {
            showNoTagImage();
        }
        return view;
    }

    public void updateTag(String originalTag, String tag){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("mTags",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("tags", null);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        ArrayList<String> mTags = gson.fromJson(json, type);
        if(mTags == null){
            mTags = new ArrayList<>();
        }else {
            int position = mTags.indexOf(originalTag);
            mTags.set(position, tag);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gso = new Gson();
            String jso = gso.toJson(mTags);
            editor.putString("tags", jso);
            editor.apply();
            Data = mTags;
            adapter.updateTag(position, tag);
        }
    }

    public void deleteTag(String data, int position){
        adapter.deleteOneTag(data,position);
    }

    static void showNoTagImage(){
        noTagImage.setVisibility(View.VISIBLE);
        noTagText.setVisibility(View.VISIBLE);
    }

    static void hideNoTagImage(){
        noTagImage.setVisibility(View.GONE);
        noTagText.setVisibility(View.GONE);
    }
}