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

    private static final String TAG = "SeriesAdapter";

    private Activity mActivity;

    private List<Long> mListSeriesId;
    private List<String> mListSeriesDate;
    private List<List<Short>> mListSeriesGames;

    public static class SeriesViewHolder extends RecyclerView.ViewHolder
    {
        private TextView mTextViewSeriesDate;
        private List<TextView> mListTextViewSeriesGames;

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

    public SeriesAdapter(
            Activity context,
            List<Long> listSeriesId,
            List<String> listSeriesDate,
            List<List<Short>> listSeriesGames)
    {
        this.mActivity = context;
        this.mListSeriesId = listSeriesId;
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
        holder.mTextViewSeriesDate.setText(mListSeriesDate.get(position));
        for (int i = 0; i < mListSeriesGames.get(position).size(); i++)
        {
            short gameScore = mListSeriesGames.get(position).get(i);
            holder.mListTextViewSeriesGames.get(i).setText("  " + String.valueOf(mListSeriesGames.get(position).get(i)));
            if (gameScore >= 300)
            {
                holder.mListTextViewSeriesGames.get(i).setTextColor(
                        mActivity.getResources().getColor(R.color.game_above_300));
            }
        }

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
                return true;
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mListSeriesId.size();
    }
}
