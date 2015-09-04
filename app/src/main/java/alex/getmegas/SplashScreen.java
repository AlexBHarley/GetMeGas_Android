package alex.getmegas;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Is used first time app is opened to load data into DB
 *
 * After that it is used to get location and find closest stations
 */
public class SplashScreen extends Activity {

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    StationDB db;
    CSVOperations csvOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new StationDB(this);
        InputStream inputStream = getResources().openRawResource(R.raw.petrol_station);
        csvOperations = new CSVOperations(inputStream);

        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        progressBar.setVisibility(View.VISIBLE);

        if(Utils.doesDatabaseExist(this, "StationDB")){
            new GetLocalPetrolStations().execute();
        } else {
            new SetUpDatabase().execute();
        }

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

        @Override
        protected Void doInBackground(Void... params) {
            LocationService locationService = new LocationService(getApplicationContext());
            locationService.getCurrentLocation();

            db.getNearPetrolStations(new LatLng(locationService.getCurrentLocation().getLatitude(),
                                                locationService.getCurrentLocation().getLongitude()
                                    ));

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            //intent.putExtras(new Bundle())
            startActivity(intent);
        }
    }


}
