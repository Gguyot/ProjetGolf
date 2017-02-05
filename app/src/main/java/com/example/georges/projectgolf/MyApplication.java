package com.example.georges.projectgolf;

/**
 * Created by chaki on 20/11/2016.
*/

import android.app.Application;

/**
 * Created by Georges on 26/10/2016.
 */
//Classe permettant d'instancier une variable globale à tout le projet
public class MyApplication extends Application {

    private boolean locationGPS=true;

    public boolean getlocationGPS() {
        return locationGPS;
    }
    public void setlocationGPS(boolean location) {
        this.locationGPS = location;
    }

    // récupéré la valeur
    //(((MyApplication) this.getApplication()).getlocationGPS())

    //Modifier la valeur
    //((MyApplication) this.getApplication()).setlocationGPS(true false);

}
