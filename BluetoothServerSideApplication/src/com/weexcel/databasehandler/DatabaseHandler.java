package com.weexcel.databasehandler;

import com.example.bluetoothserversideapplication.BluetoothClientModel;
import com.example.bluetoothserversideapplication.NFCClientModel;
import com.example.bluetoothserversideapplication.WifiClientModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper
{

    private static final int DATABASE_VERSION = 2;   //Version of the database is set
    private static final String DATABASE_NAME = "db_server";  //Database name has been given
    public static final String TABLE_WIFI_CLIENTS = "tb_wifi_clients_details";   //Table name has been given
    public static final String TABLE_BLUETOOTH_CLIENTS="tb_bluetooth_clients_details";
    public static final String TABLE_NFC_CLIENTS="tb_nfc_clients_details";

    /***** Column names of the wifi_clients_table *****/
    private static final String KEY_ID = "id";
   // private static final String USER_NUM = "user_num";
   // private static final String PORT_NUM = "port";
    private static final String MACI_ADDRESS = "macaddress";
    private static final String TIME = "time";
    //private static final String MESSAGE = "message";
    private static final String DEVICE = "device";
    
    /***** Column names of the wifi_clients_table *****/
    private static final String KEY_IDI = "idi";
    private static final String CLIENT_NAME = "clientname";
    private static final String MAC_ADDRESS = "macaddress";
    private static final String TIMEE = "time";
    
    /*** Column name of NFC clients table ***/
    private static final String KEY_IDII = "idi";
    private static final String CLIENT_NAMEE = "clientname";
    private static final String TIMEEE = "time";
    
    String CREATE_TABLE = "CREATE TABLE " + TABLE_WIFI_CLIENTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + DEVICE + " TEXT,"
            + MACI_ADDRESS + " TEXT,"
            + TIME + " TEXT"+ ")";
    
    
    public String CREATE_TABLE2 = "CREATE TABLE " + TABLE_BLUETOOTH_CLIENTS + "("
            + KEY_IDI + " INTEGER PRIMARY KEY,"
            + CLIENT_NAME + " TEXT,"
            + MAC_ADDRESS + " TEXT,"
            + TIMEE + " TEXT"+ ")";

    public String CREATE_TABLE3 = "CREATE TABLE " + TABLE_NFC_CLIENTS + "("
            + KEY_IDII + " INTEGER PRIMARY KEY,"
            + CLIENT_NAMEE + " TEXT,"
            + TIMEEE + " TEXT"+ ")";
    
    public DatabaseHandler(Context context)  //Constructor
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /***** Creating the Table *****/
    @Override
    public void onCreate(SQLiteDatabase db)    //onCreate method
    {
        Log.v("Create Table Called", "Cretae table called");
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE2);
        db.execSQL(CREATE_TABLE3);
    }

    /***** Upgrading database *****/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WIFI_CLIENTS);   //If the table already exists it is dropped (deleted)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLUETOOTH_CLIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NFC_CLIENTS);

        onCreate(db); // creating the table again
    }

    /***** Method to add details of a new Client *****/
    public void addwifiClient(WifiClientModel cc) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DEVICE, cc.getDEVICE());
        values.put(MACI_ADDRESS, cc.getIP_ADDRESS());
        values.put(TIME, cc.getTIME());
        db.insert(TABLE_WIFI_CLIENTS, null, values);
        db.close(); // Closing the database connection
    }
    
    /***** Method to add details of a new Client *****/
    public void addbtClient(BluetoothClientModel bt) {
        SQLiteDatabase dbb = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CLIENT_NAME, bt.getCLIENT_NAME());
        values.put(MAC_ADDRESS, bt.getCLIENT_MAC_ADDRESS());
        values.put(TIMEE, bt.getTIME());
        dbb.insert(TABLE_BLUETOOTH_CLIENTS, null, values);
        dbb.close(); // Closing the database connection
    }

    /***** Method to add details of a new Client *****/
    public void addnfcClient(NFCClientModel nfc) {
        SQLiteDatabase dbbb = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CLIENT_NAMEE, nfc.getCLIENT_NAME());
        values.put(TIMEEE, nfc.getTIME());
        dbbb.insert(TABLE_NFC_CLIENTS, null, values);
        dbbb.close(); // Closing the database connection
    }

}
