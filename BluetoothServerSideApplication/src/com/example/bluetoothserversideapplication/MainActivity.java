package com.example.bluetoothserversideapplication;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


import com.example.bluetoothserversideapplication.DeviceListFragment.DeviceActionListener;

import com.weexcel.databasehandler.DatabaseHandler;






import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity implements ChannelListener, DeviceActionListener {
	
	///////////////////////////////////////////////////////////////////
	
	public int incri=0;
	String part1 = "";
    String part2="";
	TextView msg, txt_recent_bt_client; // TextViews objects are declared, these can be accessed throughout the activity
    String message = "";        //String type variable to hold the full message shown by Server initialised as empty
    ServerSocket serverSocket;  //Object of ServerSocket
    String gotmsg = "";     //String to hold the message received from the Client initialised as empty
    Socket sock;            //Socket object
    String mydate ="";      //String to hold the date initialised as empty
    static final int SocketServerPORT = 5555;   //The Port number has been initialised
    DatabaseHandler db;  //Object of DatabaseHandler class
    WifiClientModel cc;  //Object of Client class
	BluetoothClientModel bt;
	Button btn_wificlient, btn_btclient,nfc_connect,btn_nfcclient;
	 OutputStream outputStream;
	 PrintStream printStream;
	 Socket socket;
	 NfcAdapter mNfcAdapter;
	//////////////////////////////////////////////////////////////////
	//Flags and Status...
	private static final int DISCOVERABLE_REQUEST_CODE = 0x1;
	private boolean CONTINUE_READ_WRITE = true;
	public boolean timerstatus=true;
	public boolean is_dialogvisible=false;
	public boolean nextstatus=true;
	protected static final int SUCCESS_CONNECT = 0;
	protected static final int MESSAGE_READ = 1;
	
	//Strings and unique UUID(all Clients used same UUID)
	public String system_date_and_time;
	public String tag = "debugging";
	public UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private ArrayAdapter<String> listAdapter;
	public ListView list_connected_devices;
	public Timer timer;
	public AcceptThread acceptThread;
	public ConnectedThread connectedThread;
	public Calendar c4;
	public SimpleDateFormat sdf4;
	public BluetoothAdapter mBluetoothAdapter;
	private BluetoothSocket mmSocket=null;
	
/////////////////////////////////////new code///////////////////////////////////////////////////////
	
	public static final String TAG = "wifidirectdemo";
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel; //channel object
    private BroadcastReceiver receiver = null;
    
    ///////////////////////////////////////////////////////new code//////////////////////////////////////////
    
	//HAndler for handling Responses...
	Handler mHandler=new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			
			Log.i(tag, "in handler");
			super.handleMessage(msg);
			switch (msg.what) {
			
			case MESSAGE_READ:
				get_time_date(); //get the current time and date..
				byte[] readBuf = (byte[])msg.obj;
				String retrivestream= new String(readBuf);
				String[] aa=retrivestream.split("/");
				SpannableString spannablecontent=new SpannableString("Client's Name:");
				spannablecontent.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 
				                         0,spannablecontent.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				String status=aa[0];
				String devicename="Client's Name:"+" "+ aa[1];
				String deviceaddress="Client's MAC Address:"+" "+aa[2];
				incri++;
				bt.setKEY_ID(incri+"");  
				bt.setCLIENT_NAME(aa[1]);
				bt.setCLIENT_MAC_ADDRESS(aa[2]);
				bt.setTIME(system_date_and_time);
				DatabaseHandler db=new DatabaseHandler(getApplicationContext()); 
				db.addbtClient(bt);
					android.util.Log.e("TrackingFlow","value stored in bluetooth database");
				Toast.makeText(MainActivity.this, status+" "+devicename+" "+deviceaddress, Toast.LENGTH_LONG).show();
				txt_recent_bt_client.setText("");
				txt_recent_bt_client.setText("\n"+devicename+"\n"+deviceaddress+"\n"+system_date_and_time+"\n");
				
				if(CONTINUE_READ_WRITE==false){
					try{
						if(mmSocket != null){
							try{
								connectedThread.cancel();
								android.util.Log.e("TrackingFlow","it is in try block");
								
							}catch(Exception e){
								android.util.Log.e("TrackingFlow", "you are in exception");
							}
							CONTINUE_READ_WRITE = true;
							acceptThread.interrupt();
							acceptThread=new AcceptThread();
							acceptThread.start();
						}
					}
					catch (Exception e) {}
				}
				break;

			default:
				break;
			}
		};
	};
	
	/////////////////////////// new code/////////////////////
	/**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }
	//////////////////////////// new code///////////////////
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//intent to make bluetooth discoverable
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,3600);
		 mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		startActivityForResult(discoverableIntent, DISCOVERABLE_REQUEST_CODE);
		is_dialogvisible=true;
		timer_bluetooth_dicoverable();
		get_time_date();
		mBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
		bt= new BluetoothClientModel();
		btn_wificlient=(Button) findViewById(R.id.btn_wificlient);  // CLick to view the Wifi Clients Lists
		btn_btclient=(Button) findViewById(R.id.btn_btclient); 				// CLick to view the Bluetooth Clients Lists
		btn_nfcclient=(Button) findViewById(R.id.btn_nfcclient);
		nfc_connect=(Button) findViewById(R.id.nfc_connect);
		btn_nfcclient.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i111=new Intent(getApplicationContext(),NFCClientsList.class );
				startActivity(i111);
				
			}
		});
		nfc_connect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i1=new Intent(getApplicationContext(),NFC.class );
				startActivity(i1);
			}
		});
		btn_btclient.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// intent to view the Bluetooth Clients Lists
				Intent iii= new Intent(getApplicationContext(),BluetoothClientsList.class);
				startActivity(iii);
			}
		});
		btn_wificlient.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// intent to view the Wifi Clients Lists
				Intent ii= new Intent(getApplicationContext(),WifiClientsList.class);
				startActivity(ii);
			}
		});
		
		///////////////////////////////////////////////////////////////////////////////////////////
		
		 
		db = new DatabaseHandler(getApplicationContext());   //Creation and initialization of the Object
//        
        txt_recent_bt_client = (TextView) findViewById(R.id.txt_recent_bt_client); //Object of TextView is being initialized
        android.util.Log.e("TrackingFlow", "before getIp Address");
        android.util.Log.e("TrackingFlow", "after getIpr address");
        android.util.Log.e("TrackingFlow", "socketserverthread created");//Creating Object of Thread
        android.util.Log.e("TrackingFlow", "socketserverthread starts");
        /////////////////////////////////////////////////////////////////////////////////////////
        enablewifi(); // Method to enable the Wifi
        start();
        
        ///////////////////////////new code////////////////////////////////
        
        /** intent filters for wifi p2p states monitoring **/
        
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        harsh(); /** makes the device discoverable for peers. */
        ////////////////////////////////// new code/////////////////////////////
		receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

        
	}
	
	/** makes the device discoverable for peers. */
    public void harsh(){
    	manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
    		//
    		                    @Override
    		                    public void onSuccess() {
    		                        Toast.makeText(MainActivity.this, "Discovery Initiated",
    		                                Toast.LENGTH_SHORT).show();
    		                    }
    		
    		                    @Override
    		                    public void onFailure(int reasonCode) {
    		                        Toast.makeText(MainActivity.this, "Discovery Failed : " + reasonCode,
    		                                Toast.LENGTH_SHORT).show();
    		                    }
    		                });
    }
	
	
	// Method to enable the Wifi
	public void enablewifi() {
		WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		if(wifi.getConnectionInfo().getSSID()!= null){
			Toast.makeText(getApplicationContext(), "WIFI is ON..",Toast.LENGTH_SHORT).show();
		}else{
			wifi.setWifiEnabled(true);
			
		}
		}
	
	//makes the BluetTooth Dicoverable when Discovering times out...
	public void timer_bluetooth_dicoverable() {
		
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				
				if(mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
					timerstatus=true;
				}
				if(timerstatus=true){
				if(!is_dialogvisible){
				Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,3600);
				startActivityForResult(discoverableIntent, DISCOVERABLE_REQUEST_CODE);
		    	nextstatus=false;
		    	is_dialogvisible=true;
			}
			}
			}
		}, 3600000, 3600000);
		
	}
	
	
	
