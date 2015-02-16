package ca.josephroque.bowlingcompanion;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.josephroque.bowlingcompanion.adapter.BowlerAdapter;
import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.dialog.NewBowlerDialog;


public class MainActivity extends ActionBarActivity
    implements NewBowlerDialog.NewBowlerDialogListener
{

    private static final String TAG = "MainActivity";

    private List<Long> mListBowlerIds;
    private List<String> mListBowlerNames;
    private List<Integer> mListBowlerAverages;

    private RecyclerView mBowlerRecycler;
    private RecyclerView.Adapter mBowlerAdapter;
    private RecyclerView.LayoutManager mBowlerLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set background color of activity
        getWindow().getDecorView()
                .setBackgroundColor(Color.parseColor(Constants.COLOR_BACKGROUND));

        mListBowlerIds = new ArrayList<>();
        mListBowlerNames = new ArrayList<>();
        mListBowlerAverages = new ArrayList<>();

        mBowlerRecycler = (RecyclerView) findViewById(R.id.recyclerView_bowlers);
        mBowlerRecycler.setHasFixedSize(true);

        mBowlerLayoutManager = new LinearLayoutManager(this);
        mBowlerRecycler.setLayoutManager(mBowlerLayoutManager);

        mBowlerAdapter = new BowlerAdapter(mListBowlerIds, mListBowlerNames, mListBowlerAverages);
        mBowlerRecycler.setAdapter(mBowlerAdapter);
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
        switch(item.getItemId())
        {
            case R.id.action_settings:
                break;
            case R.id.action_new_bowler:
                showNewBowlerDialog();
                break;
            case R.id.action_quick_series:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAddNewBowler(String bowlerName)
    {
        boolean mValidInput = true;
        String mInvalidInputMessage = null;

        if (bowlerName == null || bowlerName.length() == 0)
        {
            //No input for the name
            mValidInput = false;
            mInvalidInputMessage = "You must enter a name.";
        }
        else if (mListBowlerNames.contains(bowlerName))
        {
            //Bowler name already exists in the list
            mValidInput = false;
            mInvalidInputMessage = "That name has already been used. You must choose another.";
        }

        if (!mValidInput)
        {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            mBuilder.setMessage(mInvalidInputMessage)
                    .setCancelable(false)
                    .setPositiveButton(Constants.DIALOG_OKAY, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
            return;
        }

        /**
         * Creates a new database entry for the bowler whose name
         * was received by input via the dialog
         */
        new NewBowlerTask().execute(bowlerName);
    }

    private void showNewBowlerDialog()
    {
        DialogFragment mDialogFragment = new NewBowlerDialog();
        mDialogFragment.show(getFragmentManager(), "NewBowlerDialog");
    }

    private class NewBowlerTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... bowlerName)
        {
            long mNewId = -1;
            SQLiteDatabase mDatabase
                    = DatabaseHelper.getInstance(MainActivity.this).getWritableDatabase();
            SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date mCurrentDate = new Date();

            ContentValues mBowlerValues = new ContentValues();
            mBowlerValues.put(BowlerEntry.COLUMN_NAME_BOWLER_NAME, bowlerName[0]);
            mBowlerValues.put(BowlerEntry.COLUMN_NAME_DATE_MODIFIED, mDateFormat.format(mCurrentDate));

            mDatabase.beginTransaction();
            try
            {
                mNewId = mDatabase.insert(BowlerEntry.TABLE_NAME, null, mBowlerValues);

                ContentValues leagueValues = new ContentValues();
                leagueValues.put(LeagueEntry.COLUMN_NAME_LEAGUE_NAME, Constants.NAME_LEAGUE_OPEN);
                leagueValues.put(LeagueEntry.COLUMN_NAME_DATE_MODIFIED, mDateFormat.format(mCurrentDate));
                leagueValues.put(LeagueEntry.COLUMN_NAME_BOWLER_ID, mNewId);
                leagueValues.put(LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES, 1);
                mDatabase.insert(LeagueEntry.TABLE_NAME, null, leagueValues);

                mDatabase.setTransactionSuccessful();
            }
            catch (Exception ex)
            {
                Log.w(TAG, "Error adding new bowler: " + ex.getMessage());
            }
            finally
            {
                mDatabase.endTransaction();
            }

            if (mNewId != -1)
            {
                mListBowlerIds.add(0, mNewId);
                mListBowlerNames.add(0, bowlerName[0]);
                mListBowlerAverages.add(0, 0);
            }

            return String.valueOf(mNewId) + ":" + bowlerName[0];
        }

        @Override
        protected void onPostExecute(String bowlerIdAndName)
        {
            mBowlerAdapter.notifyDataSetChanged();
        }
    }
}
