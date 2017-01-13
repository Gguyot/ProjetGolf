package com.example.georges.projectgolf;

import android.app.Activity;
import android.app.Instrumentation;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

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

        //Récupère les éléments et les stocks dans une arraylist( id <= bientot remplacé
        // par time, posX,posY)
        textView.setOnHoverListener(new View.OnHoverListener() {
            public boolean onHover(View view, MotionEvent motionEvent) {
                if (stop==false)
                {

                    EventMoveMouse event=new EventMoveMouse();
                    compt++;
                    event.setMouseX(motionEvent.getX());
                    event.setMouseY(motionEvent.getY());
                    event.setEventlong(compt);

                    String format = "H:mm:ss:ms";
                    java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat( format );
                    java.util.Date date = new java.util.Date();

                    event.setEventDate(formater.format(date));


                    list.add(event);
                    String position = motionEvent.getX() + "  " + motionEvent.getY();
                    textView.setText(position);
                }
                return false;
            }
        });
        //le clic de souris arrete l'enregistrement des évènements dans l'arraylist et renvoie son contenu
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
                final int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        EventMoveMouse recupEvent=new EventMoveMouse();
                        synchronized(list) {
                            Iterator i = list.iterator(); // Must be in synchronized block
                            while (i.hasNext()) {
                                recupEvent = (EventMoveMouse) i.next();
                                Log.e("VALEUR ", recupEvent.getEventLong() + "         posX = " + recupEvent.getMouseX() + "       posY = " + recupEvent.getMouseY()+"                       "+recupEvent.getEventDate());
                            }
                        }
                        toast.show();

                }


                /*
                for(int i =0;i<list.size();i++)
                {
                    EventMoveMouse recupEvent=new EventMoveMouse();
                    recupEvent=(EventMoveMouse)list.get(i);
                    Log.e("VALEUR ",recupEvent.getEventLong()+"         "+recupEvent.getMouseX()+"       "+recupEvent.getMouseY());
                }*/

                return true;
            }
        });

    }
}
