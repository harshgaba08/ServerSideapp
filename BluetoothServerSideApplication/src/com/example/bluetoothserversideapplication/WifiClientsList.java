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
			Cursor c=newDB.rawQuery("SELECT user_num, port, ip, time, message, device FROM "+tableName,null);


			if (c != null ) {
				if  (c.moveToFirst()) {
					do {
						String usernum = c.getString(c.getColumnIndex("user_num"));
						String port = c.getString(c.getColumnIndex("port"));
						String ip = c.getString(c.getColumnIndex("ip"));
						String time = c.getString(c.getColumnIndex("time"));
						String message = c.getString(c.getColumnIndex("message"));
						String device = c.getString(c.getColumnIndex("device"));
						results.add("\n User Number: " + usernum + "\n Port: " + port+"\n IP Address: "+ip+"\n Message: "+message+"\n Device: "+device+"\n Connected Time: "+time);
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
