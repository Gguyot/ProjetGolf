package com.example.georges.projectgolf;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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
public class MapGoogleGolf extends FragmentActivity implements OnMapReadyCallback, LocationListener, SensorEventListener,NavigationView.OnNavigationItemSelectedListener {

    // device sensor manager
    private static SensorManager sensorService;
    private Sensor sensorOrientation;
    //Location locationTemp;
    double lat = 0, lng = 0;
    String provider;
    LocationManager locationManager;
    LatLng latLngBall, latLngHole, latLngPlayer;
    GoogleMap mMap;
    boolean shootDone = false;
    boolean holeCreated = false;
    boolean zoomDone = false;
    boolean shootEnable = false;
    boolean tpActivate = false;
    float degree = 0;
    boolean bRandomPosition = true;

    ArrayList<Marker> listMarker = new ArrayList<Marker>();
    Marker markerHole, markerBall;

    Button btnShoot;
    Button btnTP;
    Button btnRetry;

    TextView tvDistance;

    ProgressBar pbLoading;


    // L'identifiant de notre requête
    public final static int Ball_distance = 0;
    // L'identifiant de la chaîne de caractères qui contient le résultat de l'intent
    public final static String distance = "MapCall";
    public final static String direction = "MapCallV2";
    public final static String position = "MapCallV3";

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
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        //----------------------------------------------------------------------------------------------------------------------------------------------
        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);


        //------------------------------------------------MENU----------------------------------------------------------------------------------


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //---------------------------------------------------------------------------------------------------------------------------------

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        //Bouton permettant la téléportation vers la balle
        btnTP = (Button) findViewById(R.id.btnTp);
        btnTP.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                tpActivate = true;
                btnShoot.setVisibility(View.VISIBLE);
                return true;
            }
        });
        //Bouton permettant de tirer
        btnShoot = (Button) findViewById(R.id.btnShoot);
        btnShoot.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        Intent secondeActivite = new Intent(MapGoogleGolf.this, MainActivity.class);
                        secondeActivite.putExtra("TypeInterface", "test");
                        secondeActivite.putExtra("direction", degree);
                        Log.e("MApGoogleGolf", "Envoie vers la deuxième");
                        // On associe l'identifiant à notre intent
                        startActivityForResult(secondeActivite, Ball_distance);
                }
                return true;

            }
        });
        //Bouton permettant de rejouer un tir
        btnRetry = (Button) findViewById(R.id.btnRetry);
        btnRetry.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                final int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        if (listMarker.size() == 1) {
                            listMarker.get(0).remove();
                            btnShoot.setVisibility(View.VISIBLE);
                            tvDistance.setText("");
                        } else {

                            latLngBall = new LatLng(listMarker.get(listMarker.size() - 2).getPosition().latitude, listMarker.get(listMarker.size() - 2).getPosition().longitude);
                            Log.e("MapGoogleGolf", " MARQUEUR " + listMarker.get(listMarker.size() - 1).getId());
                            listMarker.get(listMarker.size() - 1).remove();
                            listMarker.remove(listMarker.size() - 1);

                            mMap.clear();
                            //Définition des markers
                            for (int i = 0; i < listMarker.size(); i++) {

                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(listMarker.get(i).getPosition().latitude, listMarker.get(i).getPosition().longitude))
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                        .title("Tir " + i));
                            }
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latLngHole.latitude, latLngHole.longitude))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                    .title("trou"));

                            Double message = 1000 * distanceBetween2point(latLngBall, latLngHole);
                            tvDistance.setText(String.format("%.2f", message) + "  m");
                        }
                }
                return true;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                // TODO Auto-generated method stub
                if (bRandomPosition == false && holeCreated == false) {
                    mMap.addMarker(new MarkerOptions()
                            .position(point)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))

                    );
                    latLngHole = point;
                    holeCreated = true;
                }
            }
        });


        //Définition de la visibilité des boutons situation initiale


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


        //Code permettant de demander à l'utilisateur d'autorisé le gps
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Veuillez activé votre gps", Toast.LENGTH_LONG).show();
            Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

            startActivityForResult(i, 1);
        }

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        // Gérer cette exception proprement (try/catch)
        Location locationTemp = locationManager.getLastKnownLocation(provider);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);

        Intent iPositionHole = new Intent(MapGoogleGolf.this, ChoicePositionHole.class);
        iPositionHole.putExtra("choice", bRandomPosition);
        // On associe l'identifiant à notre intent
        startActivityForResult(iPositionHole, 3);
        //Log.e("MapGoogleGolf", "POS GOOGLE LATITUDE : " + locationTemp.getLatitude() + "POS THOERIQUE : " + 50.166689 + " ||     POS GOOGLE LONGETITUDE : " + locationTemp.getLongitude() + "  POS THEORIQUE : " + 3.159122000000025 + "    ||||||||      " + locationTemp.getAccuracy());

        btnShoot.setVisibility(View.GONE);
        btnTP.setVisibility(View.GONE);
        btnRetry.setVisibility(View.GONE);
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.e("MapGoogleGolf", "Tpactivate  " + tpActivate);

        // Getting Current Location
        if (location.getAccuracy() < 100) {
            pbLoading.setVisibility(View.GONE);

            latLngPlayer = new LatLng(location.getLatitude(), location.getLongitude());


            if (holeCreated == false && bRandomPosition == true) {
                Random rand = new Random();
                float randomDirection = rand.nextInt(360 - 0 + 1) + 0;
                double randomDistance = rand.nextInt(900 - 200 + 1) + 200;
                LatLng holePosition = ballPosition(location, randomDistance, randomDirection);
                markerHole = mMap.addMarker(new MarkerOptions()
                        .position(holePosition)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                latLngHole = holePosition;


                latLngBall = new LatLng(location.getLatitude(), location.getLongitude());
                btnShoot.setVisibility(View.VISIBLE);
                holeCreated = true;
            }
            if (bRandomPosition == false && holeCreated == false)
                latLngBall = new LatLng(location.getLatitude(), location.getLongitude());


            if (tpActivate == false) {
                updateCameraBearing(mMap, degree, latLngPlayer, 16);

                if (distanceBetween2point(latLngPlayer, latLngBall) <= 0.022 && holeCreated == true) {
                    btnShoot.setVisibility(View.VISIBLE);
                    tpActivate = true;
                }
            } else {
                if (listMarker.size() == 0) {
                    updateCameraBearing(mMap, degree, latLngPlayer, 16);
                } else {
                    updateCameraBearing(mMap, degree, latLngBall, 16);
                }
                Log.e("MapGoogleGolf", "Player Position  " + latLngPlayer.latitude + "     " + latLngPlayer.longitude + "       " + location.getAccuracy());
                Log.e("MapGoogleGolf", "Ball Position  " + latLngBall.latitude + "     " + latLngBall.longitude);
            }
        } else {
            pbLoading.setVisibility(View.VISIBLE);
        }


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //btnRetry.setVisibility(View.VISIBLE);
        // On vérifie tout d'abord à quel intent on fait référence ici à l'aide de notre identifiant
        if (requestCode == Ball_distance) {
            // On vérifie aussi que l'opération s'est bien déroulée
            if (resultCode == RESULT_OK) {
                btnTP.setVisibility(View.VISIBLE);
                btnShoot.setVisibility(View.VISIBLE);
                // On affiche le bouton qui a été choisi
                Toast.makeText(this, "La distance que la balle va parcourir" + data.getStringExtra(distance), Toast.LENGTH_SHORT).show();
                Log.e("MapGoogleGolf", "Distance   " + data.getStringExtra(distance));
                Log.e("MapGoogleGolf", "Direction   " + data.getStringExtra(direction));
                Location lastBallLocation = new Location("");

                if (tpActivate = true && listMarker.size() > 0) {
                    lastBallLocation.setLatitude(latLngBall.latitude);
                    lastBallLocation.setLongitude(latLngBall.longitude);
                } else {
                    lastBallLocation.setLatitude(latLngPlayer.latitude);
                    lastBallLocation.setLongitude(latLngPlayer.longitude);
                }


                Log.e("MapGoogleGolf", "latLngBall  BeforeResult" + latLngPlayer.latitude + "     " + latLngPlayer.longitude);
                latLngBall = ballPosition(lastBallLocation, Double.parseDouble(data.getStringExtra(distance)), degree);
                Log.e("MapGoogleGolf", "latLngBall  AfterResult" + latLngBall.latitude + "     " + latLngBall.longitude);


                markerBall = mMap.addMarker(new MarkerOptions()
                        .position(latLngBall)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                Log.e("MapGoogleGolf", markerBall.getId());
                listMarker.add(markerBall);

                Double message = 1000 * distanceBetween2point(latLngBall, latLngHole);
                tvDistance.setText(String.format("%.2f", message) + "  m");
                if (distanceBetween2point(latLngBall, latLngHole) <= 0.022) {
                    Log.e("MapGoogleGolf", "VIIIIIIIIIIIIIIIIIIIICCCCCTOOOOOOOOOOOOOOOIIIIIIIIRRRRRREEEEEEE");
                    Toast.makeText(this, "Bravo vous avez fini le parcours", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            btnRetry.setVisibility(View.VISIBLE);
            btnShoot.setVisibility(View.INVISIBLE);
            tpActivate = false;
        }
        if (requestCode == 1) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "L'application ne peut pas fonctionner sans gps", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        if (requestCode == 3) {
            //Toast.makeText(this, "choix" + data.getBooleanExtra(position, true), Toast.LENGTH_SHORT).show();
            if (resultCode == RESULT_OK) {
                bRandomPosition = data.getBooleanExtra(position, true);
            } else {
                bRandomPosition = true;
            }

        }

    }

    //Fonction calculant la distance etre deux points
    // Formule haversine
    // Site test (true)
    // http://www.sunearthtools.com/fr/tools/distance.php
    public double distanceBetween2point(LatLng origine, LatLng destination) {
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

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
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

    /**
     * Fonction permettant d'afficher la position de la balle  sur la carte
     * A partir des données :
     * - position du tireur
     * - distance de parcours
     * MODIFICATION à ajouter
     * - Choisir la distance (par défaut 45°)
     * - Utiliser la distance récupéré (actuellement la distance est en dur)
     * SOURCE
     * - Formule
     * # http://www.movable-type.co.uk/scripts/latlong.html
     * - Site permettant de vérifier les test
     * # http://www.geomidpoint.com/destination/
     **/
    public LatLng ballPosition(Location location, Double distance, float direction) {
        Log.e("MapGoogleGolf", "DIRECTION FONCTION   : " + direction);
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

    //Fonction permettant de mettre a jour la direction de la googlemap par rapport a la direction du téléphone
    private void updateCameraBearing(GoogleMap googleMap, float bearing, LatLng position, float zoom) {

        if (googleMap == null) return;
        CameraPosition camPos = CameraPosition
                .builder(
                        googleMap.getCameraPosition() // current Camera
                )
                .zoom(zoom)
                .target(position)
                //.tilt(60)
                .bearing(bearing)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }

    public void onBackPressed() {
        //
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

        }
    }




}
