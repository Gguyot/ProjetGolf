package com.example.georges.projectgolf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Georges on 14/01/2017.
 */
// Classe permettant d'afficher le graphe  (abscisse posXcurseur    ||  ordonnée  posYcurseur)
public class graphMousePos  extends Activity {
    LineGraphSeries<DataPoint> series;
    EventMoveMouse recupEvent=new EventMoveMouse();
    protected void onCreate(Bundle savedInstaceState)
    {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_main);

        //récupère la liste en paramètre
        Intent iobjet = getIntent();
        ArrayList<EventMoveMouse> list = (ArrayList<EventMoveMouse>) iobjet
                .getSerializableExtra("Array_pos");



        GraphView graph = (GraphView) findViewById(R.id.graph);
        series =new LineGraphSeries<DataPoint>();
        //Ajout des différents par rapport aux coordonnées dans la liste
        synchronized(list) {
            Iterator i = list.iterator(); // Must be in synchronized block
            while (i.hasNext()) {
                recupEvent = (EventMoveMouse) i.next();
                Log.e("VALEUR ", recupEvent.getEventLong() + "         posX = " + recupEvent.getMouseX() + "       posY = " + recupEvent.getMouseY()+"                       "+recupEvent.getEventDate());
                series.appendData(new DataPoint(recupEvent.getMouseX(),recupEvent.getMouseY()),true,200);
            }
        }
        graph.addSeries(series);
    }
}

