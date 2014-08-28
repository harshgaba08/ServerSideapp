package com.example.bluetoothserversideapplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.example.bluetoothserversideapplication.DeviceListFragment.DeviceActionListener;
import com.weexcel.databasehandler.DatabaseHandler;

public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener {

    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    ProgressDialog progressDialog = null;
    public static Calendar c4;
	public static SimpleDateFormat sdf4;
	public static String system_date_and_time;
	public static WifiClientModel cc;
	static int key;
	public static  DatabaseHandler db;
	static String  clientname="";
	static	String clientaddress="";
	static	String clientmessage="";
static boolean	issendrunning;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

   
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	 issendrunning=true;
        mContentView = inflater.inflate(R.layout.device_detail, null);
       
        cc=new WifiClientModel();
        db=new DatabaseHandler(getActivity()); 
       
//        
// makes a disconnect request
        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((DeviceActionListener) getActivity()).disconnect();
                    }
                });

//        

        return mContentView;
    }

   
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

//        ignore for now
    }
@Override
public void onResume() {
if (issendrunning==false) {
	new SendImage(getActivity(), mContentView.findViewById(R.id.device_address))
    .execute();
}
	// TODO Auto-generated method stub
	super.onResume();
}
    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;
        this.getView().setVisibility(View.VISIBLE);

        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(getResources().getString(R.string.group_owner_text)
                + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
                        : getResources().getString(R.string.no)));

        // InetAddress from WifiP2pInfo struct.
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());

        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {
            new FileServerAsyncTask(getActivity(), mContentView.findViewById(R.id.device_address))
                    .execute();
            new SendImage(getActivity(), mContentView.findViewById(R.id.device_address))
            .execute();
        } else if (info.groupFormed) {
        	//ignore for now
        }

        
    }

    /**
     * Updates the UI with device data
     * 
     * @param device the device to be displayed
     */
    public void showDetails(WifiP2pDevice device) {
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(device.toString());

    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    
    public void resetViews() {
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view.setText(R.string.empty);
        this.getView().setVisibility(View.GONE);
    }

    /**
     * A simple server socket that accepts connection and writes some data on
     * the stream.
     */
    public static class FileServerAsyncTask extends AsyncTask<Void, Void, String> {

        private Context context;
        private static TextView statusText;

        /**
         * @param context
         * @param statusText
         */
        public FileServerAsyncTask(Context context, View statusText) {
            this.context = context;
            this.statusText = (TextView) statusText;
            
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                ServerSocket serverSocket = new ServerSocket(8988);
                Log.d(MainActivity.TAG, "Server: Socket opened");
                Socket client = serverSocket.accept();
                Log.d(MainActivity.TAG, "Server: connection done");
                final File f = new File(Environment.getExternalStorageDirectory() + "/"
                        + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                        + ".jpg");

                File dirs = new File(f.getParent());
                if (!dirs.exists())
                    dirs.mkdirs();
                f.createNewFile();

                Log.d(MainActivity.TAG, "server: copying files " + f.toString());
                InputStream inputstream = client.getInputStream();
                String a=getStringFromInputStream(inputstream);
                Log.v("trying ", a);
                serverSocket.close();
                return a;
            } catch (IOException e) {
                Log.e(MainActivity.TAG, e.getMessage());
                return null;
            }
        }
        // receive the stream data
        private static String getStringFromInputStream(InputStream is) {
        	 
    		BufferedReader br = null;
    		StringBuilder sb = new StringBuilder();
     
    		String line;
    		try {
     
    			br = new BufferedReader(new InputStreamReader(is));
    			while ((line = br.readLine()) != null) {
    				sb.append(line);
    			}
     
    		} catch (IOException e) {
    			e.printStackTrace();
    		} finally {
    			if (br != null) {
    				try {
    					br.close();
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    			}
    		}
     
    		return sb.toString();
     
    	}
     

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
            	c4 = Calendar.getInstance(); // calender object
        		sdf4 = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss ");
        		system_date_and_time ="Connected Time:"+" "+ sdf4.format(c4.getTime()); // get current date and time
            	String[] aa=result.split("/");
            	clientname=aa[0];
            	clientaddress=aa[1];
            	clientmessage=aa[2];
                statusText.setText(clientname+"\n"+clientaddress);
                key++;
                cc.setKEY_ID(key+""); // set the key id
                cc.setDEVICE(clientname); //set the client name
                cc.setIP_ADDRESS(clientaddress);//set the client address
                cc.setTIME(system_date_and_time);//set the time
                db.addwifiClient(cc);// stores the data in the database
            ////////////////////////////////////////////////
                
                
//    			final Dialog dialog = new Dialog(context);
//    			dialog.setContentView(R.layout.customdialog);
//    			dialog.setTitle("Message From Client");
//     
//    			// set the custom dialog components - text, image and button
//    			TextView text = (TextView) dialog.findViewById(R.id.text);
//    			text.setText(clientmessage);
//    			
//     
//    			Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
//    			// if button is clicked, close the custom dialog
//    			dialogButton.setOnClickListener(new OnClickListener() {
//    				@Override
//    				public void onClick(View v) {
//    					dialog.dismiss();
//    				}
//    			});
//     
//    			dialog.show();
//                
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

             // set title
             alertDialogBuilder.setTitle("Message From Client");

             // set dialog message
             alertDialogBuilder
             	.setMessage(clientmessage)
             	.setCancelable(false)
             	
             	.setNegativeButton("No",new DialogInterface.OnClickListener() {
             		public void onClick(DialogInterface dialog,int id) {
             			// if this button is clicked, just close
             			// the dialog box and do nothing
             			dialog.cancel();
             		}
             });

             // create alert dialog
             AlertDialog alertDialog = alertDialogBuilder.create();

             // show it
             alertDialog.show();
                //////////////////////////////////////////////////
            }

        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            statusText.setText("Opening a server socket");
        }

    }

    
    public static class SendImage extends AsyncTask<Void, Void, String>{
    	private Context context;
        private TextView statusText;
        private boolean running=false;
        public boolean isRunning() {
			return running;
		}
		/**
         * @param context
         * @param statusText
         */
        public SendImage(Context context, View statusText) {
            this.context = context;
            this.statusText = (TextView) statusText;
        }
		@Override
		protected String doInBackground(Void... params) {
			try {
				
                ServerSocket serverSocket1 = new ServerSocket(8988);
             //   Log.d(WiFiDirectActivity.TAG, "Server: Socket opened");
                
                	
                
                Socket client = serverSocket1.accept();
               // Log.d(WiFiDirectActivity.TAG, "Server: connection done");
                final File f = new File(Environment.getExternalStorageDirectory() + "/"
                        + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                        + ".jpg");

                File dirs = new File(f.getParent());
                if (!dirs.exists())
                    dirs.mkdirs();
                f.createNewFile();

              //  Log.d(WiFiDirectActivity.TAG, "server: copying files " + f.toString());
                InputStream inputstream = client.getInputStream();
               
                fileWrite(inputstream, new FileOutputStream(f));
                issendrunning=true;
                serverSocket1.close();
                Log.e("harsh.....",f.getAbsolutePath());
                
                	return f.getAbsolutePath();	
               
            } catch (IOException e) {
              //  Log.e(WiFiDirectActivity.TAG, e.getMessage());
                return null;
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			running=false;
		}
        @Override
        protected void onPostExecute(String result) {
            running=false;
        	if (result != null) {
               // statusText.setText("File copied - " + result);
            	
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + result), "image/*");
                context.startActivity(intent);
                issendrunning=false;
            }

        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
           // statusText.setText("Opening a server socket");
        	running=true;
        }
    }
   
    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        long startTime=System.currentTimeMillis();
        
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
            long endTime=System.currentTimeMillis()-startTime;
            Log.v("","Time taken to transfer all bytes is : "+endTime);
            
        } catch (IOException e) {
            Log.d(MainActivity.TAG, e.toString());
            return false;
        }
        return true;
    }

    public static boolean fileWrite(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        long startTime=System.currentTimeMillis();
        
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
            long endTime=System.currentTimeMillis()-startTime;
            Log.v("","Time taken to transfer all bytes is harsh.... : "+endTime);
            
        } catch (IOException e) {
            Log.d(MainActivity.TAG, e.toString());
            return false;
        }
        return true;
    }

}

