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
public class EventFragment extends Fragment
{
    private static final String TAG = "EventFragment";

    private RecyclerView mEventRecycler;
    private RecyclerView.Adapter mEventAdapter;

    private long mBowlerId = -1;
    private List<Long> mListEventIds;
    private List<String> mListEventNames;
    private List<Integer> mListEventAverages;
    private List<Byte> mListEventNumberOfGames;

    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup mContainer, Bundle savedInstance)
    {
        View mRootView = mInflater.inflate(R.layout.fragment_leagues_events, mContainer, false);

        mListEventIds = new ArrayList<>();
        mListEventNames = new ArrayList<>();
        mListEventAverages = new ArrayList<>();
        mListEventNumberOfGames = new ArrayList<>();

        mEventRecycler = (RecyclerView) mRootView.findViewById(R.id.recyclerView_leagues_events);
        mEventRecycler.setHasFixedSize(true);

        RecyclerView.LayoutManager mEventLayoutManager = new LinearLayoutManager(getActivity());
        mEventRecycler.setLayoutManager(mEventLayoutManager);

        mEventAdapter = new LeagueEventAdapter(
                getActivity(),
                mListEventIds,
                mListEventNames,
                mListEventAverages,
                mListEventNumberOfGames);
        mEventRecycler.setAdapter(mEventAdapter);

        FloatingActionButton mFloatingActionButton = (FloatingActionButton)mRootView.findViewById(R.id.fab_new_league_event);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //showNewEventDialog();
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

        mListEventIds.clear();
        mListEventNames.clear();
        mListEventAverages.clear();
        mListEventNumberOfGames.clear();
    }
}
