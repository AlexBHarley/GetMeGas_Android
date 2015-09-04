package alex.getmegas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by alex on 3/09/15.
 */
public class StationDB extends SQLiteOpenHelper {
    private final static String DB_NAME = "STATIONDB";
    private final static int DB_VERSION = 1;
    private final static String STATIONS = "stations";
    private final static String ID = "ID";
    private final static String NAME = "NAME";
    private final static String ATTRIBUTES = "ATTRIBUTES";
    private final static String URL = "URL";
    private final static String ADDRESS = "ADDRESS";
    private final static String PH_NUMBER = "PH_NUMBER";
    private final static String OPENING_HOURS = "OPENING_HOURS";
    private final static String LAT = "LAT";
    private final static String LNG = "LNG";
    private final static String FB_LINK = "FB_LINK";

    public StationDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + STATIONS
                + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " TEXT NOT NULL, "
                + ATTRIBUTES + " TEXT, "
                + URL + " TEXT, "
                + ADDRESS + " TEXT, "
                + PH_NUMBER + " TEXT, "
                + OPENING_HOURS + " TEXT, "
                + LAT + " TEXT NOT NULL, "
                + LNG + " TEXT NOT NULL, "
                + FB_LINK + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertStation(PetrolStation station){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(NAME, station.getName());
        cv.put(ATTRIBUTES, station.getAttributes());
        cv.put(OPENING_HOURS, station.getOpeningHours());
        cv.put(ADDRESS, station.getAddress());
        cv.put(PH_NUMBER, station.getPhoneNumber());
        cv.put(LAT, station.getLat());
        cv.put(LNG, station.getLng());
        cv.put(FB_LINK, station.getFacebookURL());
        cv.put(URL, station.getWebsiteURL());

        db.insert(STATIONS, null, cv);
    }

    public ArrayList<PetrolStation> getNearPetrolStations(LatLng currentLocation){
        final double mult = 1; // mult = 1.1; is more reliable
        final int radius = 100;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<PetrolStation> petrolStationArray = new ArrayList<PetrolStation>();

        LatLng p1 = Utils.calculateDerivedPosition(currentLocation, mult * radius, 0);
        LatLng p2 = Utils.calculateDerivedPosition(currentLocation, mult * radius, 90);
        LatLng p3 = Utils.calculateDerivedPosition(currentLocation, mult * radius, 180);
        LatLng p4 = Utils.calculateDerivedPosition(currentLocation, mult * radius, 270);

        String strWhere =  " WHERE "
                + LAT + " > " + String.valueOf(p3.latitude) + " AND "
                + LAT + " < " + String.valueOf(p1.latitude) + " AND "
                + LNG + " < " + String.valueOf(p2.longitude) + " AND "
                + LNG + " > " + String.valueOf(p4.longitude);
        db.execSQL("SELECT * FROM " +
                    STATIONS + " " +
                        strWhere + ";"
        );

        Cursor c = db.rawQuery("SELECT * FROM " +
                                STATIONS + " " +
                                strWhere, null);

        if (c .moveToFirst()) {
            while (c.isAfterLast() == false) {
                LatLng petrolStationLocation = new
                        LatLng(Float.parseFloat(c.getString(8)), Float.parseFloat(c.getString(9)));
                if (Utils.pointIsInCircle(petrolStationLocation, currentLocation, radius)) {
                    PetrolStation station = new PetrolStation();

                    station.setName(c.getString(1));
                    station.setAttributes(c.getString(2));
                    station.setWebsiteURL(c.getString(3));
                    station.setAddress(c.getString(4));
                    station.setPhoneNumber(c.getString(5));
                    station.setOpeningHours(c.getString(6));
                    station.setLat(c.getString(8));
                    station.setLng(c.getString(9));
                    station.setFacebookURL(c.getString(15));

                    petrolStationArray.add(station);
                }
                c.moveToNext();
            }
        }

        return petrolStationArray;
    }
}
