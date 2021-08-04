package com.example.to_do_list;

public class PriorityItem {
    private String mPriorityName;
    private int mPriorityImage;

    public PriorityItem(String priorityName, int priorityImage){
        mPriorityName = priorityName;
        mPriorityImage = priorityImage;
    }

    public String getmPriorityName() {
        return mPriorityName;
    }

    public int getmPriorityImage() {
        return mPriorityImage;
    }
}
