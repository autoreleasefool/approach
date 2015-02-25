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
    /** Tag to identify class when outputting to console */
    private static final String TAG = "StatsAdapter";
    /** Represents data which should be displayed as a header */
    private static final int VIEWTYPE_HEADER = 0;
    /** Represents data which should be displayed as a regular list item */
    private static final int VIEWTYPE_BODY = 1;

    /** Activity which created this object */
    private Activity mActivity;
    /** List of stat descriptions which will be displayed by RecyclerView */
    private List<String> mListStatNames;
    /** List of stat values which will be displayed by RecyclerView */
    private List<String> mListStatValues;

    /**
     * Subclass of RecyclerView.ViewHolder to manage views which will display
     * text to the user.
     */
    public static class StatsViewHolder extends RecyclerView.ViewHolder
    {
        /** Displays description of the stat */
        private TextView mTextViewStatName;
        /** Displays corresponding value of the stat*/
        private TextView mTextViewStatValue;

        /**
         * Calls super constructor with itemLayoutView as parameter and retrieves references
         * to TextView objects for member variables depending on viewType
         *
         * @param itemLayoutView parent layout of list item
         * @param viewType type of the list item which is being displayed
         */
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

    /**
     * Constructor which stores references to parameters in member variables
     *
     * @param context
     * @param listStatNames
     * @param listStatValues
     */
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
        //Sets text of TextView objects to display stats values to user
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
        /*
         * If a value in mListStatNames begins with the character "-"
         * then that row will be treated as a header to differentiate the
         * stats below from those above
         */
        if (mListStatNames.get(position).startsWith("-"))
            return VIEWTYPE_HEADER;
        else
            return VIEWTYPE_BODY;
    }
}
