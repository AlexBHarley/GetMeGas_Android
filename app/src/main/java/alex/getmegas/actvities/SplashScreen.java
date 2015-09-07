package alex.getmegas.actvities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.PriorityQueue;

import alex.getmegas.R;
import alex.getmegas.databases.StationDB;
import alex.getmegas.Utils.CSVOperations;
import alex.getmegas.Utils.LocationService;
import alex.getmegas.Utils.Utils;
import alex.getmegas.objects.PetrolObject;
import alex.getmegas.objects.PetrolStation;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Is used first time app is opened to load data into DB
 *
 * After that it is used to get location and find closest stations
 */
public class SplashScreen extends Activity implements LocationListener{

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    StationDB db;
    CSVOperations csvOperations;
    LocationManager locationManager;
    double lat;
    double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        db = new StationDB(this);
        InputStream inputStream = getResources().openRawResource(R.raw.petrol_station);
        csvOperations = new CSVOperations(inputStream);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100000, 100000, this);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLocationChanged(Location location) {
        locationManager.removeUpdates(this);
        lat = location.getLatitude();
        lng = location.getLongitude();
        if(Utils.doesDatabaseExist(this, "StationDB")){
            new GetLocalPetrolStations().execute();
        } else {
            new SetUpDatabase().execute();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class SetUpDatabase extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                csvOperations.dumpAllData(db);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new GetLocalPetrolStations().execute();

        }
    }

    private class GetLocalPetrolStations extends AsyncTask<Void, Void, Void>{
        ArrayList<PetrolStation> petrolStations;

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("Current location:", lat + ", " + lng);
            petrolStations = db.get100Results();
            //petrolStations =
            //        db.getStation(new LatLng(lat, lng));
                //db.getNearPetrolStations(new LatLng(lat, lng));
            Log.d("Petrol stations:", petrolStations.size() + "");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            ArrayList<PetrolStation> petrolStationArrayList = new ArrayList<>();

            for(PetrolStation p : petrolStations){
                LatLng current = new LatLng(lat, lng);
                LatLng dest = new LatLng(p.getLat(), p.getLng());
                Log.d("Dest latlng",p.getName() + " " + p.getAddress() + " " + dest.latitude + " " + dest.longitude);
                Log.d("Current latlng", current.latitude +" "+ current.longitude);
                if(Utils.pointIsInCircle(current, dest, 1000)) {
                    petrolStationArrayList.add(p);
                }
            }
            Log.d("Close stations", petrolStationArrayList.size() + "");
        }
    }


}
