package com.example.to_do_list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements Filterable {
    private ArrayList<Model> mData;
    private ArrayList<Model> mDataFull;
    private LayoutInflater mInflater;
    private Context mContext;
    private Activity mActivity;
    private ArrayList<String> mLists;
    private ArrayList<String> mTags;
    private int mTime = 0;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    public MyAdapter(Activity activity,Context context, ArrayList<Model> dataSet, ArrayList<String> lists, ArrayList<String> tags) {
        mActivity = activity;
        mContext = context;
        this.mData = dataSet;
        mLists = lists;
        mTags = tags;
        mInflater = LayoutInflater.from(context);

        Log.v("thomas","dataset = " +dataSet);
        binderHelper.setOpenOnlyOne(true);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_reveal_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (mData != null && 0 <= position && position < mData.size()) {
            final Model data = mData.get(position);

            binderHelper.bind(holder.swipeLayout, String.valueOf(data.getId()));

            holder.bind(String.valueOf(data.getId()),position);
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null)
            return 0;
        return mData.size();
    }

    @Override
    public Filter getFilter() {
        return dataFilter;
    }

    private Filter dataFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Model> filteredList = new ArrayList<>();

            if(mTime == 0){
                mDataFull = new ArrayList<>(mData);
                mTime = 1;
            }
            if(constraint == null || constraint.length() == 0){
//                Log.v("thomas","mdataFull = "+mDataFull.toString());
//                Log.v("thomas","mdata = "+mData.toString());
                filteredList.addAll(mDataFull);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Model item : mDataFull){
                    if(item.getTitle().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            Log.v("thomas","filterList = "+filteredList.toString());
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mData.clear();
            mData.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder{
        private SwipeRevealLayout swipeLayout;
        private View frontLayout;
        private View completeItem,deleteItem;
        private TextView textView,dateText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
            frontLayout = itemView.findViewById(R.id.front_layout);
            completeItem = itemView.findViewById(R.id.completedItem);
            deleteItem = itemView.findViewById(R.id.deleteItem);
            textView = (TextView) itemView.findViewById(R.id.text);
            dateText = (TextView) itemView.findViewById(R.id.dateText);
        }


        public void bind(String data,int position) {
            completeItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataBaseHelper dataBaseHelper = new dataBaseHelper(mActivity);
                    Model m = null;
                    if(mDataFull != null){
                        for(Model item : mDataFull){
                            if(String.valueOf(item.getId()) == data){
                                m = item;
                            }
                        }
                        mDataFull.remove(m);
                    }
                    dataBaseHelper.deleteOneRow(data,"complete");
                    mData.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());

                    if(dataBaseHelper.getTableSize() == 0){
                        ToDoListFragment.showNoWorkImage();
                    }
                }
            });
            deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataBaseHelper dataBaseHelper = new dataBaseHelper(mActivity);
                    Model m = null;
                    if(mDataFull != null){
                        for(Model item : mDataFull){
                            if(String.valueOf(item.getId()) == data){
                                m = item;
                            }
                        }
                        mDataFull.remove(m);
                    }

                    dataBaseHelper.deleteOneRow(data,"delete");
                    mData.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());

                    if(dataBaseHelper.getTableSize() == 0){
                        ToDoListFragment.showNoWorkImage();
                    }
                }
            });

            Model m = mData.get(position);
            textView.setText(m.getTitle());

            if(!TextUtils.isEmpty(m.getDate())){
                String[] str = m.getDate().split("-");
                String month, dayOfWeek;
                Calendar calendar = Calendar.getInstance();
                calendar.set(Integer.valueOf(str[0]),Integer.valueOf(str[1])-1,Integer.valueOf(str[2]));
                int day = calendar.get(Calendar.DAY_OF_WEEK);

                switch (Integer.valueOf(str[1])){
                    case 1:
                        month = "Jan";
                        break;
                    case 2:
                        month = "Feb";
                        break;
                    case 3:
                        month = "Mar";
                        break;
                    case 4:
                        month = "Apr";
                        break;
                    case 5:
                        month = "May";
                        break;
                    case 6:
                        month = "Jun";
                        break;
                    case 7:
                        month = "Jul";
                        break;
                    case 8:
                        month = "Aug";
                        break;
                    case 9:
                        month = "Sep";
                        break;
                    case 10:
                        month = "Oct";
                        break;
                    case 11:
                        month = "Nov";
                        break;
                    case 12:
                        month = "Dec";
                        break;
                    default:
                        month = "Jan";
                        break;
                }
                switch (day){
                    case 2:
                        dayOfWeek = "Monday";
                        break;
                    case 3:
                        dayOfWeek = "Tuesday";
                        break;
                    case 4:
                        dayOfWeek = "Wednesday";
                        break;
                    case 5:
                        dayOfWeek = "Thursday";
                        break;
                    case 6:
                        dayOfWeek = "Friday";
                        break;
                    case 7:
                        dayOfWeek = "Saturday";
                        break;
                    case 1:
                        dayOfWeek = "Sunday";
                        break;
                    default:
                        dayOfWeek = "Monday";
                        break;
                }
                dateText.setText(dayOfWeek + ", " + Integer.valueOf(str[2]) + " " + month);
            }

            frontLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Model data = mData.get(position);
                    Intent intent = new Intent(mContext,FormInCalendar.class);
                    intent.putStringArrayListExtra("mLists", mLists);
                    intent.putStringArrayListExtra("mTags", mTags);
                    intent.putExtra("id",String.valueOf(data.getId()));
                    intent.putExtra("title",data.getTitle());
                    intent.putExtra("description",data.getDescription());
                    intent.putExtra("priority", data.getPriority());
                    intent.putExtra("list", data.getList());
                    Log.v("thomas","selectedList myAdapter = "+ data.getList());
                    intent.putExtra("tag", data.getTag());
                    intent.putExtra("ifRemind", data.getIfRemind());
                    intent.putExtra("ifAllDay", data.getIfAllDay());
                    intent.putExtra("date", data.getDate());
                    intent.putExtra("time", data.getTime());
                    intent.putExtra("remindBefore", data.getRemindBefore());
                    mActivity.startActivityForResult(intent,2);
                }
            });
        }
    }


}
