package com.designbytechne.eva_app.iat381;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Eva on 2016-11-08.
 */

public class MyDatabase {
    private SQLiteDatabase db;
    private Context context;
    private final MyHelper helper;

    public MyDatabase (Context c){
        context = c;
        helper = new MyHelper(context);
    }

    public long insertData (String pattern, String graphic, String theme, String themeName)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.PATTERN, pattern);
        contentValues.put(Constants.GRAPHIC, graphic);
        contentValues.put(Constants.THEME, theme);
        contentValues.put(Constants.THEMENAME, themeName);

        long id = db.insert(Constants.TABLE_NAME, null, contentValues);
        return id;
    }

    public Cursor getData()
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        String[] columns = {Constants.UID, Constants.PATTERN, Constants.GRAPHIC, Constants.THEME, Constants.THEMENAME};
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }


    public Cursor getSelectedData(String type)
    {
        //select plants from database of type 'herb'
//        SQLiteDatabase db = helper.getWritableDatabase();
        db = helper.getWritableDatabase();

//

        String[] columns = {Constants.UID, Constants.PATTERN, Constants.GRAPHIC, Constants.THEME};
        String selection = Constants.PATTERN + "='" +type+ "'";  //Constants.TYPE = 'type'
        Cursor cursor = db.query(Constants.TABLE_NAME, columns, selection, null, null, null, null);
        return cursor;




//
//        StringBuffer buffer = new StringBuffer();
//        while (cursor.moveToNext()) {
//
//            int index1 = cursor.getColumnIndex(Constants.NAME);
//            int index2 = cursor.getColumnIndex(Constants.TYPE);
//
//            String plantName = cursor.getString(index1);
//            String plantType = cursor.getString(index2);
//            buffer.append(plantName + " " + plantType + "\n");
//        }
////        return buffer.toString();
    }


}