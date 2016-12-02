package com.designbytechne.eva_app.iat381;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;

/**
 * Created by Eva on 2016-11-08.
 */

public class MyDatabase {
    private SQLiteDatabase db;
    private Context context;
    private final MyHelper helper;

    private CustomDrawableView myView;

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


    public Cursor getSelectedData(Long id)
    {
//        SQLiteDatabase db = helper.getWritableDatabase();
        db = helper.getWritableDatabase();

        String[] columns = {Constants.UID, Constants.PATTERN, Constants.GRAPHIC, Constants.THEME};
        String selection = Constants.UID + "=" +id;  //Constants.TYPE = 'type'
//        Cursor cursor = db.query(Constants.TABLE_NAME, columns, selection, null, null, null, null);

        Cursor cursor =  this.db.rawQuery("select * from THEMESAVED where _id" +  "='" + id + "'" , null);
        return cursor;

//        StringBuffer buffer = new StringBuffer();
//        while (cursor.moveToNext()) {
//
//            int index1 = cursor.getColumnIndex(Constants.PATTERN);
//            int index2 = cursor.getColumnIndex(Constants.GRAPHIC);
//            int index3 = cursor.getColumnIndex(Constants.THEME);
//
//            String patternName = cursor.getString(index1);
//            String graphicName = cursor.getString(index2);
//            String themeName = cursor.getString(index3);
//
//            buffer.append(patternName + " " + graphicName + "\n");
//        }
//        return buffer.toString();

    }


}