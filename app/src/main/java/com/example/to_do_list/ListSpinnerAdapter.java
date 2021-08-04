package com.example.to_do_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ListSpinnerAdapter extends ArrayAdapter<ListItem> {

    public ListSpinnerAdapter(Context context, ArrayList<ListItem> spinnerList){
        super(context, 0, spinnerList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_spinner_row,parent,false);
        }
        ImageView imageView = convertView.findViewById(R.id.listSpinnerIcon);
        TextView textViewName = convertView.findViewById(R.id.listSpinnerTextView);

        ListItem listItem = getItem(position);

        if(listItem != null){
            imageView.setImageResource(listItem.getmListImage());
            textViewName.setText(listItem.getmListName());
        }

        return convertView;
    }
}
