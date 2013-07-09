package menthallab.wifimeasure;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ClassifyActivity extends Activity {
	
	private TextView resultRoomName;
	private WifiManager wifi;
	private	NeuralNetwork neuralNetwork;
	boolean learningFinished = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_classify);
		
		resultRoomName = (TextView)findViewById(R.id.text_classificationReuslt);
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		
		File sdDir = android.os.Environment.getExternalStorageDirectory();
		File file = new File(sdDir, "/dataset.csv");
		String filePath = file.getAbsolutePath();
		try
		{
			Dataset dataset = DatasetManager.loadFromFile(filePath);
			neuralNetwork = new NeuralNetwork();
			neuralNetwork.asyncLearn(dataset);
			
			final ProgressDialog pd = new ProgressDialog(this);
			pd.setMessage("Learning...");
		    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			
			new CountDownTimer(200000, 1000) {
			     public void onTick(long millisUntilFinished)
			     {
			    	 if (neuralNetwork.isCompleted())
			    	 {
			    		 this.cancel();
			    		 pd.cancel();
			    		 learningFinished = true;
			    		 registerReceiver(rssiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
			    		 wifi.startScan();
			    	 }
			     }

			     public void onFinish()
			     {
			    	 if (!learningFinished)
			    	 {
				    	 AlertDialog alertDialog;
				    	 alertDialog = new AlertDialog.Builder(ClassifyActivity.this).create();
				    	 alertDialog.setMessage("Learning time is expired. Try with another data.");
				    	 alertDialog.show();
			    	 }
			     }
			}.start();
			
		    pd.show();
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
        if (learningFinished)
        {
	        registerReceiver(rssiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	        wifi.startScan();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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
	    			int signalLevel = calculateSignalLevel(rssi, 101);
	    			instance.add(bssid, signalLevel / 100.0);
	    		}
	    		String classificationLabel = neuralNetwork.classify(instance);
	    		DateFormat df = new SimpleDateFormat("HH:mm:ss");
	    		Date currentDate = new Date();
    			String network = String.format("Room: %s. ( %s )", classificationLabel, df.format(currentDate));
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
    
    
    /** Called when the user clicks the device Back button */
    @Override
    public void onBackPressed() {
    	btBackPressed();
    }
    
    private void btBackPressed()
    {
        ClassifyActivity.super.onBackPressed();
    }
    
    // overrides WifiManager.calculateSignalLevel
    public int calculateSignalLevel(int rssi, int numLevels)
    {
    	int MIN_RSSI        = -100;
        int MAX_RSSI        = -55; 
        
        if(rssi <= MIN_RSSI) {
            return 0;
        } else if(rssi >= MAX_RSSI) {
            return numLevels - 1;
        } else {
            float inputRange = (MAX_RSSI - MIN_RSSI);
            float outputRange = (numLevels - 1);
            if(inputRange != 0)
                return (int) ((float) (rssi - MIN_RSSI) * outputRange / inputRange);
        }
        return 0;
    }

}
