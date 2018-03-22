/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Extended by Akshay
 */

package practice.testapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This is the main Activity that displays the current chat session.
 */
public class HostingActivity extends Activity {

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE0 = 1;
    private static final int REQUEST_CONNECT_DEVICE1 = 2;
    private static final int REQUEST_CONNECT_DEVICE2 = 3;
    private static final int REQUEST_ENABLE_BT = 4;
    private static String connectionType = "";
    private Button player2, player3, player4, start;
    static ObjectActivity obj;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hosting);
        obj = new ObjectActivity(getApplicationContext());
        // Get local Bluetooth adapter
        obj.mBluetoothAdapter = new BluetoothAdapter[3];
        obj.mBluetoothAdapter[0] = BluetoothAdapter.getDefaultAdapter();
        obj.mBluetoothAdapter[1] = BluetoothAdapter.getDefaultAdapter();
        obj.mBluetoothAdapter[2] = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (obj.mBluetoothAdapter[0] == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
//            finish();
//            return;
        }
        player2 = (Button) findViewById(R.id.btnPlayer2);
        connectionType = (String) getIntent().getExtras().get("CONNECTION_TYPE");
//        Toast.makeText(getApplicationContext(), "onCreate() " + connectionType, Toast.LENGTH_LONG).show();
        player2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serverIntent0 = new Intent(getApplicationContext(), DeviceListActivity.class);
                startActivityForResult(serverIntent0, REQUEST_CONNECT_DEVICE0);
            }
        });
        player3 = (Button) findViewById(R.id.btnPlayer3);
        player3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serverIntent1 = new Intent(getApplicationContext(), DeviceListActivity.class);
                startActivityForResult(serverIntent1, REQUEST_CONNECT_DEVICE1);
            }
        });
        player4 = (Button) findViewById(R.id.btnPlayer4);
        player4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serverIntent2 = new Intent(getApplicationContext(), DeviceListActivity.class);
                startActivityForResult(serverIntent2, REQUEST_CONNECT_DEVICE2);
            }
        });
        start = (Button) findViewById(R.id.btnStart);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), connectionType, Toast.LENGTH_LONG).show();
                if (connectionType.equals("SERVER")) {
                    Random rng = new Random(); // Ideally just create one instance globally
                    List<Integer> generated = new ArrayList<Integer>();
                    String message = "";
                    obj.players[0]=obj.mConnectedDeviceName[0];
                    obj.players[1]=obj.mConnectedDeviceName[1];
                    obj.players[2]=obj.mConnectedDeviceName[2];
                    obj.players[3]=BluetoothAdapter.getDefaultAdapter().getName();
                    message = obj.players[0]+","+obj.players[1]+","+obj.players[2]+","+obj.players[3]+",";
                    for (int i = 0, j; i < 4; i++) {
                        while (true) {
                            Integer next = rng.nextInt(4) + 1;
                            if (!generated.contains(next)) {
                                // Done for this iteration
                                generated.add(next);
                                switch (next.intValue()) {
                                   case 1:
                                        obj.cards[i] = "K";
                                        obj.score[i] = 0;
                                        message += "K,";
                                        break;
                                    case 2:
                                        obj.cards[i] = "Q";
                                        obj.score[i] = 0;
                                        message += "Q,";
                                        break;
                                    case 3:
                                        obj.cards[i] = "P";
                                        obj.score[i] = 0;
                                        message += "P,";
                                        break;
                                    case 4:
                                        obj.cards[i] = "T";
                                        obj.score[i] = 0;
                                        message += "T,";
                                        break;
                                }
                                break;
                            }
                        }
                    }
                    obj.chance="K";
                    Log.e("String", message + "0,0,0,0,K");
                    obj.setMsg(message + "0,0,0,0,K");
                    obj.sendMessage(obj.getMsg());
                    obj.game_status=ObjectActivity.GAME_START;
                    Intent j = new Intent(getBaseContext(), GameActivity.class);
                    j.putExtra("CONNECTION_TYPE", "SERVER");
                    startActivity(j);
                }
                else if (connectionType.equals("CLIENT")) {
                    Intent j = new Intent(getBaseContext(), GameActivity.class);
                    j.putExtra("CONNECTION_TYPE", "CLIENT");
                    startActivity(j);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.e("TAG", "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        for (int j = 0; j < obj.mBluetoothAdapter.length; j++) {
            if (!obj.mBluetoothAdapter[j].isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                // Otherwise, setup the chat session
            } else {
                if (obj.mChatService[j] == null) {
                    obj.setupChat(j);
                }
            }
        }
//        Toast.makeText(getApplicationContext(), "onStart() " + connectionType, Toast.LENGTH_LONG).show();
        if (connectionType.equals("CLIENT")) {
            player2.setEnabled(false);
            Thread t = new Thread(){
                public void run() {
                    while (true)
                    {
                        if (connectionType.equals("CLIENT"))
                        {
//                            Log.e("getApplicationContext()", "onCreate() " + obj.game_status);
                            if (obj.game_status == ObjectActivity.REPLY_RECEIVED) {
                                enableStart();
                                obj.game_status=ObjectActivity.GAME_START;
                                break;
                            }
                        }
                    }
                    try {
//                        Toast.makeText(getApplicationContext(), "Sleeping", Toast.LENGTH_LONG).show();
                        Thread.sleep(1000);
                    } catch (Exception e) {}
                }
            };
            t.start();
        }
    }

    private void enableStart() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                start.setEnabled(true);
                String values[]=obj.getMsg().split(",");
                Log.e("getMsg()", obj.getMsg());
                for(int i=0; i<4; i++)
                {
                    obj.players[i]=values[i];
                }
                for(int i=4; i<8; i++)
                {
                    obj.cards[i-4]=values[i];
                }
                for(int i=8; i<12; i++)
                {
                    obj.score[i-8]=Integer.parseInt(values[i]);
                }
                obj.chance=values[12];
            }
        });
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
//        if(D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        for (int i = 0; i < obj.mChatService.length; i++) {
            if (obj.mChatService[i] != null) {
                // Only if the state is STATE_NONE, do we know that we haven't started already
                if (obj.mChatService[i].getState() == GameService.STATE_NONE) {
                    // Start the Bluetooth chat services
                    obj.mChatService[i].start();
                }
            }
        }
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
//        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
//        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        for (int i = 0; i < obj.mChatService.length; i++) {
            if (obj.mChatService[i] != null) obj.mChatService[i].stop();
        }
