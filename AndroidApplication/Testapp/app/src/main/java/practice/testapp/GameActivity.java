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
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
//import android.util.Log;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This is the main Activity that displays the current chat session.
 */
public class GameActivity extends Activity {

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE0 = 1;
    private static final int REQUEST_CONNECT_DEVICE1 = 2;
    private static final int REQUEST_CONNECT_DEVICE2 = 3;
    private static final int REQUEST_ENABLE_BT = 4;

    private Button cand1, cand2, cand3, cand4;
    private ImageView iChar, iFind;
    private TextView tscoreview;
    static ObjectActivity gameObject;
    String type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent intent = getIntent();
        gameObject= HostingActivity.obj;
//        Log.e("using", "is accessible.");
        //gameObject = (ObjectActivity) intent.getSerializableExtra("DATA");
        type = (String) intent.getExtras().get("CONNECTION_TYPE");
//        Toast.makeText(getApplicationContext(), type, Toast.LENGTH_LONG).show();
        // Get local Bluetooth adapter
        gameObject.mBluetoothAdapter = new BluetoothAdapter[3];
        gameObject.mBluetoothAdapter[0] = BluetoothAdapter.getDefaultAdapter();
        gameObject.mBluetoothAdapter[1] = BluetoothAdapter.getDefaultAdapter();
        gameObject.mBluetoothAdapter[2] = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (gameObject.mBluetoothAdapter[0] == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
//            finish();
//            return;
        }
        tscoreview= (TextView) findViewById(R.id.tSc);
        cand1=(Button) findViewById(R.id.btnP1);
        cand1.setText(gameObject.players[0]);
        cand1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callEvaluate(0);
            }
        });
        cand2=(Button) findViewById(R.id.btnP2);
        cand2.setText(gameObject.players[1]);
        cand2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callEvaluate(1);
            }
        });
        cand3=(Button) findViewById(R.id.btnP3);
        cand3.setText(gameObject.players[2]);
        cand3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callEvaluate(2);
            }
        });
        cand4=(Button) findViewById(R.id.btnP4);
        cand4.setText(gameObject.players[3]);
        cand4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callEvaluate(3);
            }
        });
        iChar = (ImageView) findViewById(R.id.imgChar);
        iFind = (ImageView) findViewById(R.id.imgFind);
    }

    private void callEvaluate(int i) {
        for(int j=0;j<4;j++)
        {
            if(gameObject.cards[j].equals(gameObject.chance))
            {
                evaluateResult(i, j);
                break;
            }
        }
    }

    private void  evaluateResult(int i, int j) {
        String message="";
        if(gameObject.chance.equals("K"))
        {
            if(gameObject.cards[i].equals("Q"))
            {
                foundPerfect(j, 10, "Q");
            }
            else
            {
                foundImperfect(i, j, "K");
            }
        }
        else if(gameObject.chance.equals("Q"))
        {
            if(gameObject.cards[i].equals("P"))
            {
                foundPerfect(j, 8, "P");
            }
            else
            {
                foundImperfect(i, j, "Q");
            }
        }
        else if(gameObject.chance.equals("P"))
        {
            if(gameObject.cards[i].equals("T"))
            {
                foundPerfect(j, 6, "A");
            }
            else
            {
               foundImperfect(i, j, "P");
            }
        }
    }

    private void foundImperfect(int i, int j, String chance) {
        String player_temp= gameObject.cards[i];
        gameObject.cards[i]=gameObject.cards[j];
        gameObject.cards[j]=player_temp;
        String message = gameObject.cards[0]+","+gameObject.cards[1]+","+gameObject.cards[2]+","+gameObject.cards[3];
        message+=","+gameObject.score[0]+","+gameObject.score[1]+","+gameObject.score[2]+","+gameObject.score[3]+","+chance;
        gameObject.sendMessage(message);
        if(type.equals("SERVER"))
        {
            enableStart(message);
        }
    }

    private void foundPerfect(int j, int score, String chance) {
        gameObject.score[j]=gameObject.score[j] + score;
        tscoreview.setText(gameObject.score[j] + "");
//        Log.e("Score at j", j + " " + gameObject.score[j]);
        String message = gameObject.cards[0]+","+gameObject.cards[1]+","+gameObject.cards[2]+","+gameObject.cards[3];
        message+=","+gameObject.score[0]+","+gameObject.score[1]+","+gameObject.score[2]+","+gameObject.score[3]+"," + chance;
        gameObject.sendMessage(message);
        if(type.equals("SERVER") && score > 6)
        {
            enableStart(message);
        }
        else if (type.equals("SERVER"))
        {
            gameObject.game_status=ObjectActivity.GAME_FINISH;
            Intent inte = new Intent(getBaseContext(), ScoresActivity.class);
            startActivity(inte);
        }
    }

    @Override
    public void onStart() {
        //String value[] = gameObject.msg.split(",");
        super.onStart();
        Thread t = new Thread() {
            public void run() {
                while (true) {
//                      Log.e("getApplicationContext()", "onCreate() " + obj.game_status);
                    if (gameObject.game_status == ObjectActivity.GAME_START) {
                        if(!gameObject.getMsg().equals("")) {
//                            if(gameObject.getMsg().length()<=9)
                                enableStart(gameObject.getMsg());
                                if(type.equals("SERVER"))
                                {
//                                    Log.e("New getMsg()", gameObject.getMsg());
                                    gameObject.sendMessage(gameObject.getMsg());
                                }
//                            else
//                                executeGame();
                        }
                        gameObject.setMsg("");
                    }
                    else if(gameObject.game_status == ObjectActivity.GAME_FINISH)
                    {
                        Intent j = new Intent(getBaseContext(), ScoresActivity.class);
                        startActivity(j);
                        break;
                    }
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (Exception e)
                    {}
                }
            }
        };
        t.start();
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
//        if(D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        for(int i=0;i<gameObject.mChatService.length;i++)
        {
            if (gameObject.mChatService[i] != null) {
                // Only if the state is STATE_NONE, do we know that we haven't started already
                if (gameObject.mChatService[i].getState() == GameService.STATE_NONE) {
                    // Start the Bluetooth chat services
                    gameObject.mChatService[i].start();
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
        for(int i=0;i<gameObject.mChatService.length;i++) {
            if (gameObject.mChatService[i] != null) gameObject.mChatService[i].stop();
        }
//        if (gameObject.mChatService != null) gameObject.mChatService.stop();
//        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }

    private void enableStart( final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                Log.e("value received at str", str);
                String values[] = str.split(",");
                if (values.length <= 9) {
                    for (int i = 0; i < 4; i++) {
                        gameObject.cards[i] = values[i];
                    }
                    for (int i = 4; i < 8; i++) {
                        gameObject.score[i - 4] = Integer.parseInt(values[i]);
                    }
                    gameObject.chance = values[8];
                }
                if(gameObject.chance.equals("A"))
                {
                    gameObject.game_status=ObjectActivity.GAME_FINISH;
                }
                executeGame();
            }
        });
    }

    private void executeGame() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cand1.setEnabled(false);
                cand2.setEnabled(false);
                cand3.setEnabled(false);
                cand4.setEnabled(false);
                for (int i = 0; i < 4; i++) {

                    //Testing
//                    if(gameObject.players[i]==null)
//                        continue;

                    if (gameObject.players[i].equals(BluetoothAdapter.getDefaultAdapter().getName())) {
//                        Log.e("Adapter Name", BluetoothAdapter.getDefaultAdapter().getName());
//                       Toast.makeText(getApplicationContext(), gameObject.cards[i]+"\t"+ gameObject.chance, Toast.LENGTH_LONG).show();
                        if (gameObject.cards[i].equals("K")) {
                            iChar.setImageResource(R.drawable.king);
                            switch (gameObject.chance) {
                                case "K":
                                    iFind.setImageResource(R.drawable.queen_big);
                                    if (i == 0)
                                        enableButtons(1, 2, 3, "K");
                                    else if (i == 1)
                                        enableButtons(0, 2, 3, "K");
                                    else if (i == 2)
                                        enableButtons(0, 1, 3, "K");
                                    else
                                        enableButtons(0, 1, 2, "K");
                                    break;
                                case "Q":
                                    iFind.setImageResource(R.drawable.police_big);
                                    break;
                                case "P":
                                    iFind.setImageResource(R.drawable.thief_big);
                                    break;
                            }
                            break;
                        }
                        if (gameObject.cards[i].equals("Q")) {
                            iChar.setImageResource(R.drawable.queen);
                            switch (gameObject.chance) {
                                case "K":
                                    iFind.setImageResource(R.drawable.queen_big);
                                    break;
                                case "Q":
                                    iFind.setImageResource(R.drawable.police_big);
                                    if (i == 0)
                                        enableButtons(1, 2, 3, "Q");
                                    else if (i == 1)
                                        enableButtons(0, 2, 3, "Q");
                                    else if (i == 2)
                                        enableButtons(0, 1, 3, "Q");
                                    else
                                        enableButtons(0, 1, 2, "Q");
                                    break;
                                case "P":
                                    iFind.setImageResource(R.drawable.thief_big);
                                    break;
                            }
                            break;
                        }
                        if (gameObject.cards[i].equals("P")) {
                            iChar.setImageResource(R.drawable.police);
                            switch (gameObject.chance) {
                                case "K":
                                    iFind.setImageResource(R.drawable.queen_big);
                                    break;
                                case "Q":
                                    iFind.setImageResource(R.drawable.police_big);
                                    break;
                                case "P":
                                    iFind.setImageResource(R.drawable.thief_big);
                                    if (i == 0)
                                        enableButtons(1, 2, 3, "P");
                                    else if (i == 1)
                                        enableButtons(0, 2, 3, "P");
                                    else if (i == 2)
                                        enableButtons(0, 1, 3, "P");
                                    else
                                        enableButtons(0, 1, 2, "P");
                                    break;
                            }
                            break;
                        }
                        if (gameObject.cards[i].equals("T")) {
                            iChar.setImageResource(R.drawable.thief);
                            switch (gameObject.chance) {
                                case "K":
                                    iFind.setImageResource(R.drawable.queen_big);
                                    break;
                                case "Q":
                                    iFind.setImageResource(R.drawable.police_big);
                                    break;
                                case "P":
                                    iFind.setImageResource(R.drawable.thief_big);
                                    break;
                            }
                            break;
                        }
                    }
                }
            }
        });
    }

    private void enableButtons(int i, int i1, int i2, String chance) {
        switch (i)
        {
            case 0: cand1.setEnabled(true); break;
            case 1: cand2.setEnabled(true); break;
            case 2: cand3.setEnabled(true); break;
            case 3: cand4.setEnabled(true); break;
        }
        switch (i1)
        {
            case 0: cand1.setEnabled(true); break;
            case 1: cand2.setEnabled(true); break;
            case 2: cand3.setEnabled(true); break;
            case 3: cand4.setEnabled(true); break;
        }
        switch (i2)
        {
            case 0: cand1.setEnabled(true); break;
            case 1: cand2.setEnabled(true); break;
            case 2: cand3.setEnabled(true); break;
            case 3: cand4.setEnabled(true); break;
        }
        if(chance.equals("Q"))
        {
            for(int locate=0;locate<4;locate++)
            {
                if(gameObject.cards[locate].equals("K"))
                {
                    switch (locate)
                    {
                        case 0: cand1.setEnabled(false); break;
                        case 1: cand2.setEnabled(false); break;
                        case 2: cand3.setEnabled(false); break;
                        case 3: cand4.setEnabled(false); break;
                    }
                }
            }
        }
        if(chance.equals("P"))
        {
            for(int locate=0;locate<4;locate++)
            {
                if(gameObject.cards[locate].equals("K"))
                {
                    switch (locate)
                    {
                        case 0: cand1.setEnabled(false); break;
                        case 1: cand2.setEnabled(false); break;
                        case 2: cand3.setEnabled(false); break;
                        case 3: cand4.setEnabled(false); break;
                    }
                    break;
                }
            }
            for(int locate=0;locate<4;locate++)
            {
                if(gameObject.cards[locate].equals("Q"))
                {
                    switch (locate)
                    {
                        case 0: cand1.setEnabled(false); break;
                        case 1: cand2.setEnabled(false); break;
                        case 2: cand3.setEnabled(false); break;
                        case 3: cand4.setEnabled(false); break;
                    }
                    break;
                }
            }
        }
    }
}


