package practice.testapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Akshay
 */
public class ConnectActivity extends ActionBarActivity {

    // Custom integer value code for request to turn on Bluetooth，it's equal
    //requestCode in onActivityResult.
    private static final int REQUEST_CODE_BLUETOOTH_ON = 1313;

    // duration that the device is discoverable
    private static final int DISCOVER_DURATION = 300;
    // our request code (must be greater than zero)
    private static final int REQUEST_BLU = 1;

    Button host, connect;
    String conType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        host = (Button) findViewById(R.id.btnHost);
        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conType = "SERVER";
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(getApplicationContext(), "Your Device does not support Bluetooth", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent requestBluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(requestBluetoothOn, REQUEST_CODE_BLUETOOTH_ON);
            }
        });
        connect = (Button) findViewById(R.id.btnConnect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conType = "CLIENT";
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(getApplicationContext(), "Your Device does not support Bluetooth", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);
                startActivityForResult(discoveryIntent, REQUEST_BLU);
            }
        });
    }

    /**
     * Use system alert to remind user that the app will turn on Bluetooth
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == DISCOVER_DURATION && requestCode == REQUEST_BLU) {
            callActivity();
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_BLUETOOTH_ON) {
            BluetoothAdapter.getDefaultAdapter().enable();
            callActivity();
        } else {
            Toast.makeText(this, "You cancelled Bluetooth request.", Toast.LENGTH_SHORT).show();
        }
    }

    private void callActivity() {
        Intent j = new Intent(getBaseContext(), HostingActivity.class);
        j.putExtra("CONNECTION_TYPE", conType);
        startActivity(j);
    }

    public static void doRestart(Context c) {
        try {
            //check if the context is given
            if (c != null) {
                //fetch the packagemanager so we can get the default launch activity
                // (you can replace this intent with any other activity if you want
                PackageManager pm = c.getPackageManager();
                //check if we got the PackageManager
                if (pm != null) {
                    //create the intent with the default start activity for your application
                    Intent mStartActivity = pm.getLaunchIntentForPackage(
                            c.getPackageName()
                    );
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //create a pending intent so the application is restarted after System.exit(0) was called.
                        // We use an AlarmManager to call this intent in 100ms
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent
                                .getActivity(c, mPendingIntentId, mStartActivity,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        //kill the application
                        System.exit(0);
                    } else {
                        Log.e("TAG1", "Was not able to restart application, mStartActivity null");
                    }
                } else {
                    Log.e("TAG2", "Was not able to restart application, PM null");
                }
            } else {
                Log.e("TAG3", "Was not able to restart application, Context null");
            }
        } catch (Exception ex) {
            Log.e("TAG4", "Was not able to restart application");
        }
    }


}