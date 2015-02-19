package ca.josephroque.bowlingcompanion.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.adapter.LeagueEventAdapter;
import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;

/**
 * Created by josephroque on 15-02-18.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.fragments
 * in project Bowling Companion
 */
public class LeagueFragment extends Fragment
{

    private static final String TAG = "LeagueFragment";

    private RecyclerView mLeagueRecycler;
    private RecyclerView.Adapter mLeagueAdapter;

    private long mBowlerId = -1;
    private List<Long> mListLeagueIds;
    private List<String> mListLeagueNames;
    private List<Integer> mListLeagueAverages;
    private List<Byte> mListLeagueNumberOfGames;

    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup mContainer, Bundle savedInstance)
    {
        View mRootView = mInflater.inflate(R.layout.fragment_leagues_events, mContainer, false);

        mListLeagueIds = new ArrayList<>();
        mListLeagueNames = new ArrayList<>();
        mListLeagueAverages = new ArrayList<>();
        mListLeagueNumberOfGames = new ArrayList<>();

        mLeagueRecycler = (RecyclerView) mRootView.findViewById(R.id.recyclerView_leagues_events);
        mLeagueRecycler.setHasFixedSize(true);

        RecyclerView.LayoutManager mLeagueLayoutManager = new LinearLayoutManager(getActivity());
        mLeagueRecycler.setLayoutManager(mLeagueLayoutManager);

        mLeagueAdapter = new LeagueEventAdapter(
                getActivity(),
                mListLeagueIds,
                mListLeagueNames,
                mListLeagueAverages,
                mListLeagueNumberOfGames);
        mLeagueRecycler.setAdapter(mLeagueAdapter);

        FloatingActionButton mFloatingActionButton = (FloatingActionButton)mRootView.findViewById(R.id.fab_new_league_event);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //showNewLeagueDialog();
            }
        });

        return mRootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        SharedPreferences preferences = getActivity().getSharedPreferences(Constants.PREFERENCES, Activity.MODE_PRIVATE);
        mBowlerId = preferences.getLong(Constants.PREFERENCE_ID_BOWLER, -1);

        mListLeagueIds.clear();
        mListLeagueNames.clear();
        mListLeagueAverages.clear();
        mListLeagueNumberOfGames.clear();

        new LoadLeaguesTask().execute();
    }

    private class LoadLeaguesTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            SQLiteDatabase mDatabase = DatabaseHelper.getInstance(getActivity()).getReadableDatabase();

            String mRawLeagueQuery = "SELECT "
                    + LeagueEntry.TABLE_NAME + "." + LeagueEntry._ID + " AS lid, "
                    + LeagueEntry.COLUMN_NAME_LEAGUE_NAME + ", "
                    + LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES + ", "
                    + " AVG(" + GameEntry.COLUMN_NAME_GAME_FINAL_SCORE + ") AS avg"
                    + " FROM " + LeagueEntry.TABLE_NAME
                    + " LEFT JOIN " + GameEntry.TABLE_NAME
                    + " ON lid=" + GameEntry.COLUMN_NAME_LEAGUE_ID
                    + " WHERE " + LeagueEntry.COLUMN_NAME_BOWLER_ID + "=? AND " + LeagueEntry.COLUMN_NAME_IS_TOURNAMENT + "=?"
                    + " GROUP BY lid"
                    + " ORDER BY " + LeagueEntry.COLUMN_NAME_DATE_MODIFIED + " DESC";
            String[] mRawLeagueArgs ={String.valueOf(mBowlerId), String.valueOf(0)};

            Cursor mLeagueCursor = mDatabase.rawQuery(mRawLeagueQuery, mRawLeagueArgs);
            if (mLeagueCursor.moveToFirst())
            {
                while(!mLeagueCursor.isAfterLast())
                {
                    String mLeagueName = mLeagueCursor.getString(mLeagueCursor.getColumnIndex(LeagueEntry.COLUMN_NAME_LEAGUE_NAME));
                    long mLeagueID = mLeagueCursor.getLong(mLeagueCursor.getColumnIndex("lid"));
                    byte mNumberOfGames = (byte)mLeagueCursor.getInt(mLeagueCursor.getColumnIndex(LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES));
                    int mLeagueAverage = mLeagueCursor.getInt(mLeagueCursor.getColumnIndex("avg"));
                    mListLeagueIds.add(mLeagueID);
                    mListLeagueNames.add(mLeagueName);
                    mListLeagueAverages.add(mLeagueAverage);
                    mListLeagueNumberOfGames.add(mNumberOfGames);

                    mLeagueCursor.moveToNext();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param)
        {
            mLeagueAdapter.notifyDataSetChanged();
        }
    }
}
