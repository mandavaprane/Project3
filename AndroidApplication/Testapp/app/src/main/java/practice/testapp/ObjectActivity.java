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

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;

/**
 * This is the main Activity that displays the current chat session.
 */
@SuppressWarnings("serial")
public class ObjectActivity extends Application implements Serializable {

    // Message types sent from the GameService Handler
    public static transient final int MESSAGE_STATE_CHANGE = 1;
    public static transient final int MESSAGE_READ = 2;
    public static transient final int MESSAGE_WRITE = 3;
    public static transient final int MESSAGE_DEVICE_NAME = 4;
    public static transient final int MESSAGE_TOAST = 5;
    // Key names received from the GameService Handler
    public static transient final String DEVICE_NAME = "device_name";
    public static transient final String TOAST = "toast";

    public static final int REPLY_RECEIVED = 98;   // for HostingActivity
    public static final int GAME_START = 99;       // for GameActivity
    public static final int GAME_FINISH = 100;
    public static int game_status;
    // Name of the connected device
    protected String mConnectedDeviceName[] = new String[3];
    protected String cards[] = new String[4];
    protected String players[] = new String[4];
    protected String chance = new String("");
    protected int score[] = new int[4];
    // Local Bluetooth adapter
    protected transient BluetoothAdapter mBluetoothAdapter[] = null;
    // Member object for the chat services
    protected GameService mChatService[] = new GameService[3];
    protected transient Context con;
    protected static String msg="";

     //ObjectActivity obj;

    public ObjectActivity(Context c) {
//        if (obj == null) {
//            obj = new ObjectActivity(c);
//        }
            con=c;
    }

    public void setupChat(int j) {
//        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread

        // Initialize the GameService to perform bluetooth connections

        if (j == 0) {
            mChatService[j] = new GameService(con, "fa87c0d0-afac-11de-8a39-0800200c9a66", mHandler0);
        }
        else if(j==1)
        {
//            if (mChatService[0] != null)
//            {
                mChatService[j] = new GameService(con, "fa87c0d1-afac-11de-8a39-0800200c9a66", mHandler1);
//            }
//            else
//            {
//                mChatService[0] = new GameService(this, "fa87c0d1-afac-11de-8a39-0800200c9a66", mHandler1);
//            }
        }
        else
        {
//            if (mChatService[0] != null && mChatService[1] != null)
//            {
                mChatService[j] = new GameService(con, "fa87c0d2-afac-11de-8a39-0800200c9a66", mHandler2);
//            }
//            else if (mChatService[0] == null)
//            {
//                mChatService[0] = new GameService(this, "fa87c0d2-afac-11de-8a39-0800200c9a66", mHandler2);
//            }
//            else
//            {
//                mChatService[1] = new GameService(this, "fa87c0d2-afac-11de-8a39-0800200c9a66", mHandler2);
//            }
        }
    }

    public void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        for(int i=0;i<mChatService.length;i++) {
//            Log.e("value", i +  " " + mChatService[i].toString());
//            Log.e("log", i +  " " + mChatService[i].getState());
//            Toast.makeText(getApplicationContext(), mChatService[i+1].getState(), Toast.LENGTH_LONG).show();
//            Toast.makeText(getApplicationContext(), mChatService[i].getState(), Toast.LENGTH_LONG).show();
            if (mChatService[i].getState() != GameService.STATE_CONNECTED) {
//                Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
//                return;
                continue;
            }

            // Check that there's actually something to send
            if (message.length() > 0) {
                // Get the message bytes and tell the GameService to write
                byte[] send = message.getBytes();
                mChatService[i].write(send);
            }
        }
    }

    // The Handler that gets information back from the GameService
    private final Handler mHandler0 = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
//                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case GameService.STATE_CONNECTED:
//                    mTitle.setText(R.string.title_connected_to);
//                    mTitle.append(mConnectedDeviceName);
//                            Log.d("mTitle", "state_connected");
//                            mConversationArrayAdapter.clear();
                            break;
                        case GameService.STATE_CONNECTING:
//                    mTitle.setText(R.string.title_connecting);
//                            Log.d("mTitle", "state_connecting");
                            break;
                        case GameService.STATE_LISTEN:
                        case GameService.STATE_NONE:
