package com.example.to_do_list;

public class ListItem {
    private String mListName;
    private int mListImage;

    public ListItem(String listName, int listImage){
        mListImage = listImage;
        mListName = listName;
    }

    public String getmListName(){
        return mListName;
    }
    public int getmListImage(){
        return mListImage;
    }
}
