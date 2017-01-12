package com.example.georges.projectgolf;

import android.app.Activity;
import android.app.Instrumentation;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.R.attr.absListViewStyle;
import static android.R.attr.text;
import static android.R.attr.width;

// implements View.OnTouchListener
public class MainActivity extends Activity {
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    TextView textView;
    long compt=0;
    boolean stop =false;
    ArrayList list=new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textView = new TextView(this);
        textView.setText("Touché et déplacé votre doigt sur l'écran.");
        setContentView(textView);


        textView.setOnHoverListener(new View.OnHoverListener() {
            public boolean onHover(View view, MotionEvent motionEvent) {
                if (stop==false)
                {
                    EventMoveMouse event=new EventMoveMouse();
                    compt++;
                    event.setMouseX(motionEvent.getX());
                    event.setMouseY(motionEvent.getY());
                    event.setEventlong(compt);
                    list.add(event);
                    String position = motionEvent.getX() + "  " + motionEvent.getY();
                    textView.setText(position);
                }
                return false;
            }
        });


        textView.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {
                Toast toast = Toast.makeText(
                        getApplicationContext(),
                        "View touched",
                        Toast.LENGTH_LONG
                );
                stop=true;
                for(int i =0;i<list.size();i++)
                {
                    EventMoveMouse recupEvent=new EventMoveMouse();
                    recupEvent=(EventMoveMouse)list.get(i);
                    Log.e("VALEUR ",recupEvent.getEventLong()+"         "+recupEvent.getMouseX()+"       "+recupEvent.getMouseY());
                }
                toast.show();

                return true;
            }
        });

    }
}