//Get the Current Time and Date....	
	public void get_time_date(){
		c4 = Calendar.getInstance();
		sdf4 = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss ");
		system_date_and_time ="Connected Time:"+" "+ sdf4.format(c4.getTime());
	}
	
	//Clears all Previous Threads...
	public synchronized void start() {
		
		if(connectedThread !=null){
			connectedThread.cancel();
			connectedThread=null;
		}
		
		if (acceptThread !=null) {
			acceptThread.cancel();
			acceptThread=null;
		}
		
		if (acceptThread==null) {
			acceptThread=new AcceptThread();
			
		}
	}
	
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	
	///////////////////////////////////////////////new code////////////////////////////////////
	
	// resets all the data
	public void resetData() {
		harsh();
        DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);
        DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        if (fragmentDetails != null) {
            fragmentDetails.resetViews();
        }
    }
	
	
	/////////////////////////////////////////////////new code//////////////////////////////////
	
	@Override
	protected void onDestroy() {
		//disconnect the connection
		manager.removeGroup(channel, new ActionListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFailure(int reason) {
				// TODO Auto-generated method stub
			       Log.d(TAG, "Disconnect failed. Reason :" + reason);
			}
		});
		// unregister the receiver
		unregisterReceiver(receiver);
		if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
		
		
		if(mmSocket != null){
			try{
				connectedThread.cancel();
				android.util.Log.e("TrackingFlow","it is in try block");
			}catch(Exception e){}
			CONTINUE_READ_WRITE = false;
		}
		
		timer.cancel();
		timer.purge();
		super.onDestroy();
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		android.util.Log.e("TrackingFlow", "Creating thread to start listening...");
	
		if(nextstatus==true){
		android.util.Log.e("TrackingFlow", "AcceptThread created.....");
     	acceptThread.start();
		android.util.Log.e("TrackingFlow", "calling of acceptthread....");
	}
		is_dialogvisible=false;
    	timerstatus=false;
	}

	
	////////////////////////////////////////////////////new code//////////////////////////////////////////////
	
	 @Override
	    public void showDetails(WifiP2pDevice device) {
	        DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
	                .findFragmentById(R.id.frag_detail);
	        fragment.showDetails(device);

	    }

	    @Override
	    public void connect(WifiP2pConfig config) {
	        manager.connect(channel, config, new ActionListener() {

	            @Override
	            public void onSuccess() {
	                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
	            }

	            @Override
	            public void onFailure(int reason) {
	                Toast.makeText(MainActivity.this, "Connect failed. Retry.",
	                        Toast.LENGTH_SHORT).show();
	            }
	        });
	    }

	    @Override
	    public void disconnect() {
	        final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
	                .findFragmentById(R.id.frag_detail);
	        fragment.resetViews();
	        manager.removeGroup(channel, new ActionListener() {

	            @Override
	            public void onFailure(int reasonCode) {
	                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
	            }

	            @Override
	            public void onSuccess() {
	                fragment.getView().setVisibility(View.GONE);
	                harsh();
	            }

	        });
	    }

	    @Override
	    public void onChannelDisconnected() {
	        // we will try once more
	        if (manager != null && !retryChannel) {
	            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
	            resetData();
	            retryChannel = true;
	            manager.initialize(this, getMainLooper(), this);
	        } else {
	            Toast.makeText(this,
	                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
	                    Toast.LENGTH_LONG).show();
	        }
	    }

	    @Override
	    public void cancelDisconnect() {

//ignore for now..
	    }

	
	//////////////////////////////////////////////////////new code////////////////////////////////////////////
	
	
	
	// Threads Opens the Server Socket for LIstening Clients Requests....
	private class AcceptThread extends Thread {
		private final BluetoothServerSocket mmServerSocket;
	 
	    public AcceptThread() {
	        // Use a temporary object that is later assigned to mmServerSocket,
	        // because mmServerSocket is final
	        BluetoothServerSocket tmp = null;
	        try {
	            // MY_UUID is the app's UUID string, also used by the client code
	            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("BLTServer", uuid);
	            android.util.Log.e("TrackingFlow", "opren server socket called");
	        } catch (IOException e) { }
	        mmServerSocket = tmp;
	    }
	 
	    
	    public void run() {
	        BluetoothSocket socket = null;
	        // Keep listening until exception occurs or a socket is returned
	        while (true) {
	            try {
	            	android.util.Log.e("TrackingFlow", "before accept mmethod called.....");
	                socket = mmServerSocket.accept();
	                android.util.Log.e("TrackingFlow", "accept method called");
	            } catch (IOException e) {
	                break;
	            }
	            // If a connection was accepted
	            if (socket != null) {
	                // Do work to manage the connection (in a separate thread)
	            	android.util.Log.e("TrackingFlow", "ConnectedThread... before calling...");
//	            	if(connectedThread==null){
	            	connectedThread = new ConnectedThread(socket);
	            	connectedThread.start();
	            	android.util.Log.e("TrackingFlow", "ConnectedThread... after calling.....");
//	            	}
	            	try {
						mmServerSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            	break;
	            }
	        }
	    }
	 
	    /** Will cancel the listening socket, and cause the thread to finish */
	    public void cancel() {
	        try {
	            mmServerSocket.close();
	        } catch (IOException e) { }
	    }
	}
	
	
	
	// Connected Thread which reads and write the Data from  
	private class ConnectedThread extends Thread {
	    //private final BluetoothSocket mmSocket;
	    private final InputStream mmInStream;
	    private final OutputStream mmOutStream;
	 
	    public ConnectedThread(BluetoothSocket socket) {
	        mmSocket = socket;
	        InputStream tmpIn = null;
	        OutputStream tmpOut = null;
	 
	        // Get the input and output streams, using temp objects because
	        // member streams are final
	        try {
	            tmpIn = socket.getInputStream();
	            tmpOut = socket.getOutputStream();
	        } catch (IOException e) { }
	 
	        mmInStream = tmpIn;
	        mmOutStream = tmpOut;
	    }
	 
	    public void run() {
	        byte[] buffer = new byte[1024];  // buffer store for the stream
	        int bytesRead = -1; // bytes returned from read()
	        int bufferSize = 1024;
	        // Keep listening to the InputStream until an exception occurs
	        while (CONTINUE_READ_WRITE) {
	            try {
	            	final StringBuilder sb = new StringBuilder();
	                // Read from the InputStream
	                bytesRead = mmInStream.read(buffer);
	                
	                if (bytesRead != -1) {
						String result = "";
						while ((bytesRead == bufferSize) && (buffer[bufferSize-1] != 0)){
							result = result + new String(buffer, 0, bytesRead - 1);
							bytesRead = mmInStream.read(buffer);
							}
							result = result + new String(buffer, 0, bytesRead - 1);
							sb.append(result);
						}
						android.util.Log.e("TrackingFlow", "Read: " + sb.toString());
	                
	                // Send the obtained bytes to the UI activity
						CONTINUE_READ_WRITE=false;
	                mHandler.obtainMessage(MESSAGE_READ, bytesRead, -1, buffer)
	                        .sendToTarget();
	            } catch (IOException e) {
	                break;
	            }
	        }
	    }
	 
	    /* Call this from the main activity to send data to the remote device */
	    public void write(byte[] bytes) {
	        try {
	            mmOutStream.write(bytes);
	        } catch (IOException e) { }
	    }
	 
	    /* Call this from the main activity to shutdown the connection */
	    public void cancel() {
	        try {
	        	mmInStream.close();
	        	mmOutStream.close();
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
//	
	
}
