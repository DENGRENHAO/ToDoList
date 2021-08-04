package com.example.to_do_list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ListAdapter extends RecyclerView.Adapter {
    private ArrayList<String> mData;
    private LayoutInflater mInflater;
    private Context mContext;
    private Activity mActivity;
    private ViewBinderHelper binderHelper = new ViewBinderHelper();

    public ListAdapter(Activity activity,Context context, ArrayList<String> dataSet){
        mActivity = activity;
        mContext = context;
        mData = dataSet;
        mInflater = LayoutInflater.from(context);

        binderHelper.setOpenOnlyOne(true);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_reveal_lists_layout,parent,false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ListAdapter.ListViewHolder Holder = (ListAdapter.ListViewHolder) holder;

        if (mData != null && 0 <= position && position < mData.size()) {
            final String data = mData.get(position);

            binderHelper.bind(Holder.swipeLayout, data);

            Holder.bind(data,position);
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null)
            return 0;
        return mData.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder{
        private SwipeRevealLayout swipeLayout;
        private View frontLayout;
        private View deleteItem;
        private TextView textView;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);

            swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout_lists);
            frontLayout = itemView.findViewById(R.id.front_layout_in_list);
            deleteItem = itemView.findViewById(R.id.deleteItemInList);
            textView = (TextView) itemView.findViewById(R.id.listText);
        }
        public void bind(String data,int position) {
            deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteOneList(data,getAdapterPosition());
                }
            });
            String str = mData.get(position);
            textView.setText(str);

            frontLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String data = mData.get(position);
                    Intent intent = new Intent(mContext,UpdateListActivity.class);
                    intent.putExtra("list", data);
                    intent.putStringArrayListExtra("mLists", mData);
                    intent.putExtra("position", getAdapterPosition());
                    mActivity.startActivityForResult(intent,10);
                }
            });
        }
    }

    public void updateList(int position, String list){
        mData.set(position, list);
        notifyDataSetChanged();
    }

    public void deleteOneList(String data, int position) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("mLists",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("lists", null);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        ArrayList<String> mLists = gson.fromJson(json, type);
        if(mLists == null){
            mLists = new ArrayList<>();
        }else {
            mLists.remove(data);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gso = new Gson();
            String jso = gso.toJson(mLists);
            editor.putString("lists", jso);
            editor.apply();
            mData.remove(position);
            notifyItemRemoved(position);
            if(mLists.isEmpty()){
                ManagingListsFragment.showNoListImage();
            }
        }
    }


}
