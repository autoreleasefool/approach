package ca.josephroque.bowlingcompanion.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.SeriesActivity;

/**
 * Created by josephroque on 15-02-22.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.adapter
 * in project Bowling Companion
 */
public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder>
{
    /** Tag to identify class when outputting to console */
    private static final String TAG = "SeriesAdapter";

    /** Instance of activity which created instance of this object */
    private Activity mActivity;
    /** List of series Ids from "series" table in database to uniquely identify series */
    private List<Long> mListSeriesIds;
    /** List of series dates which will be displayed by RecyclerView */
    private List<String> mListSeriesDate;
    /** List of scores in each series which will be displayed by RecyclerView */
    private List<List<Short>> mListSeriesGames;

    /**
     * Subclass of RecyclerView.ViewHolder to manage views which will display a
     * text to the user.
     */
    public static class SeriesViewHolder extends RecyclerView.ViewHolder
    {
        /** Displays date of the series */
        private TextView mTextViewSeriesDate;
        /** Each TextView displays a different score in the series*/
        private List<TextView> mListTextViewSeriesGames;

        /**
         * Calls super constructor with itemLayoutView as parameter and retrieves references
         * to TextView objects for member variables
         *
         * @param itemLayoutView
         */
        public SeriesViewHolder(View itemLayoutView)
        {
            super(itemLayoutView);
            mTextViewSeriesDate = (TextView)
                    itemLayoutView.findViewById(R.id.textView_series_date);
            mListTextViewSeriesGames = new ArrayList<>();
            for (byte i = 0; i < Constants.MAX_NUMBER_LEAGUE_GAMES; i++)
            {
                switch(i)
                {
                    case 0:
                        mListTextViewSeriesGames.add((TextView)
                                itemLayoutView.findViewById(R.id.textView_series_game_1));
                        break;
                    case 1:
                        mListTextViewSeriesGames.add((TextView)
                                itemLayoutView.findViewById(R.id.textView_series_game_2));
                        break;
                    case 2:
                        mListTextViewSeriesGames.add((TextView)
                                itemLayoutView.findViewById(R.id.textView_series_game_3));
                        break;
                    case 3:
                        mListTextViewSeriesGames.add((TextView)
                                itemLayoutView.findViewById(R.id.textView_series_game_4));
                        break;
                    case 4:
                        mListTextViewSeriesGames.add((TextView)
                                itemLayoutView.findViewById(R.id.textView_series_game_5));
                        break;
                }
            }
        }
    }

    /**
     * Stores references to parameters in member variables
     *
     * @param context activity which created this object
     * @param listSeriesId list of unique series ids from "series" table in database
     * @param listSeriesDate list of dates of series which corresponds to order of ids in listSeriesId
     * @param listSeriesGames list of games of series which corresponds to order of ids in listSeriesId
     */
    public SeriesAdapter(
            Activity context,
            List<Long> listSeriesId,
            List<String> listSeriesDate,
            List<List<Short>> listSeriesGames)
    {
        this.mActivity = context;
        this.mListSeriesIds = listSeriesId;
        this.mListSeriesDate = listSeriesDate;
        this.mListSeriesGames = listSeriesGames;
    }

    @Override
    public SeriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_series, parent, false);
        return new SeriesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SeriesViewHolder holder, final int position)
    {
        /*
         * Sets TextView text to display series dates and games in list to user
         */
        final int numberOfGamesInSeries = mListSeriesGames.get(position).size();
        holder.mTextViewSeriesDate.setText(mListSeriesDate.get(position));
        for (int i = 0; i < numberOfGamesInSeries; i++)
        {
            /*
             * Highlights a score if it is over 300 or applies default theme if not
             */
            short gameScore = mListSeriesGames.get(position).get(-i + (numberOfGamesInSeries - 1));
            holder.mListTextViewSeriesGames.get(i).setText(
                    "  " + String.valueOf(gameScore));
            if (gameScore >= 300)
            {
                holder.mListTextViewSeriesGames.get(i).setTextColor(
                        mActivity.getResources().getColor(R.color.game_above_300));
            }
        }

        /*
         * Below methods are executed when an item in the RecyclerView is
         * clicked or long clicked.
         */
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SeriesActivity seriesActivity = (SeriesActivity) mActivity;
                seriesActivity.openSeries(position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                //TODO: showDeleteSeriesDialog(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mListSeriesIds.size();
    }
}
