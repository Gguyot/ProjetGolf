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
    float min=0;
    boolean stop =false;
    //instanciation de la liste des coordonnées
    ArrayList<EventMoveMouse> list=new ArrayList<EventMoveMouse>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Affichage des coordonnées courant du curseur de la souris dans un libellé
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
                    //Permet de créer un identifiant unique pour chaque objet
                    compt++;
                    //Enregistrement sur l'objet courant id,posX,posY,timer
                    event.setMouseX(motionEvent.getX());
                    event.setMouseY(motionEvent.getY());
                    event.setEventlong(compt);

                    String format = "H:mm:ss:ms";
                    java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat( format );
                    java.util.Date date = new java.util.Date();

                    event.setEventDate(formater.format(date));
                    // CODE TEMPORAIRE AFIN D'ÉVITER L'ERREUR DU RETOUR EN ARRIERE
                    if(min<motionEvent.getX()) {
                        //Ajout de l'objet dans la liste
                        list.add(event);
                        min=motionEvent.getX();
                    }
                    //Mise à jour de l'affichage de la position du curseur
                    String position = motionEvent.getX() + "  " + motionEvent.getY();
                    textView.setText(position);
                }
                return false;
            }
        });
        //le clic de souris arrete l'enregistrement des évènements dans l'arraylist et renvoie son contenu dans la console
        //Affiche une nouvelle interface avec un graph
        // ATTENTION LES COORDONNEES DES X DOIVENT ETRE CROISSANT
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
                        //Affichage sur la console les coordonnées enregistrées
                        synchronized(list) {
                            Iterator i = list.iterator(); // Must be in synchronized block
                            while (i.hasNext()) {
                                recupEvent = (EventMoveMouse) i.next();
                                Log.e("VALEUR ", recupEvent.getEventLong() + "         posX = " + recupEvent.getMouseX() + "       posY = " + recupEvent.getMouseY()+"                       "+recupEvent.getEventDate());
                            }
                        }
                        toast.show();

                }
                //Appel à l'interface possèdant le graph à afficher avec comme paramètre la liste des coordonnées
                Intent objIndent = new Intent(MainActivity.this,graphMousePos.class);
                objIndent.putExtra("Array_pos",list);
                startActivity(objIndent);


                return true;
            }
        });

    }
}