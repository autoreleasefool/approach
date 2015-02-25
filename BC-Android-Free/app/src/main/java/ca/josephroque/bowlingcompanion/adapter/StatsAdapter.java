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

    private static final String TAG = "StatsAdapter";
    private static final int VIEWTYPE_HEADER = 0;
    private static final int VIEWTYPE_BODY = 1;

    private Activity mActivity;
    private List<String> mListStatNames;
    private List<String> mListStatValues;

    public static class StatsViewHolder extends RecyclerView.ViewHolder
    {
        private TextView mTextViewStatName;
        private TextView mTextViewStatValue;

        public StatsViewHolder(View itemLayoutView, int viewType)
        {
            super(itemLayoutView);

            switch(viewType)
            {
                case VIEWTYPE_HEADER:
                    mTextViewStatName = (TextView)itemLayoutView.findViewById(R.id.textView_stat_header);
                    break;
                case VIEWTYPE_BODY:
                    mTextViewStatName = (TextView)itemLayoutView.findViewById(R.id.textView_stat_name);
                    mTextViewStatValue = (TextView)itemLayoutView.findViewById(R.id.textView_stat_value);
                    break;
            }
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
        View itemView;
        switch(viewType)
        {
            case VIEWTYPE_HEADER:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_stats_header, parent, false);
                break;
            case VIEWTYPE_BODY:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_stats_body, parent, false);
                break;
            default:
                throw new IllegalArgumentException("viewType must be VIEWTYPE_HEADER or VIEWTYPE_BODY");
        }

        return new StatsViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(StatsViewHolder holder, final int position)
    {
        switch(getItemViewType(position))
        {
            case VIEWTYPE_HEADER:
                holder.mTextViewStatName.setText(mListStatNames.get(position).substring(1));
                break;
            case VIEWTYPE_BODY:
                holder.mTextViewStatName.setText(mListStatNames.get(position) + ":");
                holder.mTextViewStatValue.setText(mListStatValues.get(position));
                break;
        }
    }

    @Override
    public int getItemCount()
    {
        return mListStatNames.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        if (mListStatNames.get(position).startsWith("-"))
            return VIEWTYPE_HEADER;
        else
            return VIEWTYPE_BODY;
    }
}
