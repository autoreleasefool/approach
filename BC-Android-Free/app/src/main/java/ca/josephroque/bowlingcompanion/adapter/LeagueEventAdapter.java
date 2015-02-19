package ca.josephroque.bowlingcompanion.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;

/**
 * Created by josephroque on 15-02-18.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.adapter
 * in project Bowling Companion
 */
public class LeagueEventAdapter extends RecyclerView.Adapter<LeagueEventAdapter.LeagueEventViewHolder>
{
    private static final String TAG = "LeagueEventAdapter";

    private Activity mActivity;

    private List<Long> mListLeagueIds;
    private List<String> mListLeagueNames;
    private List<Integer> mListLeagueAverages;
    private List<Byte> mListLeagueNumberOfGames;

    public static class LeagueEventViewHolder extends RecyclerView.ViewHolder
    {
        private TextView mTextViewLeagueName;
        private TextView mTextViewLeagueAverage;
        private TextView mTextViewLeagueNumberOfGames;

        private LeagueEventViewHolder(View mItemLayoutView)
        {
            super(mItemLayoutView);
            mTextViewLeagueName = (TextView)
                    mItemLayoutView.findViewById(R.id.textView_league_event_name);
            mTextViewLeagueAverage = (TextView)
                    mItemLayoutView.findViewById(R.id.textView_league_event_average);
            mTextViewLeagueNumberOfGames = (TextView)
                    mItemLayoutView.findViewById(R.id.textView_league_event_games);
        }
    }

    public LeagueEventAdapter(
            Activity mActivity,
            List<Long> mListLeagueIds,
            List<String> mListLeagueNames,
            List<Integer> mListLeagueAverages,
            List<Byte> mListLeagueNumberOfGames)
    {
        this.mActivity = mActivity;
        this.mListLeagueIds = mListLeagueIds;
        this.mListLeagueNames = mListLeagueNames;
        this.mListLeagueAverages = mListLeagueAverages;
        this.mListLeagueNumberOfGames = mListLeagueNumberOfGames;
    }

    @Override
    public LeagueEventViewHolder onCreateViewHolder(ViewGroup mParent, int mViewType)
    {
        View mItemLayoutView = LayoutInflater.from(mParent.getContext())
                .inflate(R.layout.list_leagues_events, mParent, false);
        return new LeagueEventViewHolder(mItemLayoutView);
    }

    @Override
    public void onBindViewHolder(LeagueEventViewHolder mHolder, final int mPosition)
    {
        mHolder.mTextViewLeagueName.setText(mListLeagueNames.get(mPosition));
        mHolder.mTextViewLeagueAverage.setText(String.valueOf(mListLeagueAverages.get(mPosition)));
        mHolder.mTextViewLeagueNumberOfGames.setText(String.valueOf(mListLeagueNumberOfGames.get(mPosition)));
        mHolder.itemView.setBackgroundColor(
                mActivity.getResources().getColor(R.color.secondary_background));

        mHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO: openLeagueSeriesTask().execute(mPosition)
            }
        });

        mHolder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                //TODO: showDeleteLeagueDialog(mPosition)
                return true;
            }
        });
    }

    public int getItemCount()
    {
        return mListLeagueIds.size();
    }
}
