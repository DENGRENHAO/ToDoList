package com.example.to_do_list;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private Activity mActivity;
    private List<String> mListHeaderTitle;
    private List<List<String>> mListChildTitle;
    private int yesterdayYear,yesterdayMonth,yesterdayDay,todayYear,todayMonth,todayDay,tomorrowDay,tomorrowMonth,tomorrowYear,nextWeekDay,nextWeekMonth,nextWeekYear;
    private String yesterdayDate,todayDate,tomorrowDate,nextWeekDate;
    
    public ExpandableListAdapter(Activity activity, Context context, List<String> listHeader, List<List<String>> listChild){
        this.mActivity = activity;
        this.context = context;
        mListHeaderTitle = listHeader;
        mListChildTitle = listChild;
    }
    @Override
    public int getGroupCount() {
        return mListHeaderTitle.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mListChildTitle.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mListHeaderTitle.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mListChildTitle.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String mHeaderTitle = (String) getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater Inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Inflater.inflate(R.layout.expandable_list_header, null);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.headMenu);
        ImageView headerIcon = (ImageView) convertView.findViewById(R.id.headerIcon);
        ImageView arrowIcon = (ImageView) convertView.findViewById(R.id.arrowIcon);
        TextView tv = (TextView) convertView.findViewById(R.id.counter);
        textView.setText(mHeaderTitle);
        headerIcon.setImageResource(MainActivity.icon[groupPosition]);
        if(groupPosition != 0 && groupPosition != 5){
            tv.setVisibility(View.GONE);
            arrowIcon.setVisibility(View.VISIBLE);
            if (isExpanded) {
                arrowIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
            } else {
                arrowIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_left_24);
            }
        }else if(groupPosition == 0){
            arrowIcon.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
            dataBaseHelper dataBaseHelper = new dataBaseHelper(mActivity);
            Cursor cursor = dataBaseHelper.getAllBySort(0, 0, "Inbox", null, null,null,null);
            tv.setText("" + cursor.getCount());
        }else {
            arrowIcon.setVisibility(View.GONE);
            tv.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String childText = (String) getChild(groupPosition, childPosition);
        if(convertView == null){
            LayoutInflater Inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Inflater.inflate(R.layout.expandable_list_sub_menu, null);
        }
        TextView mChildTitle = (TextView) convertView.findViewById(R.id.submenu);
        TextView tv = (TextView) convertView.findViewById(R.id.subCounter);
        dataBaseHelper dataBaseHelper = new dataBaseHelper(mActivity);
        Cursor cursor;
        if (groupPosition == 2){
            Calendar calendar = Calendar.getInstance();
            todayDay = calendar.get(Calendar.DAY_OF_MONTH);
            todayMonth = calendar.get(Calendar.MONTH) + 1;
            todayYear = calendar.get(Calendar.YEAR);
            calendar.add(Calendar.DAY_OF_MONTH,1);
            tomorrowDay = calendar.get(Calendar.DAY_OF_MONTH);
            tomorrowMonth = calendar.get(Calendar.MONTH) + 1;
            tomorrowYear = calendar.get(Calendar.YEAR);
            calendar.add(Calendar.DAY_OF_MONTH,5);
            nextWeekDay = calendar.get(Calendar.DAY_OF_MONTH);
            nextWeekMonth = calendar.get(Calendar.MONTH) + 1;
            nextWeekYear = calendar.get(Calendar.YEAR);
            calendar.add(Calendar.DAY_OF_MONTH,-7);
            yesterdayDay = calendar.get(Calendar.DAY_OF_MONTH);
            yesterdayMonth = calendar.get(Calendar.MONTH) + 1;
            yesterdayYear = calendar.get(Calendar.YEAR);

            if (todayMonth < 10 && todayDay < 10){
                todayDate = todayYear + "-0" + todayMonth + "-0" + todayDay;
            }else if(todayMonth >= 10 && todayDay < 10){
                todayDate = todayYear + "-" + todayMonth + "-0" + todayDay;
            }else if(todayMonth < 10 && todayDay >= 10){
                todayDate = todayYear + "-0" + todayMonth + "-" + todayDay;
            }else {
                todayDate = todayYear + "-" + todayMonth + "-" + todayDay;
            }

            if (tomorrowMonth < 10 && tomorrowDay < 10){
                tomorrowDate = tomorrowYear + "-0" + tomorrowMonth + "-0" + tomorrowDay;
            }else if(tomorrowMonth >= 10 && tomorrowDay < 10){
                tomorrowDate = tomorrowYear + "-" + tomorrowMonth + "-0" + tomorrowDay;
            }else if(tomorrowMonth < 10 && tomorrowDay >= 10){
                tomorrowDate = tomorrowYear + "-0" + tomorrowMonth + "-" + tomorrowDay;
            }else {
                tomorrowDate = tomorrowYear + "-" + tomorrowMonth + "-" + tomorrowDay;
            }
            if (nextWeekMonth < 10 && nextWeekDay < 10){
                nextWeekDate = nextWeekYear + "-0" + nextWeekMonth + "-0" + nextWeekDay;
            }else if(nextWeekMonth >= 10 && nextWeekDay < 10){
                nextWeekDate = nextWeekYear + "-" + nextWeekMonth + "-0" + nextWeekDay;
            }else if(nextWeekMonth < 10 && nextWeekDay >= 10){
                nextWeekDate = nextWeekYear + "-0" + nextWeekMonth + "-" + nextWeekDay;
            }else {
                nextWeekDate = nextWeekYear + "-" + nextWeekMonth + "-" + nextWeekDay;
            }
            if (yesterdayMonth < 10 && yesterdayDay < 10){
                yesterdayDate = yesterdayYear + "-0" + yesterdayMonth + "-0" + yesterdayDay;
            }else if(yesterdayMonth >= 10 && yesterdayDay < 10){
                yesterdayDate = yesterdayYear + "-" + yesterdayMonth + "-0" + yesterdayDay;
            }else if(yesterdayMonth < 10 && yesterdayDay >= 10){
                yesterdayDate = yesterdayYear + "-0" + yesterdayMonth + "-" + yesterdayDay;
            }else {
                yesterdayDate = yesterdayYear + "-" + yesterdayMonth + "-" + yesterdayDay;
            }
            cursor = dataBaseHelper.getAllBySort(groupPosition, childPosition, mListChildTitle.get(groupPosition).get(childPosition), yesterdayDate, todayDate, tomorrowDate,nextWeekDate);
        }else {
            cursor = dataBaseHelper.getAllBySort(groupPosition, childPosition, mListChildTitle.get(groupPosition).get(childPosition), null, null,null,null);
        }
        tv.setText("" + cursor.getCount());
        mChildTitle.setText(childText);
        mChildTitle.setTextColor(Color.parseColor("#000000"));
        if(childText == "High Priority"){
            mChildTitle.setTextColor(Color.parseColor("#FF0000"));
        }
        if(childText == "Medium Priority"){
            mChildTitle.setTextColor(Color.parseColor("#FF9800"));
        }
        if(childText == "Low Priority"){
            mChildTitle.setTextColor(Color.parseColor("#0088FF"));
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void updateCounter(){
        notifyDataSetChanged();
    }
}
