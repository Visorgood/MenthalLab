package menthallab.wifimeasure;

import java.io.File;
import java.io.IOException;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
	
	private boolean isWorking;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_work);
		
		textView = (TextView)findViewById(R.id.text_view);
		startButton = (Button)findViewById(R.id.bt_start);
		resultRoomName = (TextView)findViewById(R.id.text_name);
		isWorking = false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.work, menu);
		return true;
	}
	
    /** Called when the user clicks the Back button */
    public void returnBack(View view) {
    	btBackPressed();
    }
    
    /** Called when the user clicks the Start button */
    public void startTraining(View view)
    {
    	if (isWorking)
    	{
    		isWorking = false;
    		startButton.setText("Start");
    	}
    	else
    	{
	    	isWorking = true;
	    	startButton.setText("Stop");
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
