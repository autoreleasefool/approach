package ca.josephroque.bowlingcompanion;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class SeriesActivity extends ActionBarActivity
{

    private static final String TAG = "SeriesActivity";

    private List<Long> mListSeriesId;
    private List<String> mListSeriesDate;
    private List<List<Short>> mListSeriesGames;

    /** View to display series dates and games */
    private RecyclerView mSeriesRecycler;
    /** Adapter to manage data displayed in mSeriesRecycler */
    private RecyclerView.Adapter mSeriesAdapter;
    /** TextView to display instructions to the user */
    private TextView mSeriesInstructionsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.primary_green)));

        //Set background color of activity
        getWindow().getDecorView()
                .setBackgroundColor(getResources().getColor(R.color.primary_background));

        mListSeriesId = new ArrayList<Long>();
        mListSeriesDate = new ArrayList<String>();
        mListSeriesGames = new ArrayList<>();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_series, menu);
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
}
