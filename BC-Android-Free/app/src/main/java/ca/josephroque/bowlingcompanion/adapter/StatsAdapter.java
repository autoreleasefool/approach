package ca.josephroque.bowlingcompanion.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ca.josephroque.bowlingcompanion.R;

/**
 * Created by josephroque on 15-02-25.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.adapter
 * in project Bowling Companion
 */
public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.StatsViewHolder>
{

    private Activity mActivity;
    private List<String> mListStatNames;
    private List<String> mListStatValues;

    public static class StatsViewHolder extends RecyclerView.ViewHolder
    {
        private TextView mTextViewStatName;
        private TextView mTextViewStatValue;

        public StatsViewHolder(View itemLayoutView)
        {
            super(itemLayoutView);
            mTextViewStatName = (TextView)itemLayoutView.findViewById(R.id.textView_stat_name);
            mTextViewStatValue = (TextView)itemLayoutView.findViewById(R.id.textView_stat_value);
        }
    }

    public StatsAdapter(Activity context, List<String> listStatNames, List<String> listStatValues)
    {
        this.mActivity = context;
        this.mListStatNames = listStatNames;
        this.mListStatValues = listStatValues;
    }

    @Override
    public StatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_stats, parent, false);
        return new StatsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StatsViewHolder holder, final int position)
    {
        holder.mTextViewStatName.setText(mListStatNames.get(position) + ":");
        holder.mTextViewStatValue.setText(mListStatValues.get(position));
        holder.itemView.setBackgroundColor(
                mActivity.getResources().getColor(R.color.secondary_background));
    }

    @Override
    public int getItemCount()
    {
        return mListStatNames.size();
    }
}
