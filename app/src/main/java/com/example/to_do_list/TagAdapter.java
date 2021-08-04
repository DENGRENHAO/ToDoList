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

public class TagAdapter extends RecyclerView.Adapter {
    private ArrayList<String> mData;
    private LayoutInflater mInflater;
    private Context mContext;
    private Activity mActivity;
    private ViewBinderHelper binderHelper = new ViewBinderHelper();

    public TagAdapter(Activity activity,Context context, ArrayList<String> dataSet){
        mActivity = activity;
        mContext = context;
        mData = dataSet;
        mInflater = LayoutInflater.from(context);

        binderHelper.setOpenOnlyOne(true);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_reveal_tags_layout,parent,false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final TagAdapter.TagViewHolder Holder = (TagAdapter.TagViewHolder) holder;

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

    public class TagViewHolder extends RecyclerView.ViewHolder{
        private SwipeRevealLayout swipeLayout;
        private View frontLayout;
        private View deleteItem;
        private TextView textView;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);

            swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout_tags);
            frontLayout = itemView.findViewById(R.id.front_layout_in_tag);
            deleteItem = itemView.findViewById(R.id.deleteItemInTag);
            textView = (TextView) itemView.findViewById(R.id.tagText);
        }

        public void bind(String data,int position) {
            deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteOneTag(data,getAdapterPosition());
                }
            });
            String str = mData.get(position);
            textView.setText(str);

            frontLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String data = mData.get(position);
                    Intent intent = new Intent(mContext,UpdateTagActivity.class);
                    intent.putExtra("tag", data);
                    intent.putStringArrayListExtra("mTags", mData);
                    intent.putExtra("position", getAdapterPosition());
                    mActivity.startActivityForResult(intent,11);
                }
            });
        }
    }

    public void updateTag(int position, String tag){
        mData.set(position, tag);
        notifyDataSetChanged();
    }

    public void deleteOneTag(String data, int position) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("mTags",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("tags", null);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        ArrayList<String> mTags = gson.fromJson(json, type);
        if(mTags == null){
            mTags = new ArrayList<>();
        }else {
            mTags.remove(data);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gso = new Gson();
            String jso = gso.toJson(mTags);
            editor.putString("tags", jso);
            editor.apply();
            mData.remove(position);
            notifyItemRemoved(position);
            if(mTags.isEmpty()){
                ManagingTagsFragment.showNoTagImage();
            }
        }
    }
}
