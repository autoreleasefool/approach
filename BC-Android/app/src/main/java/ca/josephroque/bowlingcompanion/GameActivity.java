package ca.josephroque.bowlingcompanion;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class GameActivity extends ActionBarActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        HorizontalScrollView hsvFrames = (HorizontalScrollView)findViewById(R.id.hsv_frames);
        List<List<TextView>> ballsTextViews = new ArrayList<List<TextView>>(10);
        List<TextView> framesTextViews = new ArrayList<TextView>(10);

        for (int i = 0; i < 10; i++)
        {
            ballsTextViews.add(new ArrayList<TextView>(3));
            TextView frameText = new TextView(this);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id)
        {
            case R.id.action_game_stats:
                showGameStats();
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showGameStats()
    {
        //TODO: showGameStats()
    }
}