//                    mTitle.setText("NONE");
                            Log.d("mTitle", "listening or none");
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
//                    Log.e("Write:", "writing");
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
//                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case MESSAGE_READ:
//                    Log.e("Read:", "reading");
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    mConversationArrayAdapter.add(mConnectedDeviceName[0]+":  " + readMessage);
                    applyReadSettings(readMessage);
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName[0] = msg.getData().getString(DEVICE_NAME);
//                    Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName[0], Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
//                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private final Handler mHandler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
//                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case GameService.STATE_CONNECTED:
//                    mTitle.setText(R.string.title_connected_to);
//                    mTitle.append(mConnectedDeviceName);
//                            Log.d("mTitle", "state_connected");
//                            mConversationArrayAdapter.clear();
                            break;
                        case GameService.STATE_CONNECTING:
//                    mTitle.setText(R.string.title_connecting);
//                            Log.d("mTitle", "state_connecting");
                            break;
                        case GameService.STATE_LISTEN:
                        case GameService.STATE_NONE:
//                    mTitle.setText("NONE");
//                            Log.d("mTitle", "listening or none");
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
//                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    mConversationArrayAdapter.add(mConnectedDeviceName[1]+":  " + readMessage);
                    applyReadSettings(readMessage);
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName[1] = msg.getData().getString(DEVICE_NAME);
//                    Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName[1], Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
//                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
//                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void applyReadSettings(String value) {
        msg = value;
        if(mChatService[0].getState()==GameService.STATE_CONNECTED && mChatService[1].getState()==GameService.STATE_CONNECTED && mChatService[2].getState()==GameService.STATE_CONNECTED)
        {
//            game_status=GAME_START;
        }
        if(mChatService[0].getState()==GameService.STATE_CONNECTED && mChatService[1].getState()!=GameService.STATE_CONNECTED && mChatService[2].getState()!=GameService.STATE_CONNECTED)
        {
            if(game_status != GAME_START && game_status != GAME_FINISH) {
                game_status = REPLY_RECEIVED;
            }
        }
        if(mChatService[0].getState()!=GameService.STATE_CONNECTED && mChatService[1].getState()==GameService.STATE_CONNECTED && mChatService[2].getState()!=GameService.STATE_CONNECTED)
        {
            if(game_status != GAME_START && game_status != GAME_FINISH) {
                game_status = REPLY_RECEIVED;
            }
        }
        if(mChatService[0].getState()!=GameService.STATE_CONNECTED && mChatService[1].getState()!=GameService.STATE_CONNECTED && mChatService[2].getState()==GameService.STATE_CONNECTED)
        {
            if(game_status != GAME_START && game_status != GAME_FINISH) {
                game_status = REPLY_RECEIVED;
            }
        }
    }

    private final Handler mHandler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
//                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case GameService.STATE_CONNECTED:
//                    mTitle.setText(R.string.title_connected_to);
//                    mTitle.append(mConnectedDeviceName);
//                            Log.d("mTitle", "state_connected");
//                            mConversationArrayAdapter.clear();
                            break;
                        case GameService.STATE_CONNECTING:
//                    mTitle.setText(R.string.title_connecting);
//                            Log.d("mTitle", "state_connecting");
                            break;
                        case GameService.STATE_LISTEN:
                        case GameService.STATE_NONE:
//                    mTitle.setText("NONE");
//                            Log.d("mTitle", "listening or none");
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
//                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    mConversationArrayAdapter.add(mConnectedDeviceName[2]+":  " + readMessage);
                    applyReadSettings(readMessage);
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName[2] = msg.getData().getString(DEVICE_NAME);
//                    Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName[2], Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
//                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
//                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public String[] getmConnectedDeviceName() {
        return mConnectedDeviceName;
    }

    public void setmConnectedDeviceName(String[] mConnectedDeviceName) {
        this.mConnectedDeviceName = mConnectedDeviceName;
    }

    public int[] getScore() {
        return score;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setScore(int[] score) {
        this.score = score;
    }

    public String[] getCards() {
        return cards;
    }

    public void setCards(String[] cards) {
        this.cards = cards;
    }

    public BluetoothAdapter[] getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public void setmBluetoothAdapter(BluetoothAdapter[] mBluetoothAdapter) {
        this.mBluetoothAdapter = mBluetoothAdapter;
    }

    public GameService[] getmChatService() {
        return mChatService;
    }

    public void setmChatService(GameService[] mChatService) {
        this.mChatService = mChatService;
    }
}