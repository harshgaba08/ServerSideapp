package com.example.bluetoothserversideapplication;

import java.util.ArrayList;

import com.weexcel.databasehandler.DatabaseHandler;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BluetoothClientsList extends Activity {

	public ArrayList<String> results; 
	private SQLiteDatabase newDB;
	private String tableName = DatabaseHandler.TABLE_BLUETOOTH_CLIENTS; // contains Bluetooth table name
	public ListView list_bt_clients;
	public ArrayAdapter<String> listadadpter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetoothclientslist);
		list_bt_clients=(ListView) findViewById(R.id.list_bt_clients);
		results = new ArrayList<String>();
		openAndQueryDatabase(); // method to Query the database

		displayResultList();  // method to show Query Results in List
	}
	
	// method to show Query Results in List
	private void displayResultList() {
		// TODO Auto-generated method stub
		listadadpter =new ArrayAdapter<String>(this,R.layout.listview,results);
		list_bt_clients.setAdapter(listadadpter);
	}
	
	// method to Query the database
	private void openAndQueryDatabase() {
		// TODO Auto-generated method stub
		
		
		try {
			DatabaseHandler dbhandler= new DatabaseHandler(this.getApplicationContext());
			newDB = dbhandler.getWritableDatabase();
			//Cursor to carry the query result
			Cursor c=newDB.rawQuery("SELECT clientname, macaddress, time FROM "+tableName,null);


			if (c != null ) {
				if  (c.moveToFirst()) {
					do {
						String clientname = c.getString(c.getColumnIndex("clientname"));
						String macaddress = c.getString(c.getColumnIndex("macaddress"));
						String time = c.getString(c.getColumnIndex("time"));
						results.add("\n Client's Name: " + clientname + "\n MAC Address: " + macaddress+"\n "+time);
					}while (c.moveToNext());
				} 
			}			
		} 
		catch (SQLiteException se ) {
			Log.e(getClass().getSimpleName(), "Could not create or Open the database");
		}
		finally {
			newDB.close();
		}	
		
		
		
	}
}
