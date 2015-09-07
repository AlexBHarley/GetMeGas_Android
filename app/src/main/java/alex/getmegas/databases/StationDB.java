package alex.getmegas.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.PriorityQueue;

import alex.getmegas.Utils.Utils;
import alex.getmegas.objects.PetrolObject;
import alex.getmegas.objects.PetrolStation;

/**
 * Created by alex on 3/09/15.
 */
public class StationDB extends SQLiteOpenHelper {
    private final static String DB_NAME = "STATIONDB";
    private final static int DB_VERSION = 1;
    private final static String STATIONS = "stations1";
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

    public PriorityQueue<PetrolObject> getNearPetrolStations(LatLng currentLocation){
        final double mult = 2; // mult = 1.1; is more reliable
        final int radius = 1000000;
        SQLiteDatabase db = this.getReadableDatabase();
        PetrolObject object;
        PriorityQueue<PetrolObject> priorityQueue = new PriorityQueue<PetrolObject>();

        LatLng p1 = Utils.calculateDerivedPosition(currentLocation, mult * radius, 0);
        LatLng p2 = Utils.calculateDerivedPosition(currentLocation, mult * radius, 90);
        LatLng p3 = Utils.calculateDerivedPosition(currentLocation, mult * radius, 180);
        LatLng p4 = Utils.calculateDerivedPosition(currentLocation, mult * radius, 270);

        String strWhere =  " WHERE "
                + LAT + " > " + String.valueOf(p3.latitude) + " AND "
                + LAT + " < " + String.valueOf(p1.latitude) + " AND "
                + LNG + " < " + String.valueOf(p2.longitude) + " AND "
                + LNG + " > " + String.valueOf(p4.longitude);

        Cursor c = db.rawQuery("SELECT * FROM " +
                                STATIONS +
                                strWhere, null);
        Log.d("Query", "SELECT * FROM " + STATIONS + strWhere);
        Log.d("Amount", c.getCount() + "");

        if(c.moveToFirst()) {
            Log.d("Move to first", "True");

            do {
                PetrolStation station = new PetrolStation();

                station.setName(c.getString(1));
                station.setAttributes(c.getString(2));
                station.setWebsiteURL(c.getString(3));
                station.setAddress(c.getString(4));
                station.setPhoneNumber(c.getString(5));
                station.setOpeningHours(c.getString(3));
                station.setLat(c.getString(7));
                station.setLng(c.getString(8));
                station.setFacebookURL(c.getString(9));

                double distance = Utils.getDistanceBetweenTwoPoints(currentLocation,
                        new LatLng(station.getLat(), station.getLng()));

                object = new PetrolObject(distance, station);

                priorityQueue.add(object);
            } while (c.moveToNext());
        }
        /*
        if (c.moveToFirst()) {
            while (c.isAfterLast() == false) {
                LatLng petrolStationLocation = new
                        LatLng(Float.parseFloat(c.getString(7)), Float.parseFloat(c.getString(8)));
                if (Utils.pointIsInCircle(petrolStationLocation, currentLocation, radius)) {
                    PetrolStation station = new PetrolStation();

                    station.setName(c.getString(1));
                    station.setAttributes(c.getString(2));
                    station.setWebsiteURL(c.getString(3));
                    station.setAddress(c.getString(4));
                    station.setPhoneNumber(c.getString(5));
                    station.setOpeningHours(c.getString(3));
                    station.setLat(c.getString(7));
                    station.setLng(c.getString(8));
                    station.setFacebookURL(c.getString(9));

                    double distance = Utils.getDistanceBetweenTwoPoints(currentLocation,
                            new LatLng(station.getLat(), station.getLng()));

                    object = new PetrolObject(distance, station);

                    priorityQueue.add(object);
                }
                c.moveToNext();
            }
        }
        */

        return priorityQueue;
    }

    public ArrayList get100Results(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "select * from " + STATIONS;
        Cursor c = sqLiteDatabase.rawQuery(query, null);
        ArrayList<PetrolStation> p = new ArrayList<>();
        c.moveToFirst();
        do{
            PetrolStation station = new PetrolStation();
            /*
            String s = c.getString(1) +
                    c.getString(2) + " " +
            c.getString(3)+ " " +
            c.getString(4)+ " " +
            c.getString(5)+ " " +
            c.getString(6)+ " " +
                    c.getString(7)+ " " +
            c.getString(8)+ " " +
            c.getString(9)+ " ";

            Log.d("Result", s);
            */
            station.setName(c.getString(1));
            station.setAttributes(c.getString(2));
            station.setWebsiteURL(c.getString(3));
            station.setAddress(c.getString(4));
            station.setPhoneNumber(c.getString(5));
            station.setOpeningHours(c.getString(6));
            station.setLat(c.getString(7));
            station.setLng(c.getString(8));
            station.setFacebookURL(c.getString(9));
            p.add(station);
        } while(c.moveToNext());
        return p;
    }

    public PriorityQueue<PetrolObject> getStation(LatLng currentLocation){
        SQLiteDatabase db = this.getReadableDatabase();
        PriorityQueue<PetrolObject> petrolObjects = new PriorityQueue<>();
        Cursor c = db.rawQuery("select * from " + STATIONS, null);
        Log.d("Cursor length", c.getCount() + "");
        if(c.moveToFirst()){
            do{
                double lat = Double.parseDouble(c.getString(7));
                double lng = Double.parseDouble(c.getString(8));
                LatLng dest = new LatLng(lat, lng);
                if(Utils.pointIsInCircle(new LatLng(lat, lng), currentLocation, 10000)){
                    PetrolStation station = new PetrolStation();

                    station.setName(c.getString(1));
                    station.setAttributes(c.getString(2));
                    station.setWebsiteURL(c.getString(3));
                    station.setAddress(c.getString(4));
                    station.setPhoneNumber(c.getString(5));
                    station.setOpeningHours(c.getString(3));
                    station.setLat(c.getString(7));
                    station.setLng(c.getString(8));
                    station.setFacebookURL(c.getString(9));

                    petrolObjects.add(new PetrolObject(Utils.getDistanceBetweenTwoPoints(dest, currentLocation),
                            station));
                }



            } while (c.moveToNext());
        }
        Log.d("PetrolObjectListLength", petrolObjects.size() + "");
        return petrolObjects;
    }
}
