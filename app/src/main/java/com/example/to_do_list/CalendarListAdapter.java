package com.example.to_do_list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.ArrayList;

public class CalendarListAdapter extends RecyclerView.Adapter {
    private ArrayList<Model> mData;
    private LayoutInflater mInflater;
    private Context mContext;
    private Activity mActivity;
    private ArrayList<String> mLists;
    private ArrayList<String> mTags;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public CalendarListAdapter(Activity activity,Context context, ArrayList<Model> dataSet,ArrayList<String> lists,ArrayList<String> tags) {
        mActivity = activity;
        mContext = context;
        mData = dataSet;
        mLists = lists;
        mTags = tags;
        mInflater = LayoutInflater.from(context);

        binderHelper.setOpenOnlyOne(true);
    }

    @NonNull
    @Override
    public ViewHolderInCal onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_reveal_layout,parent,false);
        return new ViewHolderInCal(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ViewHolderInCal Holder = (ViewHolderInCal) holder;

        if (mData != null && 0 <= position && position < mData.size()) {
            final Model data = mData.get(position);

            binderHelper.bind(Holder.swipeLayout, String.valueOf(data.getId()));

            Holder.cal_bind(String.valueOf(data.getId()),position);
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null)
            return 0;
        return mData.size();
    }

    public class ViewHolderInCal extends RecyclerView.ViewHolder{
        private SwipeRevealLayout swipeLayout;
        private View frontLayout;
        private View completeItem,deleteItem;
        private TextView textView;

        public ViewHolderInCal(@NonNull View itemView) {
            super(itemView);

            swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
            frontLayout = itemView.findViewById(R.id.front_layout);
            completeItem = itemView.findViewById(R.id.completedItem);
            deleteItem = itemView.findViewById(R.id.deleteItem);
            textView = (TextView) itemView.findViewById(R.id.text);
        }


        public void cal_bind(String data,int position) {
            completeItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model model = null;
                    for(Model item : mData){
                        if(item.getId() == Integer.valueOf(data)){
                            model = item;
                            break;
                        }
                    }
                    String date = model.getDate();
                    dataBaseHelper dataBaseHelper = new dataBaseHelper(mActivity);
                    dataBaseHelper.deleteOneRow(data,"complete");
                    mData.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());

                    Cursor cursor = dataBaseHelper.getAllInCal(date);
                    if(cursor.getCount() == 0){
                        ListInCalendar.showNoWorkImage();
                    }
                }
            });
            deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model model = null;
                    for(Model item : mData){
                        if(item.getId() == Integer.valueOf(data)){
                            model = item;
                            break;
                        }
                    }
                    String date = model.getDate();
                    dataBaseHelper dataBaseHelper = new dataBaseHelper(mActivity);
                    dataBaseHelper.deleteOneRow(data,"delete");
                    mData.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());

                    Cursor cursor = dataBaseHelper.getAllInCal(date);
                    if(cursor.getCount() == 0){
                        ListInCalendar.showNoWorkImage();
                    }
                }
            });

            Model model = mData.get(position);
            textView.setText(model.getTitle());

            frontLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String displayText = "" + data + " clicked";
                    final Model data = mData.get(position);
                    Intent intent = new Intent(mContext,FormInCalendar.class);
                    intent.putStringArrayListExtra("mLists", mLists);
                    intent.putStringArrayListExtra("mTags", mTags);
                    intent.putExtra("id",String.valueOf(data.getId()));
                    intent.putExtra("title",data.getTitle());
                    intent.putExtra("description",data.getDescription());
                    intent.putExtra("priority", data.getPriority());
                    intent.putExtra("list", data.getList());
                    intent.putExtra("tag", data.getTag());
                    intent.putExtra("ifRemind", data.getIfRemind());
                    intent.putExtra("ifAllDay", data.getIfAllDay());
                    intent.putExtra("date", data.getDate());
                    intent.putExtra("time", data.getTime());
                    intent.putExtra("remindBefore", data.getRemindBefore());
                    mActivity.startActivityForResult(intent,4);
                }
            });
        }
    }
}
