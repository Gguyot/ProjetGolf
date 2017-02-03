package com.example.georges.projectgolf;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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


/**
 * Created by Georges on 16/01/2017.
 */

//classe permettant d'afficher une carte (par défaut elle est centré sur l'australie)
public class MapGoogleGolf extends FragmentActivity implements OnMapReadyCallback, LocationListener {


    //Location locationTemp;
    double lat = 0, lng = 0;
    String provider;
    LocationManager locationManager;
    LatLng latLngBall;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Positionnement sur un marqueur sur sydney
       /* LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marqueur sur Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/


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


        if ((((MyApplication) this.getApplication()).getlocationGPS())==true) {
            // Getting LocationManager object from System Service LOCATION_SERVICE
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location
            Location locationTemp = locationManager.getLastKnownLocation(provider);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


            Log.e("LOCALISATION", "POS GOOGLE LATITUDE : " + locationTemp.getLatitude() + "POS THOERIQUE : " + 50.166689 + " ||     POS GOOGLE LONGETITUDE : " + locationTemp.getLongitude() + "  POS THEORIQUE : " + 3.159122000000025 + "    ||||||||      " + locationTemp.getAccuracy());

            if (locationTemp != null && locationTemp.getAccuracy() >= 100) {
                LatLng loc = new LatLng(locationTemp.getLatitude(), locationTemp.getLongitude());
                CameraPosition camera = new CameraPosition.Builder()
                        .target(loc)
                        .zoom(17)
                        .build();
            }
        }

    }

    @Override
    public void onLocationChanged(Location location) {

        // Getting Current Location
        if (location.getAccuracy() < 100) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition camera = new CameraPosition.Builder()
                    .target(loc)
                    .zoom(16)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
            latLngBall = ballPosition(location);
        }
        Log.e("PositionChange", location.getLatitude() + "     " + location.getLongitude() + "       " + location.getAccuracy());
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
    public LatLng ballPosition(Location Location) {
        double R = 6378.1;  // Rayon de la terre
        double brng = Math.toRadians(45); // Direction
        double d = 120 * 0.001; // Distance en m

        double lat = Math.toRadians(50.16848049999); // Position actuelle latitude radian
        double lon = Math.toRadians(3.159599299999); // Position actuelle Longétitude radian

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
        Log.e("VALEUR", "Latitude   : " + finalLat + "               longitude    :  " + finalLon);
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


    private Menu m = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuoption, menu);
        m = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tpBall:
                CameraPosition camera = new CameraPosition.Builder()
                        .target(latLngBall)
                        .zoom(16)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return true;
                }
                locationManager.removeUpdates(this);
                locationManager = null;

                ((MyApplication) this.getApplication()).setlocationGPS(false);

               /* Intent objIndent = new Intent(MapGoogleGolf.this,graphMousePos.class);
                startActivityForResult(objIndent);*/
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // On vérifie tout d'abord à quel intent on fait référence ici à l'aide de notre identifiant
       /* if (requestCode == CHOOSE_BUTTON_REQUEST) {
            // On vérifie aussi que l'opération s'est bien déroulée
            if (resultCode == RESULT_OK) {
                // On affiche le bouton qui a été choisi
                Toast.makeText(this, "Vous avez choisi le bouton " + data.getStringExtra(BUTTONS), Toast.LENGTH_SHORT).show();
            }
        }*/
    }





}