//        if (obj.mChatService != null) obj.mChatService.stop();
//        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE0:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = obj.mBluetoothAdapter[0].getRemoteDevice(address);
                    // Attempt to connect to the device
                    obj.mChatService[0].connect(device);
                    Thread t = new Thread(){
                        public void run()
                        {

                            while(true)
                            {
                                if(obj.mChatService[0].getState() == GameService.STATE_CONNECTED) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            player2.setEnabled(false);
                                            player3.setEnabled(true);
                                        }
                                    });
                                    break;
                                }
                                try
                                {
                                    Thread.sleep(1000);
                                }
                                catch(Exception e)
                                {}
                            }
                        }
                    };
                    t.start();
//                    start.setEnabled(true);
                }
                break;
            case REQUEST_CONNECT_DEVICE1:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
//                    if(obj.mBluetoothAdapter[0] == BluetoothAdapter.getDefaultAdapter())
//                    {
                    BluetoothDevice device = obj.mBluetoothAdapter[1].getRemoteDevice(address);
                    // Attempt to connect to the device
                    obj.mChatService[1].connect(device);

                    Thread t = new Thread(){
                        public void run()
                        {

                            while(true)
                            {
                                if(obj.mChatService[1].getState() == GameService.STATE_CONNECTED) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            player3.setEnabled(false);
                                            player4.setEnabled(true);
                                        }
                                    });
                                    break;
                                }
                                try
                                {
                                    Thread.sleep(1000);
                                }
                                catch(Exception e)
                                {}
                            }
                        }
                    };
                    t.start();
//                    }
//                    else
//                    {
//                        BluetoothDevice device = obj.mBluetoothAdapter[0].getRemoteDevice(address);
//                        // Attempt to connect to the device
//                        obj.mChatService[0].connect(device);
//                    }
                }
                break;
            case REQUEST_CONNECT_DEVICE2:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
//                    if(obj.mBluetoothAdapter[0] == BluetoothAdapter.getDefaultAdapter() && obj.mBluetoothAdapter[1] != BluetoothAdapter.getDefaultAdapter())
//                    {
                    BluetoothDevice device = obj.mBluetoothAdapter[2].getRemoteDevice(address);
                    // Attempt to connect to the device
                    obj.mChatService[2].connect(device);
                    Thread t = new Thread(){
                        public void run()
                        {

                            while(true)
                            {
                                if(obj.mChatService[2].getState() == GameService.STATE_CONNECTED) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            player4.setEnabled(false);
                                            start.setEnabled(true);
                                        }
                                    });
                                    break;
                                }
                                try
                                {
                                    Thread.sleep(1000);
                                }
                                catch(Exception e)
                                {}
                            }
                        }
                    };
                    t.start();
//                    }
//                    else
//                    {
//                        if(obj.mBluetoothAdapter[0] == BluetoothAdapter.getDefaultAdapter())
//                        {
//                            BluetoothDevice device = obj.mBluetoothAdapter[1].getRemoteDevice(address);
//                            // Attempt to connect to the device
//                            obj.mChatService[1].connect(device);
//                        }
//                        else
//                        {
//                            BluetoothDevice device = obj.mBluetoothAdapter[0].getRemoteDevice(address);
//                            // Attempt to connect to the device
//                            obj.mChatService[0].connect(device);
//                        }
//                    }
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session

                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }


}