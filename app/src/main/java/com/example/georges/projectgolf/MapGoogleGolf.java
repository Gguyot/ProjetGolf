package com.example.georges.projectgolf;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Random;


/**
 * Created by Georges on 16/01/2017.
 */






//Appel deuxième interface
    /*
          shootDone=false;
                Intent secondeActivite = new Intent(MapGoogleGolf.this, MainActivity.class);
                secondeActivite.putExtra("TypeInterface","test");
                // On associe l'identifiant à notre intent
                startActivityForResult(secondeActivite, Ball_distance);
     */






//classe permettant d'afficher une carte (par défaut elle est centré sur l'australie)
public class MapGoogleGolf extends FragmentActivity implements OnMapReadyCallback, LocationListener,SensorEventListener {

    // device sensor manager
    private static SensorManager sensorService;
    private Sensor sensorOrientation;
    //Location locationTemp;
    double lat = 0, lng = 0;
    String provider;
    LocationManager locationManager;
    LatLng latLngBall,latLngHole;
    GoogleMap mMap;
    boolean shootDone=false;
    boolean holeCreated=false;
    float degree=0;

    // L'identifiant de notre requête
    public final static int Ball_distance =0;
    // L'identifiant de la chaîne de caractères qui contient le résultat de l'intent
    public final static String distance = "MapCall";
    public final static String direction = "MapCallV2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //----------------------------------------------------------------------------Capteur-----------------------------------------------------------
        sensorService = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorOrientation = sensorService.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        //----------------------------------------------------------------------------------------------------------------------------------------------



    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Positionnement sur un marqueur sur sydney
       /* LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marqueur sur Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        //Bouton permettant la téléportation vers la balle
        Button btnTP=(Button)findViewById(R.id.btnTp);
        btnTP.setOnTouchListener(new View.OnTouchListener() {
                                     public boolean onTouch(View v, MotionEvent event) {
                                         CameraPosition camera = new CameraPosition.Builder()
                                                 .target(latLngBall)
                                                 .zoom(16)
                                                 .build();
                                         mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));

                                         if (ActivityCompat.checkSelfPermission(MapGoogleGolf.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapGoogleGolf.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                             // TODO: Consider calling
                                             //    ActivityCompat#requestPermissions
                                             // here to request the missing permissions, and then overriding
                                             //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                             //                                          int[] grantResults)
                                             // to handle the case where the user grants the permission. See the documentation
                                             // for ActivityCompat#requestPermissions for more details.
                                             return true;
                                         }
                                         try {
                                             locationManager.removeUpdates(MapGoogleGolf.this);
                                             locationManager = null;
                                         }catch(Exception e )
                                         {

                                         }
                                            return true;
                                     }
                                 });



        mMap = map;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        // Enabling MyLocation Layer of Google Map
        map.setMyLocationEnabled(true);



            // Getting LocationManager object from System Service LOCATION_SERVICE
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location
            Location locationTemp = locationManager.getLastKnownLocation(provider);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);




            //Log.e("MapGoogleGolf", "POS GOOGLE LATITUDE : " + locationTemp.getLatitude() + "POS THOERIQUE : " + 50.166689 + " ||     POS GOOGLE LONGETITUDE : " + locationTemp.getLongitude() + "  POS THEORIQUE : " + 3.159122000000025 + "    ||||||||      " + locationTemp.getAccuracy());



        }



    @Override
    public void onLocationChanged(Location location) {

        // Getting Current Location
        if (location.getAccuracy() < 100 && shootDone==false) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition camera = new CameraPosition.Builder()
                    .target(loc)
                    .zoom(16)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
            latLngBall = ballPosition(location,120.0,90.0);
            shootDone=true;

            if(holeCreated==false)
            {
                Random rand = new Random();
                double randomDirection = rand.nextInt(360 - 0 + 1) + 0;
                double randomDistance = rand.nextInt(900 - 200 + 1) + 200;
                LatLng holePosition=ballPosition(location,randomDistance,randomDirection);
                mMap.addMarker(new MarkerOptions()
                        .position(holePosition)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                latLngHole=holePosition;

                holeCreated=true;
            }
            updateCameraBearing(mMap, degree);
        }

        Log.e("MapGoogleGolf","PositionChange  "+ location.getLatitude() + "     " + location.getLongitude() + "       " + location.getAccuracy()+"  "+location.getBearing());
        Log.e("MapGoogleGolf","latLngBall  "+ latLngBall.latitude + "     " + latLngBall.longitude);
    }


    /*Fonction permettant d'afficher la position de la balle  sur la carte
    A partir des données :
        - position du tireur
        - distance de parcours
    MODIFICATION à ajouter
        - Choisir la distance (par défaut 45°)
        - Utiliser la distance récupéré (actuellement la distance est en dur)
    SOURCE
        - Formule
            # http://www.movable-type.co.uk/scripts/latlong.html
        - Site permettant de vérifier les test
            # http://www.geomidpoint.com/destination/
    */
    public LatLng ballPosition(Location location,Double distance,Double direction) {
        Log.e("MapGoogleGolf", "DIRECTION FONCTION   : " + direction );
        double R = 6378.1;  // Rayon de la terre
        double brng = Math.toRadians(Math.round(direction)); // Direction
        double d = distance * 0.001; // Distance en m

        double lat = Math.toRadians(location.getLatitude()); // Position actuelle latitude radian
        double lon = Math.toRadians(location.getLongitude()); // Position actuelle Longétitude radian

        double lat2 = Math.asin(Math.sin(lat) * Math.cos(d / R) +
                Math.cos(lat) * Math.sin(d / R) * Math.cos(brng)); // Calcul de la latitude de la balle

        double lon2 = lon + Math.atan2(Math.sin(brng) * Math.sin(d / R) * Math.cos(lat),
                Math.cos(d / R) - Math.sin(lat) * Math.sin(lat2));// Calcul de la longétitude de la balle

        // Résultat
        double finalLat = Math.toDegrees(lat2);
        double finalLon = Math.toDegrees(lon2);
        // Ajout du marqueur à la position
        LatLng loc = new LatLng(finalLat, finalLon);
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(loc.latitude, loc.longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("Vous êtes ici"));
        Log.e("MapGoogleGolf", "Latitude   : " + finalLat + "               longitude    :  " + finalLon);
        return loc;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.e("Status changed", "STATUS CHANGE");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.e("Provider changed", "PROVIDER CHANGE");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.e("PROVIDER DISABLE", "PRovider disable");
    }

