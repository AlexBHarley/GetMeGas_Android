package alex.getmegas;

import android.util.Log;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by alex on 4/09/15.
 */
public class CSVOperations {
    CSVReader reader;
    private InputStream inputStream;

    public CSVOperations(InputStream inputStream) {
        this.inputStream = inputStream;
        ;
    }


    public boolean dumpAllData(StationDB db) throws IOException {
        BufferedReader b = new BufferedReader(new InputStreamReader(inputStream));
        reader = new CSVReader(b);
        String[] line;
        while((line = reader.readNext()) != null){
            PetrolStation station = new PetrolStation();

            station.setName(line[1]);
            station.setAttributes(line[2]);
            station.setWebsiteURL(line[3]);
            station.setAddress(line[4]);
            station.setPhoneNumber(line[5]);
            station.setOpeningHours(line[6]);
            station.setLat(line[8]);
            station.setLng(line[9]);
            station.setFacebookURL(line[15]);

            //("Station", station.getName() + " " + station.getOpeningHours());

            db.insertStation(station);

        }
        return true;
    }
}
