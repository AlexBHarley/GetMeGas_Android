package alex.getmegas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alex on 3/09/15.
 */
public class StationDB extends SQLiteOpenHelper {
    private final static String DB_NAME = "STATIONDB";
    private final static int DB_VERSION = 1;
    private final static String STATIONS = "stations";
    private final static String ID = "ID";
    private final static String NAME = "NAME";
    private final static String DESCRIPTION = "DESCRIPTION";
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
                + DESCRIPTION + " TEXT, "
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
}
