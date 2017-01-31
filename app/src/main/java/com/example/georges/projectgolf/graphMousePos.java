package com.example.georges.projectgolf;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Georges on 14/01/2017.
 */
// Classe permettant d'afficher le graphe  (abscisse posXcurseur    ||  ordonnée  posYcurseur)
public class graphMousePos extends Activity {
    PointsGraphSeries<DataPoint> series;
    EventMoveMouse recupEvent=new EventMoveMouse();
    protected void onCreate(Bundle savedInstaceState)
    {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.graphmouse);

        //récupère la liste en paramètre
        Intent iobjet = getIntent();
        ArrayList<EventMoveMouse> list = (ArrayList<EventMoveMouse>) iobjet
                .getSerializableExtra("Array_pos");



        GraphView graph = (GraphView) findViewById(R.id.graph);
        float min =0;
        //Ajout des différents par rapport aux coordonnées dans la liste
        synchronized(list) {
            Iterator i = list.iterator(); // Must be in synchronized block
            while (i.hasNext()) {
                series =new PointsGraphSeries<DataPoint>();
                recupEvent = (EventMoveMouse) i.next();
                Log.e("VALEUR ", recupEvent.getEventLong() + "         posX = " + recupEvent.getMouseX() + "       posY = " + recupEvent.getMouseY()+"                       "+recupEvent.getEventDate());
                series.appendData(new DataPoint(recupEvent.getMouseX(),recupEvent.getMouseY()),true,200);


                if (min > recupEvent.getMouseY())
                {
                    graph.addSeries(series);
                    series.setShape(PointsGraphSeries.Shape.RECTANGLE);
                    series.setColor(Color.RED);

                }else
                {
                    graph.addSeries(series);
                    series.setShape(PointsGraphSeries.Shape.TRIANGLE);
                    series.setColor(Color.GREEN);
                }
                min=recupEvent.getMouseY();
            }
        }


        //------------------------------------------------------------------------------------------
        //Affichage du temps d'execution du mouvement complet
        EventMoveMouse recupEventFirst,recupEventLast=new EventMoveMouse();
        recupEventFirst= list.get(0);
        recupEventLast=list.get(list.size()-1);

        double resultTimeVar=0;
        resultTimeVar=recupEventLast.getEventDate()-recupEventFirst.getEventDate();
        TextView resultTime =(TextView) findViewById(R.id.tvTime);
        resultTime.setText("le temps d'execution du geste   :  "+resultTimeVar+"ms");
        //------------------------------------------------------------------------------------------
        //Affichage de la vitesse en pixel/ms
        EventMoveMouse previousEvent,currentEvent = new EventMoveMouse();
        double calDistance=0,sumSpeed=0,avgSpeed=0;
        ArrayList<Double> listDistance=new ArrayList<>();
        for(int i=1;i<list.size();i++)
        {
            previousEvent=list.get(i-1);
            currentEvent=list.get(i);

            //calcul distance entre deux points racine racine((xB-xA)³+(yB-yA)²)
            calDistance=Math.sqrt(Math.pow((currentEvent.getMouseX()-previousEvent.getMouseX()),2.0)+Math.pow((currentEvent.getMouseY()-previousEvent.getMouseY()),2.0));

            //calcul de la vitesse courante entre deux points (PIXEL/MS)
            //sumSpeed+=(calDistance/(currentEvent.getEventDate()-previousEvent.getEventDate()));

            //tentative M/S
            sumSpeed+=((calDistance*0.000265)/((currentEvent.getEventDate()-previousEvent.getEventDate())*0.001));

            /*INFO :
            https://www.unitjuggler.com/convertir-time-de-ms-en-s.html?val=125
            Relation de base : 1 ms = 0.001 sec.

            http://endmemo.com/sconvert/meterpixel.php
           1 px = 0.000265 m

           https://www.ilemaths.net/sujet-comment-passer-de-m-s-en-km-h-37126.html
           m/s * 3.6 = km/h
             */



            //Log d'affichage de différent paramètre
            //Log.e("DISTANCE A=>B",previousEvent.getEventId()+" ===> "+currentEvent.getEventId()+"        Val   "+calDistance);   Affichage de la distance entre deux point à la suite dans la liste
            //Log.i("Val Boucle(t-1)(t)","previous  : "+previousEvent.getEventId()+"       current  : "+currentEvent.getEventId());       Affichage  des objets vérification
        }


        String message="";
        //calcul de la vitesse moyenne en m/s
        avgSpeed=(sumSpeed/(list.size()-1))*iobjet.getIntExtra("power",0);
        message = "Moyenne de la vitesse  \n:"+avgSpeed+"   m/s";

        //Calcul de la distance qu'aura parcouru la balle
        //Source
        //Simulateur
        //https://www.edumedia-sciences.com/fr/media/660-chute-libre-parabolique
        //Calcul
        //http://www.reviz.fr/terminale/physique/applications-lois-dynamique/mouvement-parabolique-chute-libre.html
        double distanceball=0;
        distanceball=(Math.pow(avgSpeed,2.0)*Math.sin(2*45)/9.81);
        TextView resultDistance =(TextView)findViewById(R.id.tvDistance);
        resultDistance.setText("Distance parcouru   : "+distanceball+" m");

        //conversion m/s en km/h
        avgSpeed*=3.6;
        message+="    ||    "+avgSpeed+"  km/h";
        //Affichage
        TextView resultSpeed=(TextView)findViewById(R.id.tvAvgSpeed);
        resultSpeed.setText(message);


        //------------------------------------------------------------------------------------------
        graph.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {

                //Appel à l'interface possèdant le graph à afficher avec comme paramètre la liste des coordonnées
                Intent objIndent = new Intent(graphMousePos.this,MapGoogleGolf.class);
                startActivity(objIndent);

                return true;
            }
        });
        // A vérifier la pertinence de ce code
        //list.removeAll(list);
    }
}

