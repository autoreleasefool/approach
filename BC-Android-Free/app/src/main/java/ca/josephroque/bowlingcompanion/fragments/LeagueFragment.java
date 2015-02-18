package ca.josephroque.bowlingcompanion.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.adapter.LeagueEventAdapter;

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
    }
}
