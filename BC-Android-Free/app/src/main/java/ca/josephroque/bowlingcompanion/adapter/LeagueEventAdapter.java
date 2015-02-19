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
    private List<Short> mListLeagueAverages;
    private List<Byte> mListLeagueNumberOfGames;

    public static class LeagueEventViewHolder extends RecyclerView.ViewHolder
    {
        private TextView mTextViewLeagueName;
        private TextView mTextViewLeagueAverage;
        private TextView mTextViewLeagueNumberOfGames;

        private LeagueEventViewHolder(View itemLayoutView)
        {
            super(itemLayoutView);
            mTextViewLeagueName = (TextView)
                    itemLayoutView.findViewById(R.id.textView_league_event_name);
            mTextViewLeagueAverage = (TextView)
                    itemLayoutView.findViewById(R.id.textView_league_event_average);
            mTextViewLeagueNumberOfGames = (TextView)
                    itemLayoutView.findViewById(R.id.textView_league_event_games);
        }
    }

    public LeagueEventAdapter(
            Activity activity,
            List<Long> listLeagueIds,
            List<String> listLeagueNames,
            List<Short> listLeagueAverages,
            List<Byte> listLeagueNumberOfGames)
    {
        this.mActivity = activity;
        this.mListLeagueIds = listLeagueIds;
        this.mListLeagueNames = listLeagueNames;
        this.mListLeagueAverages = listLeagueAverages;
        this.mListLeagueNumberOfGames = listLeagueNumberOfGames;
    }

    @Override
    public LeagueEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_leagues_events, parent, false);
        return new LeagueEventViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(LeagueEventViewHolder holder, final int position)
    {
        holder.mTextViewLeagueName.setText(mListLeagueNames.get(position));
        holder.mTextViewLeagueAverage.setText(String.valueOf(mListLeagueAverages.get(position)));
        holder.mTextViewLeagueNumberOfGames.setText(String.valueOf(mListLeagueNumberOfGames.get(position)));
        holder.itemView.setBackgroundColor(
                mActivity.getResources().getColor(R.color.secondary_background));

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO: openLeagueSeriesTask().execute(position)
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                //TODO: showDeleteLeagueDialog(position)
                return true;
            }
        });
    }

    public int getItemCount()
    {
        return mListLeagueIds.size();
    }
}
