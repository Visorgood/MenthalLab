package menthallab.wifimeasure;

import java.io.File;
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
import android.widget.TextView;

public class ClassifyActivity extends Activity {
	
	private TextView resultRoomName;
	private WifiManager wifi;
	private	NeuralNetwork neuralNetwork;
	private boolean learningCompleted; // network has been learned successfully!

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_classify);
		
		resultRoomName = (TextView)findViewById(R.id.text_classificationReuslt);
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		learningCompleted = false;
		
		File sdDir = android.os.Environment.getExternalStorageDirectory();
		File file = new File(sdDir, "/dataset.csv");
		String filePath = file.getAbsolutePath();
		try
		{
			Dataset dataset = DatasetManager.loadFromFile(filePath);
			neuralNetwork = new NeuralNetwork();
			neuralNetwork.asyncLearn(dataset);
			final double maxNetworkError = neuralNetwork.getMaxError();
			
			final ProgressDialog pd = new ProgressDialog(this);
			pd.setMessage("Learning...");
		    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		    pd.setCanceledOnTouchOutside(false);
		    
			new CountDownTimer(300 * 1000, 1 * 1000) {
			     public void onTick(long millisUntilFinished)
			     {
			    	 if (neuralNetwork.isCompleted())
			    	 {
			    		 this.cancel();
			    		 pd.cancel();
			    		 learningCompleted = true;
			    		 registerReceiver(rssiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
			    		 wifi.startScan();
			    	 }
			    	 else
			    	 {
			    		 pd.setMessage("Learning...\nRemaining seconds: " + (millisUntilFinished / 1000) + "\nDesired error: " + maxNetworkError + "\nCurrent error: " + neuralNetwork.getCurrentError());
			    	 }
			     }

			     public void onFinish()
			     {
			    	 if (!learningCompleted)
			    	 {
			    		 neuralNetwork.stopLearning();
				    	 pd.cancel();
				    	 AlertDialog alertDialog;
				    	 alertDialog = new AlertDialog.Builder(ClassifyActivity.this).create();
				    	 alertDialog.setMessage("Learning time is expired. Try with another data.");
				    	 alertDialog.show();
				    	 //btBackPressed();
			    	 }
			     }
			}.start();
			
		    pd.show();
		}
		catch (Exception exc)
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
        if (learningCompleted)
        {
	        registerReceiver(rssiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	        wifi.startScan();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (learningCompleted)
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
	    		for (ScanResult scanResult : scanResults)
	    		{
	    			String bssid = scanResult.BSSID;
	    			int rssi = scanResult.level;
	    			int signalLevel = WifiLib.calculateSignalLevel(rssi, WifiLib.numberOfLevels + 1);
	    			instance.add(bssid, 1.0 * signalLevel / WifiLib.numberOfLevels);
	    		}
	    		String classificationLabel = neuralNetwork.classify(instance);
	    		DateFormat df = new SimpleDateFormat("HH:mm:ss");
	    		String currentTimeStr = df.format(new Date());
    			String network = String.format("Room: %s. ( %s )", classificationLabel, currentTimeStr);
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
}