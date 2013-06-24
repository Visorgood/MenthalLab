package menthallab.wifimeasure;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class WorkActivity extends Activity {
	
	private TextView textView;
	private Button startButton;
	private TextView resultRoomName;
	
	private WifiManager wifi;	
	private boolean isWorking;
	
	private	kNN knn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_work);
		
		textView = (TextView)findViewById(R.id.text_view);
		startButton = (Button)findViewById(R.id.bt_start);
		resultRoomName = (TextView)findViewById(R.id.text_name);
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		isWorking = false;
		
		File sdDir = android.os.Environment.getExternalStorageDirectory();
		File file = new File(sdDir, "/dataset.csv");
		String filePath = file.getAbsolutePath();
		try
		{
			Dataset dataset = DatasetManager.loadFromFile(filePath);
			knn = new kNN(dataset, 10);
		}
		catch (IOException exc)
		{
			AlertDialog ad = new AlertDialog.Builder(this).create();
			ad.setMessage(exc.toString()); 
			ad.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.work, menu);
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
        	unregisterReceiver(rssiReceiver);
    }
	
    BroadcastReceiver rssiReceiver = new BroadcastReceiver() {
    	@Override
        public void onReceive(Context context, Intent intent)
    	{
    		try
    		{
				Instance instance = new Instance();
    			List<ScanResult> scanResults = wifi.getScanResults();
	    		for (ScanResult scanResult : scanResults)
	    		{
	    			String bssid = scanResult.BSSID;
	    			int rssi = scanResult.level;
	    			int signalLevel = WifiManager.calculateSignalLevel(rssi, 1001);
	    			instance.add(bssid, signalLevel / 1000.0);
	    		}
	    		Date start = new Date();
	    		String classificationLabel = knn.classify(instance);
	    		Date end = new Date();
	    		long computingTime = (end.getTime() - start.getTime());
    			String network = String.format("Room: %s. Computing time: %d ms", classificationLabel, computingTime);
    			resultRoomName.setText(network);
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
	
    /** Called when the user clicks the Back button */
    public void returnBack(View view) {
    	btBackPressed();
    }
    
    /** Called when the user clicks the Start button */
    public void startTraining(View view)
    {
    	if (isWorking)
    	{
    		unregisterReceiver(rssiReceiver);
    		isWorking = false;
    		startButton.setText("Start");
    	}
    	else
    	{
	    	isWorking = true;
	    	startButton.setText("Stop");
    		registerReceiver(rssiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	    	wifi.startScan();
    	}
    }
    
    /** Called when the user clicks the device Back button */
    @Override
    public void onBackPressed() {
    	btBackPressed();
    }
    
    private void btBackPressed()
    {
        WorkActivity.super.onBackPressed();
    }

}
