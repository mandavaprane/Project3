package practice.testapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.*;
//import android.util.Log;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Akshay
 */
public class ScoresActivity extends Activity {

    TextView t1, s1, t2, s2, t3, s3, t4, s4;
    Button bCont, bEnd;
    ObjectActivity scoreObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        scoreObject=HostingActivity.obj;
        scoreObject.game_status=0;
        bCont = (Button) findViewById(R.id.btnPlayAgain);
        bCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        bEnd = (Button) findViewById(R.id.btnExit);
        bEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sMain = new Intent(Intent.ACTION_MAIN);
                sMain.addCategory(Intent.CATEGORY_HOME);
                sMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(sMain);
                finish();
            }
        });
        t1=(TextView) findViewById(R.id.txtPlayer1);
        s1=(TextView) findViewById(R.id.txtScore1);
        t2=(TextView) findViewById(R.id.txtPlayer2);
        s2=(TextView) findViewById(R.id.txtScore2);
        t3=(TextView) findViewById(R.id.txtPlayer3);
        s3=(TextView) findViewById(R.id.txtScore3);
        t4=(TextView) findViewById(R.id.txtPlayer4);
        s4=(TextView) findViewById(R.id.txtScore4);
        t1.setText(scoreObject.players[0]);
        t2.setText(scoreObject.players[1]);
        t3.setText(scoreObject.players[2]);
        t4.setText(scoreObject.players[3]);
        s1.setText(scoreObject.score[0]+"");
        s2.setText(scoreObject.score[1]+"");
        s3.setText(scoreObject.score[2]+"");
        s4.setText(scoreObject.score[3]+"");
    }
}
