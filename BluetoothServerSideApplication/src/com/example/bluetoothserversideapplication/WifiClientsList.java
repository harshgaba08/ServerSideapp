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

public class WifiClientsList extends Activity {
	public ArrayList<String> results;  //Array LIst to view the results
	private SQLiteDatabase newDB;
	private String tableName = DatabaseHandler.TABLE_WIFI_CLIENTS; //contains Wifi clients table name
	public ListView list_wifi_clients;
	public ArrayAdapter<String> listadadpter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wificlientslist);
		list_wifi_clients=(ListView)findViewById(R.id.list_wifi_clients);
		results = new ArrayList<String>();
		openAndQueryDatabase(); // method to Query the database

		displayResultList(); // method to show Query Results in List

	}
	// method to show Query Results in List
	private void displayResultList() {
		// TODO Auto-generated method stub
		listadadpter =new ArrayAdapter<String>(this,R.layout.listview,results);
		list_wifi_clients.setAdapter(listadadpter);

	}
	// method to Query the database
	private void openAndQueryDatabase() {
		// TODO Auto-generated method stub
		try {
			DatabaseHandler dbhandler= new DatabaseHandler(this.getApplicationContext());
			newDB = dbhandler.getWritableDatabase();
			//Cursor to carry the query result
			Cursor c=newDB.rawQuery("SELECT macaddress, time, device FROM "+tableName,null);


			if (c != null ) {
				if  (c.moveToFirst()) {
					do {
						String device = c.getString(c.getColumnIndex("device"));
						String macaddress = c.getString(c.getColumnIndex("macaddress"));
						
						
						String time = c.getString(c.getColumnIndex("time"));
						results.add("\n"+device+"\n"+macaddress+"\n"+time);
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
