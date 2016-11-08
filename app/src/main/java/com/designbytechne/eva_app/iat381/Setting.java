package com.designbytechne.eva_app.iat381;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Eva on 2016-10-18.
 */

public class Setting extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView myList;
    MyDatabase db;
    SimpleCursorAdapter myAdapter;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        myList = (ListView)findViewById(R.id.listView);
        db = new MyDatabase(this);

        Intent intent = getIntent();
        //check if it's query
        if(intent.hasExtra("query")){
            String queryResult = intent.getStringExtra("query");
            cursor = db.getSelectedData(queryResult);
        }
        else{
            cursor = db.getData();

        }

        // For the cursor adapter, specify which columns go into which views
        String[] fromColumns = {Constants.PATTERN, Constants.GRAPHIC, Constants.THEME, Constants.THEMENAME};
        int[] toViews = {R.id.themeNameEntry, R.id.patternEntry, R.id.graphicEntry, R.id.themeEntry }; // The TextView in simple_list_item_1

        myAdapter = new SimpleCursorAdapter(this, R.layout.list_row, cursor, fromColumns, toViews, 2);
        myList.setAdapter(myAdapter);
        myList.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LinearLayout clickedRow = (LinearLayout) view;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        //PASS the saved value to spinner


    }


}
