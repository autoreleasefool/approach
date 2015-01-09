package ca.josephroque.bowlingcompanion;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity
    implements NewBowlerDialog.NewBowlerDialogListener
{

    private static final String ADD_NEW_BOWLER = "Add new bowler";

    private static final String[] FILLER_NAMES = {"Joseph Roque", "Jordan Roque", "Audriana Roque",
            "Pam Roque", "Ruben Roque", "Ryan Groombridge", "Stephanie Hale", "Sarah Szymanski",
            "Cameron Thompson", "Ryan Cecchini", "Matt Smith"};

    private static String selectedBowlerID = null;
    public static String getSelectedBowlerID() {return selectedBowlerID;}

    private ArrayList<String> bowlerNamesArrayList = null;
    private ArrayAdapter<String> bowlerAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        TODO: Replace filler values
        The following block adds filler values to the list view on the main menu.
        This needs to be populated with actual bowler names
         */
        final ListView listBowlerNames = (ListView) findViewById(R.id.list_bowler_name);

        bowlerNamesArrayList = new ArrayList<String>();
        for (String name : FILLER_NAMES)
        {
            bowlerNamesArrayList.add(name);
        }
        bowlerNamesArrayList.add(ADD_NEW_BOWLER);

        bowlerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bowlerNamesArrayList);
        listBowlerNames.setAdapter(bowlerAdapter);
        bowlerAdapter.notifyDataSetChanged();

        listBowlerNames.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                selectedBowlerID = (String) listBowlerNames.getItemAtPosition(position);

                if (selectedBowlerID.equals(ADD_NEW_BOWLER))
                {
                    showNewBowlerDialog();
                }
                else
                {
                    Intent leagueIntent = new Intent(MainActivity.this, LeagueActivity.class);
                    startActivity(leagueIntent);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showNewBowlerDialog()
    {
        DialogFragment dialog = new NewBowlerDialog();
        dialog.show(getSupportFragmentManager(), "NewBowlerDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String firstName, String lastName)
    {
        //adds a new name to the list of bowlers
        //TODO: Create database entry for name
        bowlerNamesArrayList.add(bowlerNamesArrayList.size() - 1, firstName + " " + lastName);
        bowlerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog)
    {
        //does nothing
    }
}
