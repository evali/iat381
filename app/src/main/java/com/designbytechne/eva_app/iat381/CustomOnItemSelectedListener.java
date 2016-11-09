package com.designbytechne.eva_app.iat381;

/**
 * Created by riceyu on 2016-11-03.
 */
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class CustomOnItemSelectedListener extends Activity implements OnItemSelectedListener {

    public static Spinner patternSpinner, graphicSpinner, themeSpinner;
    public static String selected;
    CustomDrawableView mView;

    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);

        mView = new CustomDrawableView(getApplicationContext());
        mView.setPatternString("Nothing");
        Canvas canvas = new Canvas();
        mView.draw(canvas);
        mView.invalidate();

        patternSpinner = (Spinner)findViewById(R.id.patternSpinner);
        graphicSpinner = (Spinner)findViewById(R.id.graphicSpinner);
        themeSpinner = (Spinner)findViewById(R.id.themeSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.pattern_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        patternSpinner.setAdapter(adapter);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
        // Toast.makeText(parent.getContext(), "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();

        selected = parent.getItemAtPosition(pos).toString();
//        Toast.makeText(parent.getContext(), selected, Toast.LENGTH_SHORT).show();

        if(selected.equals("Circular")){
            Toast.makeText(parent.getContext(), "Circular True", Toast.LENGTH_SHORT).show();
            mView.setPatternString("Circular");
        }
        else if(selected.equals("Square")){
            Toast.makeText(parent.getContext(), "Square True", Toast.LENGTH_SHORT).show();
            mView.setPatternString("Square");
        }
        else{
            Toast.makeText(parent.getContext(), "None", Toast.LENGTH_SHORT).show();
//            mView.setPatternString("Nothing");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}