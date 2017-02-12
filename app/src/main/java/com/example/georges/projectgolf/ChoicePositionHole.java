package com.example.georges.projectgolf;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Georges on 11/02/2017.
 */

public class ChoicePositionHole extends Activity{
    Intent intentMap;
    @Override
      protected void onCreate(Bundle savedInstanceState) {

// TODO Auto-generated method stub

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choicehole);

        Button btnRandom=(Button)findViewById(R.id.btnRandom);
        Button btnManual=(Button)findViewById(R.id.btnManual);

        intentMap = getIntent();

        btnRandom.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                intentMap.putExtra(MapGoogleGolf.position,true);
                setResult(RESULT_OK, intentMap);
                finish();
                return true;
            }
        });

        btnManual.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                intentMap.putExtra(MapGoogleGolf.position,false);
                setResult(RESULT_OK, intentMap);
                finish();
                return true;
            }
        });
    }

}
