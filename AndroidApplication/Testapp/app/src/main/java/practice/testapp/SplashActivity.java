package practice.testapp;

import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Akshay
 */

public class SplashActivity extends ActionBarActivity {

    Button play, about, help;

    //requestCode in onActivityResult.
    private static final int REQUEST_CODE_BLUETOOTH_ON = 1313;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        play= (Button) findViewById(R.id.btnPlayer2);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent j = new Intent(getBaseContext(), ConnectActivity.class);
                startActivity(j);
            }
        });
        help= (Button) findViewById(R.id.btnPlayer3);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent j = new Intent(getBaseContext(), Help.class);
            startActivity(j);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
