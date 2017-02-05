package com.example.georges.projectgolf;

import android.app.Activity;


import android.content.Intent;
import android.graphics.Color;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends Activity implements SensorEventListener {
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    long compt = 0;
    float min = 0;
    boolean stop = false;
    //instanciation de la liste des coordonnées
    ArrayList<EventMoveMouse> list = new ArrayList<EventMoveMouse>();
    //variable orientation
    float direction = 0;
    //Coefficient de puissance du tir
    int powerShoot = 30;
    // device sensor manager
    private static SensorManager sensorService;
    private Sensor sensorOrientation;





    private static double floatBearing = 0;
   // ----------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Déclaration des variables liées aux composants graphique
        final TextView tvChoose = (TextView) findViewById(R.id.tvChoose);
        final TextView tvXY = (TextView) findViewById(R.id.tvXY);
        final ImageView ivGround = (ImageView) findViewById(R.id.ivGround);
        final ImageButton ibStrong = (ImageButton) findViewById(R.id.ibStrong);
        final ImageButton ibSoft = (ImageButton) findViewById(R.id.ibSoft);

        ibStrong.setColorFilter(Color.GREEN);
        tvChoose.setText("Puissance forte");
        tvXY.setText("Veuillez faire un mouvement sur votre écran");

        ibStrong.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                ibSoft.setColorFilter(Color.BLACK);
                ibStrong.setColorFilter(Color.GREEN);
                powerShoot = 30;
                tvChoose.setText("Puissance forte");
                return true;
            }
        });
        ibSoft.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                ibSoft.setColorFilter(Color.GREEN);
                ibStrong.setColorFilter(Color.BLACK);
                powerShoot = 10;
                tvChoose.setText("Puissance faible");
                return true;
            }
        });

        //----------------------------------------------------------------------------Capteur-----------------------------------------------------------
        sensorService = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorOrientation = sensorService.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        //----------------------------------------------------------------------------------------------------------------------------------------------

        //lancement du service gps
        // Intent servIntent = new Intent(this, ServiceGPS.class);
        //startService(servIntent);


        //le clic de souris arrete l'enregistrement des évènements dans l'arraylist et renvoie son contenu dans la console
        //Affiche une nouvelle interface avec un graph
        // ATTENTION LES COORDONNEES DES X DOIVENT ETRE CROISSANT
        ivGround.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        EventMoveMouse recupEvent = new EventMoveMouse();
                        //Affichage sur la console les coordonnées enregistrées
                        synchronized (list) {
                            Iterator i = list.iterator(); // Must be in synchronized block
                            while (i.hasNext()) {
                                recupEvent = (EventMoveMouse) i.next();
                                // Log.e("VALEUR ", recupEvent.getEventLong() + "         posX = " + recupEvent.getMouseX() + "       posY = " + recupEvent.getMouseY() + "                       " + recupEvent.getEventDate());
                            }
                        }
                        //Log.e("ACTION_UP", " je suis le test");


                        //-----------------------------------------------------------Calcul(dérivé de la classe grapMousePos voué à disparaitre------------------------------------------------------------------------------------
                        //Affichage du temps d'execution du mouvement complet
                        EventMoveMouse recupEventFirst, recupEventLast = new EventMoveMouse();
                        recupEventFirst = list.get(0);
                        recupEventLast = list.get(list.size() - 1);

                        double resultTimeVar = 0;
                        resultTimeVar = recupEventLast.getEventDate() - recupEventFirst.getEventDate();
                        //------------------------------------------------------------------------------------------
                        //Affichage de la vitesse en pixel/ms
                        EventMoveMouse previousEvent, currentEvent = new EventMoveMouse();
                        double calDistance = 0, sumSpeed = 0, avgSpeed = 0;
                        ArrayList<Double> listDistance = new ArrayList<>();
                        for (int i = 1; i < list.size(); i++) {
                            previousEvent = list.get(i - 1);
                            currentEvent = list.get(i);

                            //calcul distance entre deux points racine racine((xB-xA)³+(yB-yA)²)
                            calDistance = Math.sqrt(Math.pow((currentEvent.getMouseX() - previousEvent.getMouseX()), 2.0) + Math.pow((currentEvent.getMouseY() - previousEvent.getMouseY()), 2.0));

                            //calcul de la vitesse courante entre deux points (PIXEL/MS)
                            //sumSpeed+=(calDistance/(currentEvent.getEventDate()-previousEvent.getEventDate()));

                            //tentative M/S
                            sumSpeed += ((calDistance * 0.000265) / ((currentEvent.getEventDate() - previousEvent.getEventDate()) * 0.001));

                            /*INFO :
                            https://www.unitjuggler.com/convertir-time-de-ms-en-s.html?val=125
                            Relation de base : 1 ms = 0.001 sec.

                            http://endmemo.com/sconvert/meterpixel.php
                           1 px = 0.000265 m

                           https://www.ilemaths.net/sujet-comment-passer-de-m-s-en-km-h-37126.html
                           m/s * 3.6 = km/h            */

                            //Log d'affichage de différent paramètre
                            //Log.e("DISTANCE A=>B",previousEvent.getEventId()+" ===> "+currentEvent.getEventId()+"        Val   "+calDistance);   Affichage de la distance entre deux point à la suite dans la liste
                            //Log.i("Val Boucle(t-1)(t)","previous  : "+previousEvent.getEventId()+"       current  : "+currentEvent.getEventId());       Affichage  des objets vérification
                        }


                        String message = "";
                        //calcul de la vitesse moyenne en m/s
                        avgSpeed = (sumSpeed / (list.size() - 1)) * powerShoot;
                        message = "Moyenne de la vitesse  \n:" + avgSpeed + "   m/s";

                        //Calcul de la distance qu'aura parcouru la balle
                        //Source
                        //Simulateur
                        //https://www.edumedia-sciences.com/fr/media/660-chute-libre-parabolique
                        //Calcul
                        //http://www.reviz.fr/terminale/physique/applications-lois-dynamique/mouvement-parabolique-chute-libre.html
                        double distanceball = 0;
                        distanceball = (Math.pow(avgSpeed, 2.0) * Math.sin(2 * 45) / 9.81);

                        //conversion m/s en km/h
                        avgSpeed *= 3.6;

                        Log.e("MainActivity", "Distance  " + Double.toString(distanceball));
                        //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                        try {
                            Intent result = getIntent();
                            if (!result.getStringExtra("TypeInterface").isEmpty()) {
                                result.putExtra(MapGoogleGolf.distance, Double.toString(distanceball));
                                result.putExtra(MapGoogleGolf.direction, Double.toString(direction));
                                setResult(RESULT_OK, result);
                                finish();
                            }
                            return true;

                        } catch (Exception e) {
                            //Appel à l'interface possèdant le graph à afficher avec comme paramètre la liste des coordonnées
                            Intent objIndent = new Intent(MainActivity.this, MapGoogleGolf.class);
                            objIndent.putExtra("Distance", distanceball);

                            startActivity(objIndent);


                            // e.printStackTrace();
                            return false;
                        }


                    case MotionEvent.ACTION_MOVE:
                        EventMoveMouse eventObject = new EventMoveMouse();
                        //Permet de créer un identifiant unique pour chaque objet
                        compt++;

                        //Enregistrement sur l'objet courant id,posX,posY,timer
                        eventObject.setMouseX(event.getX());
                        eventObject.setMouseY(event.getY());
                        eventObject.setEventlong(compt);

                        double start_time = System.currentTimeMillis();
                        eventObject.setEventDate(start_time);

                        list.add(eventObject);
                        //Mise à jour de l'affichage de la position du curseur
                        String position = event.getX() + "  " + event.getY();
                        tvXY.setText(position);
                }
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorOrientation != null) {
            sensorService.registerListener(this, sensorOrientation, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            Toast.makeText(MainActivity.this, "Not supported", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorService.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float degree = Math.round(event.values[0]);
        TextView tvHeading = (TextView) findViewById(R.id.tvOrientation);

        direction = degree;

        tvHeading.setText(Float.toString(degree) + " degrees  ");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}