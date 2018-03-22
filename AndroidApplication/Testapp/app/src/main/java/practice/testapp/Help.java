package practice.testapp;

import android.app.ListActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Akshay
 */

public class Help extends ListActivity {

//    ListView help;
    private ArrayAdapter<String> adapter;
    private List<String> liste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_help);
//        help=(ListView) findViewById(R.id.lvHelp);
        String[] values = new String[] {
                "This game supports connection between 4 devices. The connection between the devices depends upon the quality of Bluetooth Adapter.",
                "One device acts as a Host and rest devices will be Client. Click on Player buttons on Host device which will wait to connect players.",
                "The Client devices need to press Connect button and allow Bluetooth discovery.",
                "Once the devices get connected, the Start button will get enable on Host device. Choosing Start button on Host will enable Start button on other devices.",
                "After the connected Client devices, joins the game by pressing Start button, the Game starts.",
                "Out of the four players, each will be randomly alloted king, Queen, police and Kalla. The king player needs to find Queen, and Queen player needs to find police and police needs to locate the Kalla by selecting the respective buttons provided below.",
                "In case, anyone makes a mistake finding right subordinate, their roles will get swapped giving others the chance to find the actual subordinate. This continues till police does not locate Kalla.",
                "After completion of game, players can choose to \"Continue\" or they can \"Exit the Game\"."
        };
        liste = new ArrayList<String>();
        Collections.addAll(liste, values);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, liste);
        setListAdapter(adapter);

    }
}
