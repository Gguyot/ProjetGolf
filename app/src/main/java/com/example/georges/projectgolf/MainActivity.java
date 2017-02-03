package com.example.georges.projectgolf;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

import java.util.Iterator;

// implements View.OnTouchListener
public class MainActivity extends Activity {
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    long compt=0;
    float min=0;
    boolean stop =false;
    //instanciation de la liste des coordonnées
    ArrayList<EventMoveMouse> list=new ArrayList<EventMoveMouse>();

    int powerShoot=30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Déclaration des variables liées aux composants graphique
        final TextView tvChoose=(TextView)findViewById(R.id.tvChoose);
        final TextView tvXY=(TextView)findViewById(R.id.tvXY);
        final ImageView ivGround=(ImageView)findViewById(R.id.ivGround);
        final ImageButton ibStrong =(ImageButton)findViewById(R.id.ibStrong);
        final ImageButton ibSoft=(ImageButton)findViewById(R.id.ibSoft);

        ibStrong.setColorFilter(Color.GREEN);
        tvChoose.setText("Puissance forte");
        tvXY.setText("Veuillez faire un mouvement sur votre écran");

        ibStrong.setOnTouchListener(new View.OnTouchListener() {
                                        public boolean onTouch(View v, MotionEvent event) {
                                            ibSoft.setColorFilter(Color.BLACK);
                                            ibStrong.setColorFilter(Color.GREEN);
                                            powerShoot=30;
                                            tvChoose.setText("Puissance forte");
                                            return true;
                                        }
                                    });
        ibSoft.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                ibSoft.setColorFilter(Color.GREEN);
                ibStrong.setColorFilter(Color.BLACK);
                powerShoot=10;
                tvChoose.setText("Puissance faible");
                return true;
            }
        });

        //lancement du service gps
       // Intent servIntent = new Intent(this, ServiceGPS.class);
        //startService(servIntent);



        //le clic de souris arrete l'enregistrement des évènements dans l'arraylist et renvoie son contenu dans la console
        //Affiche une nouvelle interface avec un graph
        // ATTENTION LES COORDONNEES DES X DOIVENT ETRE CROISSANT
        ivGround.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {
                final int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                              EventMoveMouse recupEvent=new EventMoveMouse();
                        //Affichage sur la console les coordonnées enregistrées
                        synchronized(list) {
                            Iterator i = list.iterator(); // Must be in synchronized block
                            while (i.hasNext()) {
                                recupEvent = (EventMoveMouse) i.next();
                                Log.e("VALEUR ", recupEvent.getEventLong() + "         posX = " + recupEvent.getMouseX() + "       posY = " + recupEvent.getMouseY()+"                       "+recupEvent.getEventDate());
                            }
                        }
                        Log.e("ACTION_UP"," je suis le test");

                        //Appel à l'interface possèdant le graph à afficher avec comme paramètre la liste des coordonnées
                        Intent objIndent = new Intent(MainActivity.this,graphMousePos.class);
                        objIndent.putExtra("power",powerShoot);
                       // Collections.sort(list, null);
                        objIndent.putExtra("Array_pos",list);

                        startActivity(objIndent);

                    case MotionEvent.ACTION_MOVE:
                        EventMoveMouse eventObject=new EventMoveMouse();
                        //Permet de créer un identifiant unique pour chaque objet
                        compt++;

                        //Enregistrement sur l'objet courant id,posX,posY,timer
                        eventObject.setMouseX(event.getX());
                        eventObject.setMouseY(event.getY());
                        eventObject.setEventlong(compt);

                        double start_time = System.currentTimeMillis();
                        eventObject.setEventDate(start_time);
                        //--------------------------------------------------------------------------------------------
                        /*
                        String format = "H:mm:ss:ms";
                        java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat( format );
                        java.util.Date date = new java.util.Date();

                        eventObject.setEventDate(formater.format(date));
                        //---------------------------------------------------------------------------------------------
                        // CODE TEMPORAIRE AFIN D'ÉVITER L'ERREUR DU RETOUR EN ARRIERE
                        /*if(min<v.getX()) {
                            //Ajout de l'objet dans la liste

                            min=v.getX();
                        }*/
                        list.add(eventObject);
                        //Mise à jour de l'affichage de la position du curseur
                        String position = event.getX() + "  " + event.getY();
                        tvXY.setText(position);
                }
                return true;
            }
        });

    }
}