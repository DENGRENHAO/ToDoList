package com.example.to_do_list;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.muddzdev.styleabletoast.StyleableToast;

public class dataBaseHelper extends SQLiteOpenHelper {
    public static final String MYTABLE = "MYTABLE";
    public static final String TITLE = "TITLE";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String ID = "ID";
    public static final String PRIORITY = "PRIORITY";
    public static final String LIST = "LIST";
    public static final String TAG = "TAG";
    public static final String IFREMIND = "IFREMIND";
    public static final String IFALLDAY = "IFALLDAY";
    public static final String DATE = "DATE";
    public static final String TIME = "TIME";
    public static final String REMINDBEFORE = "REMINDBEFORE";
    private Context context;

    public dataBaseHelper(@Nullable Context context) {
        super(context, "data.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + MYTABLE + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE + " TEXT, " + DESCRIPTION + " TEXT, " + PRIORITY + " INT, "
                + LIST + " TEXT, " + TAG + " TEXT, " + IFREMIND + " INT, " + IFALLDAY + " INT, " + DATE + " TEXT, " + TIME + " TEXT, " + REMINDBEFORE + " INT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addOne(Model model) {
        if(model != null){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(TITLE, model.getTitle());
            cv.put(DESCRIPTION, model.getDescription());
            cv.put(PRIORITY, model.getPriority());
            cv.put(LIST, model.getList());
            cv.put(TAG, model.getTag());
            cv.put(IFREMIND, model.getIfRemind());
            cv.put(IFALLDAY, model.getIfAllDay());
            cv.put(DATE, model.getDate());
            cv.put(TIME, model.getTime());
            cv.put(REMINDBEFORE, model.getRemindBefore());

            db.insert(MYTABLE, null, cv);
        }
    }

     Cursor getAll() {
        String queryString = "SELECT * FROM " + MYTABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if (db!=null) {
            cursor = db.rawQuery(queryString,null);
        }
        return cursor;
    }

    Cursor getAllBySort(int groupPosition, int childPosition, String clickedItem, String yesterday, String today, String tomorrow,String nextWeek){
        String queryString, str;
        switch (groupPosition){
            case 0 | 1:
                str = "'" + clickedItem + "'";
                queryString = "SELECT * FROM " + MYTABLE + " WHERE " + LIST + " = " + str;
                break;
            case 2:
                switch (childPosition){
                    case 0:
                        queryString = "SELECT * FROM " + MYTABLE + " WHERE " + DATE + " = " + "'" + today + "'";
                        Log.v("thomas","today = "+today);
                        break;
                    case 1:
                        queryString = "SELECT * FROM " + MYTABLE + " WHERE " + DATE + " = " + "'" + tomorrow + "'";
                        break;
                    case 2:
                        queryString = "SELECT * FROM " + MYTABLE + " WHERE " + DATE + " BETWEEN '" + today + "'" + " AND " + "'" + nextWeek + "'";
                        break;
                    case 3:
                        queryString = "SELECT * FROM " + MYTABLE + " WHERE " + DATE + " IS NULL";
                        break;
                    case 4:
                        queryString = "SELECT * FROM " + MYTABLE + " WHERE " + DATE + " BETWEEN '" + "2000-00-00" + "'" + " AND " + "'" + yesterday + "'";
                        break;
                    default:
                        queryString = "SELECT * FROM " + MYTABLE + " WHERE " + DATE + " = " + today;
                }
                Log.v("thomas","today = "+today);
                Log.v("thomas","tomorrow = "+tomorrow);
                Log.v("thomas","nextweek = "+nextWeek);
                Log.v("thomas","yesterday = "+yesterday);
                break;
            case 3:
                switch (clickedItem){
                    case "High Priority":
                        str = "'" + 1 + "'";
                        break;
                    case "Medium Priority":
                        str = "'" + 2 + "'";
                        break;
                    case "Low Priority":
                        str = "'" + 3 + "'";
                        break;
                    case "No Priority":
                        str = "'" + 0 + "'";
                        break;
                    default:
                        str = "'" + 0 + "'";
                }
                queryString = "SELECT * FROM " + MYTABLE + " WHERE " + PRIORITY + " = " + str;
                break;
            case 4:
                str = "'" + "%" + "\"" +  clickedItem + "\""  + "%" + "'";
                queryString = "SELECT * FROM " + MYTABLE + " WHERE " + TAG + " LIKE " +  str;
                break;
            default:
                str = "'" + "Inbox" + "'";
                queryString = "SELECT * FROM " + MYTABLE + " WHERE " + LIST + " = " + str;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if (db!=null) {
            cursor = db.rawQuery(queryString,null);
        }
        return cursor;
    }

    Cursor getAllInCal(String date) {
        String str = "'" + date + "'";
        String queryString = "SELECT * FROM " + MYTABLE + " WHERE " + DATE + " = " + str;
        Log.v("thomas",queryString);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if (db!=null) {
            cursor = db.rawQuery(queryString,null);
        }
        return cursor;
    }

    void updateData(Model model, String id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TITLE, model.getTitle());
        cv.put(DESCRIPTION, model.getDescription());
        cv.put(PRIORITY, model.getPriority());
        cv.put(LIST, model.getList());
        Log.v("thomas","selectedList updateindbhelper = "+ model.getList());
        cv.put(TAG, model.getTag());
        cv.put(IFREMIND, model.getIfRemind());
        cv.put(IFALLDAY, model.getIfAllDay());
        cv.put(DATE, model.getDate());
        cv.put(TIME, model.getTime());
        cv.put(REMINDBEFORE, model.getRemindBefore());
        Log.v("thomas","id = "+id);

        long result = db.update(MYTABLE,cv,ID + "='" + id + "'",null);
        if(result != -1){
            StyleableToast.makeText(context, "Updated successfully",R.style.successToast).show();
        }
    }

    void deleteOneRow(String id,String action){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(MYTABLE,ID + "='" + id + "'",null);
        if(result != -1){
            if(action == "delete"){
                StyleableToast.makeText(context, "Deleted successfully",R.style.successToast).show();
            }else {
                StyleableToast.makeText(context, "Congrats! You've completed one more task!",R.style.congratsToast).show();
            }
        }
    }

    long getTableSize(){
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, MYTABLE );
        db.close();
        return count;
    }
}
