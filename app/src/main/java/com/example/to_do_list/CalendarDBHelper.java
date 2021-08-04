package com.example.to_do_list;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class CalendarDBHelper extends SQLiteOpenHelper {
    public static final String MYCALTABLE = "MYCALTABLE";
    public static final String CALID = "CALID";
    public static final String CALTITLE = "CALTITLE";
    public static final String CALDESCRIPTION = "CALDESCRIPTION";
    public static final String CALDATE = "CALDATE";
    public static final String CALTIME = "CALTIME";
    private Context context;

    public CalendarDBHelper(@Nullable Context context) {
        super(context,"calData.db",null,1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + MYCALTABLE + " (" + CALID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CALTITLE + " TEXT, " + CALDESCRIPTION + " TEXT, " + CALTIME + " TEXT, " + CALDATE + " INT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addOneInCal(CalendarModel calendarModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CALTITLE, calendarModel.getTitle());
        cv.put(CALDESCRIPTION, calendarModel.getDescription());
        cv.put(CALTIME , calendarModel.getTime());
        cv.put(CALDATE, calendarModel.getDate());

        long insert = db.insert(MYCALTABLE, null, cv);
        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }



    void updateDataInCal(String title,String description,String time,String date,String id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CALTITLE, title);
        cv.put(CALDESCRIPTION, description);
        cv.put(CALTIME, time);
        cv.put(CALDATE, date);

        long result = db.update(MYCALTABLE,cv,CALID + "='" + id + "'",null);
        if(result == -1){
            Toast.makeText(context,"fail to update",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context,"update successssss",Toast.LENGTH_SHORT).show();
        }
    }

    void deleteOneRowInCal(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(MYCALTABLE,CALID + "='" + id + "'",null);
        if(result == -1){
            Toast.makeText(context,"fail to delete",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context,"delete success",Toast.LENGTH_SHORT).show();
        }
    }


}