    private void updateCameraBearing(GoogleMap googleMap, float bearing) {
        if ( googleMap == null) return;
        CameraPosition camPos = CameraPosition
                .builder(
                        googleMap.getCameraPosition() // current Camera
                )
                .zoom(16)
                .target(latLngBall)
                .bearing(bearing)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // On vérifie tout d'abord à quel intent on fait référence ici à l'aide de notre identifiant
       if (requestCode == Ball_distance) {
            // On vérifie aussi que l'opération s'est bien déroulée
            if (resultCode == RESULT_OK) {
                // On affiche le bouton qui a été choisi
                Toast.makeText(this, "La distance que la balle va parcourir" + data.getStringExtra(distance), Toast.LENGTH_SHORT).show();
                Log.e("MapGoogleGolf","Distance   " +data.getStringExtra(distance));
                Log.e("MapGoogleGolf","Direction   " +data.getStringExtra(direction));
                Location lastBallLocation=new Location("");
                lastBallLocation.setLatitude(latLngBall.latitude);
                lastBallLocation.setLongitude(latLngBall.longitude);

                Log.e("MapGoogleGolf","latLngBall  BeforeResult"+ latLngBall.latitude + "     " + latLngBall.longitude);
                latLngBall = ballPosition(lastBallLocation,Double.parseDouble(data.getStringExtra(distance)),Double.parseDouble(data.getStringExtra(direction)));
                Log.e("MapGoogleGolf","latLngBall  AfterResult"+ latLngBall.latitude + "     " + latLngBall.longitude);

                mMap.addMarker(new MarkerOptions()
                        .position(latLngBall)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                CameraPosition camera = new CameraPosition.Builder()
                        .target(latLngBall)
                        .zoom(16)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));

                if(distanceBetween2point(latLngBall,latLngHole)<=0.022)
                {
                    Log.e("MapGoogleGolf","VIIIIIIIIIIIIIIIIIIIICCCCCTOOOOOOOOOOOOOOOIIIIIIIIRRRRRREEEEEEE");
                    finish();
                }
            }
        }
    }
    //Fonction calculant la distance etre deux points
    // Formule haversine
    // Site test (true)
    // http://www.sunearthtools.com/fr/tools/distance.php
    public double distanceBetween2point (LatLng origine,LatLng destination)
    {
        double R = 6372.8; // In kilometers

        double lat1 = origine.latitude;
        double lon1 = origine.longitude;
        //destination
        double lat2 = destination.latitude;
        double lon2 = destination.longitude;


        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (sensorOrientation != null) {
            sensorService.registerListener(this, sensorOrientation, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            Toast.makeText(MapGoogleGolf.this, "Not supported", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
         degree = Math.round(event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
