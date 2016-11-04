package com.designbytechne.eva_app.iat381;

/**
 * Created by riceyu on 2016-11-03.
 */
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class CustomOnItemSelectedListener extends Activity implements OnItemSelectedListener {

    CustomDrawableView mView;

    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);

        mView = new CustomDrawableView(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
        // Toast.makeText(parent.getContext(), "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();

        if(parent.getSelectedItem().toString() == "Circular"){
            mView.setCircular(true);
            Toast.makeText(parent.getContext(), "Circular True", Toast.LENGTH_SHORT).show();
        }

        if(parent.getSelectedItem().toString() == "Square"){
            mView.setSquare(true);
            Toast.makeText(parent.getContext(), "Square True", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}