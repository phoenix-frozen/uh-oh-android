package emergencycall.intertech.com.emergencycall.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import emergencycall.intertech.com.emergencycall.CallManager;
import emergencycall.intertech.com.emergencycall.R;


public class MainActivity extends Activity implements View.OnClickListener {

    private Button mButtonCall;
    private Button mButtonSend;

    private CallManager mCallManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonCall = (Button) findViewById(R.id.button_call);
        mButtonSend = (Button) findViewById(R.id.button_send_coordinates);
        mButtonCall.setOnClickListener(this);
        mButtonSend.setOnClickListener(this);
        mCallManager = new CallManager(getApplicationContext());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_call:
                mCallManager.call();
                break;
            case R.id.button_send_coordinates:

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
