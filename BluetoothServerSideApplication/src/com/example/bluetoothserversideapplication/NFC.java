package com.example.bluetoothserversideapplication;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.weexcel.databasehandler.DatabaseHandler;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class NFC extends Activity {
    private static final String TAG = "Ankit::::";
    private boolean mResumed = false;
    private boolean mWriteMode = false;
    NfcAdapter mNfcAdapter;
    TextView nfc_client_info, nfc_server_info;

    PendingIntent mNfcPendingIntent;
    IntentFilter[] mNdefExchangeFilters;
    public int incri=0;
    DatabaseHandler db;  //Object of DatabaseHandler class
    public String system_date_and_time; //store the current time and date
    public Calendar c4; // Calendar object 
	public SimpleDateFormat sdf4;
	NFCClientModel nfc; // object of client model
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        db = new DatabaseHandler(getApplicationContext());   //Creation and initialization of the Object
        setContentView(R.layout.nfc);
        // mNote = ((EditText) findViewById(R.id.note));
        // mNote.addTextChangedListener(mTextWatcher);
        nfc=new NFCClientModel();
        nfc_client_info = (TextView) findViewById(R.id.nfc_client_info);
        String device_name = Build.MODEL;
        nfc_client_info.setText("Client's Device: "+device_name);
        nfc_client_info.addTextChangedListener(mTextWatcher);
        nfc_client_info.setVisibility(View.INVISIBLE);

        nfc_server_info = (TextView) findViewById(R.id.nfc_server_info);
        String server_device = Build.MODEL;
        nfc_server_info.setText("Server's Device: "+server_device);



        // Handle all of our received NFC intents in this activity.
        mNfcPendingIntent = PendingIntent.getActivity(this, 0,new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Intent filters for reading or exchanging over p2p.
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try
        {
            ndefDetected.addDataType("text/plain");
        }
        catch (MalformedMimeTypeException e)
        {
            e.printStackTrace();
        }
        mNdefExchangeFilters = new IntentFilter[] { ndefDetected };

    }

    @Override
    protected void onResume() {
        super.onResume();
        mResumed = true;
        // Data REceived..
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            NdefMessage[] messages = getNdefMessages(getIntent());
            byte[] payload = messages[0].getRecords()[0].getPayload();
            setNoteBody(new String(payload));
            setIntent(new Intent()); // Consume this intent.
        }

        if (mNfcAdapter == null)
        {
            Toast.makeText(getApplicationContext(), "No NFC availability in this device", Toast.LENGTH_SHORT).show();
        }
        else if (mNfcAdapter != null)
        {
            if (mNfcAdapter.isEnabled() == false)
            {
                Toast.makeText(getApplicationContext(), "Please Switch on NFC", Toast.LENGTH_SHORT).show();
            } else
            {
                enableNdefExchangeMode();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mResumed = false;
        mNfcAdapter.disableForegroundNdefPush(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // NDEF exchange mode
        if (!mWriteMode && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            NdefMessage[] msgs = getNdefMessages(intent);
            promptForContent(msgs[0]);
        }
    }
//Monitors the Text Changes...
    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        }

        @Override
        public void afterTextChanged(Editable arg0) {
            if (mResumed) {
            	
                mNfcAdapter.enableForegroundNdefPush(NFC.this, getNoteAsNdef());
            }
            
        }
    };


    private void promptForContent(final NdefMessage msg) {
        String body = new String(msg.getRecords()[0].getPayload());
        setNoteBody(body);

    }
//set the data recived...
    private void setNoteBody(String body) {

        String device = nfc_client_info.getText().toString();

        nfc_client_info.setText(body);
        nfc_client_info.setVisibility(View.VISIBLE);
        
        String checking=nfc_client_info.getText().toString();
//stores the received data in database if the check is not null....
        if(checking!=null){
    		Log.e("HArsh"," you are in if block");
    	incri++;
        c4 = Calendar.getInstance();
		sdf4 = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss ");
		system_date_and_time ="Connected Time:"+" "+ sdf4.format(c4.getTime());
		nfc.setKEY_ID(incri+"");  
		nfc.setCLIENT_NAME(nfc_client_info.getText().toString());
		nfc.setTIME(system_date_and_time);
		db.addnfcClient(nfc);
    	}
		
    }

    private NdefMessage getNoteAsNdef() {
        byte[] textBytes = nfc_client_info.getText().toString().getBytes();
        NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/plain".getBytes(),
                new byte[] {}, textBytes);
        
        return new NdefMessage(new NdefRecord[] {
                textRecord
        });
    }

    NdefMessage[] getNdefMessages(Intent intent) {
        // Parse the intent
        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
        {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null)
            {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++)
                {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
                Toast.makeText(this, "You are Connected to CLient....", Toast.LENGTH_LONG).show();
                
            }
            else
            {
                // Unknown tag type
                byte[] empty = new byte[] {};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {
                        record
                });
                msgs = new NdefMessage[] {
                        msg
                };
            }
        } else {
            Log.d(TAG, "Unknown intent.");
            finish();
        }
        return msgs;
    }

    private void enableNdefExchangeMode() {
        mNfcAdapter.enableForegroundNdefPush(NFC.this, getNoteAsNdef());
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mNdefExchangeFilters, null);
    }

    private void disableNdefExchangeMode() {
        mNfcAdapter.disableForegroundNdefPush(this);
        mNfcAdapter.disableForegroundDispatch(this);
    }


    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}