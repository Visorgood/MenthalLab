package menthallab.wifimeasure;

import java.io.File;
import java.io.IOException;
import java.util.*;
import menthallab.wifimeasure.R;
import android.net.wifi.*;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TrainActivity extends Activity {
	
	private TextView textView;
	private Button startButton;
	private Button backButton;
	private EditText editText;
	
	private WifiManager wifi;
	private boolean isWorking;
	
	private Dataset dataset;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train);
		
		textView = (TextView)findViewById(R.id.text_wifiResults);
		startButton = (Button)findViewById(R.id.bt_start);
		backButton = (Button)findViewById(R.id.bt_back);
		editText = (EditText)findViewById(R.id.edit_roomName);
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		isWorking = false;
		IDGenerator.reset();
		
		dataset = new Dataset();
		
		editText.setHint("Room " + IDGenerator.getNextId());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
    public void onResume() {
        super.onResume();
        if (isWorking)
        {
	        registerReceiver(rssiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	        wifi.startScan();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isWorking)
        {
        	unregisterReceiver(rssiReceiver);
        }
    }
    
    BroadcastReceiver rssiReceiver = new BroadcastReceiver() {
    	@Override
        public void onReceive(Context context, Intent intent)
    	{
    		try
    		{
				Instance instance = new Instance();
    			List<ScanResult> scanResults = wifi.getScanResults();
	    		StringBuilder messageBuilder = new StringBuilder();
	    		for (ScanResult scanResult : scanResults)
	    		{
	    			String ssid = scanResult.SSID;
	    			String networkName = scanResult.BSSID;
	    			int rssi = scanResult.level;
	    			int signalLevel = WifiLib.calculateSignalLevel(rssi, WifiLib.numberOfLevels + 1); 
	    			String network = String.format("Name: %s; Level: %d\n", ssid, signalLevel);
	    			messageBuilder.append(network);
	    			instance.add(networkName, 1.0 * signalLevel / WifiLib.numberOfLevels);
	    		}
	    		String label = editText.getText().toString();
	    		if (label.equals(""))
	    			label = editText.getHint().toString();
				dataset.addInstance(instance, label, true);
	    		textView.setText(messageBuilder.toString());
	    		wifi.startScan();
    		}
    		catch (Exception exc)
    		{
    			AlertDialog ad = new AlertDialog.Builder(context).create();
    			ad.setMessage(exc.toString());
    			StackTraceElement[] stackTrace = exc.getStackTrace();
    			ad.show();
    		}
        }
    };
    
    /** Called when the user clicks the Start button */
    public void startTraining(View view)
    {
    	if (isWorking)
    	{
    		unregisterReceiver(rssiReceiver);
    		editText.setEnabled(true);
    		backButton.setEnabled(true);
    		textView.setText("");
    		startButton.setText("Start");
    		isWorking = false;
    		editText.setHint("Room " + IDGenerator.getNextId());
    		try
    		{
    			File sdDir = android.os.Environment.getExternalStorageDirectory();
    			File file = new File(sdDir, "/dataset.csv");
    			String filePath = file.getAbsolutePath();
    			DatasetManager.saveToFile(dataset, filePath);
    		}
    		catch (IOException exc)
    		{
    			AlertDialog ad = new AlertDialog.Builder(this).create();
    			ad.setMessage(exc.toString()); 
    			ad.show();
    		}
    	}
    	else
    	{
    		isWorking = true;
    		startButton.setText("Stop");
    		editText.setEnabled(false);
    		backButton.setEnabled(false);
    		registerReceiver(rssiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    		wifi.startScan();
    	}
    }
    
    /** Called when the user clicks the Back button */
    public void returnBack(View view) {
    	btBackPressed();
    }
    
    /** Called when the user clicks the device Back button */
    @Override
    public void onBackPressed() {
    	btBackPressed();
    }
    
    private void btBackPressed()
    {
    	isWorking = false;//!!!!!! must be done other way!!!!!
        TrainActivity.super.onBackPressed();
    }
}