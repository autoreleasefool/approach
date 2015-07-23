package ca.josephroque.bowlingcompanion.adapter;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.theme.Theme;

/**
 * Created by Joseph Roque on 15-03-17.
 * <p/>
 * Manages series and their associated games for a ListView. Offers a callback interface
 * {@link SeriesAdapter.SeriesEventHandler} to handle interaction events.
 */
public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder>
        implements Theme.ChangeableTheme, View.OnClickListener
{

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "SeriesAdapter";

    /** Activity which created the instance of this object. */
    private Activity mActivity;
    /** Instance of handler for callback on user action. */
    private SeriesEventHandler mEventHandler;

    /** List of dates which will be displayed. */
    private List<String> mListDates;
    /** List of games which will be displayed, in an order relative to mListDates. */
    private List<List<Short>> mListGames;

    /** Indicates minimum score values which will be highlighted when displayed. */
    private int minimumScoreToHighlight = Constants.DEFAULT_GAME_HIGHLIGHT;

    /**
     * Subclass of RecyclerView.ViewHolder to manage view which will display
     * text to the user.
     */
    public static class SeriesViewHolder extends RecyclerView.ViewHolder
    {
        /** Displays date of the series. */
        private TextView mTextViewDate;
        /** Each TextView displays a different score in the series. */
        private TextView[] mArrayTextViewGames;
        /** Displays an icon to allow editing of the date of a series. */
        private ImageView mImageViewEdit;

        /**
         * Calls super constructor and gets instances of ImageView and TextView objects
         * for member variables from itemLayoutView.
         *
         * @param itemLayoutView layout view containing views to display data
         */
        public SeriesViewHolder(View itemLayoutView)
        {
            super(itemLayoutView);
            mTextViewDate = (TextView) itemLayoutView.findViewById(R.id.tv_series_date);
            mImageViewEdit = (ImageView) itemLayoutView.findViewById(R.id.iv_edit_date);

            //Adds text views by id to array
            mArrayTextViewGames = new TextView[Constants.MAX_NUMBER_LEAGUE_GAMES];
            for (byte i = 0; i < Constants.MAX_NUMBER_LEAGUE_GAMES; i++)
            {
                switch (i)
                {
                    case 0:
                        mArrayTextViewGames[0] =
                                (TextView) itemLayoutView.findViewById(R.id.tv_series_game_1);
                        break;
                    case 1:
                        mArrayTextViewGames[1] =
                                (TextView) itemLayoutView.findViewById(R.id.tv_series_game_2);
                        break;
                    case 2:
                        mArrayTextViewGames[2] =
                                (TextView) itemLayoutView.findViewById(R.id.tv_series_game_3);
                        break;
                    case 3:
                        mArrayTextViewGames[3] =
                                (TextView) itemLayoutView.findViewById(R.id.tv_series_game_4);
                        break;
                    case 4:
                        mArrayTextViewGames[4] =
                                (TextView) itemLayoutView.findViewById(R.id.tv_series_game_5);
                        break;
                    default:
                        // does nothing
                }
            }
        }
    }

    /**
     * Sets member variables to parameters.
     *
     * @param activity activity which created this instance
     * @param eventHandler handles on click/long click events on views
     * @param listDates list of dates to be displayed in RecyclerView
     * @param listGames list of game scores, relative to listDates to be displayed
     */
    public SeriesAdapter(
            Activity activity,
            SeriesEventHandler eventHandler,
            List<String> listDates,
            List<List<Short>> listGames)
    {
        this.mActivity = activity;
        this.mEventHandler = eventHandler;
        this.mListDates = listDates;
        this.mListGames = listGames;
    }

    @Override
    public SeriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_series, parent, false);
        return new SeriesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SeriesViewHolder holder, final int position)
    {
        holder.mTextViewDate.setText(mListDates.get(position));

        final int numberOfGamesInSeries = mListGames.get(position).size();
        for (int i = 0; i < numberOfGamesInSeries; i++)
        {
            /**
             * Highlights a score if it is over 300 or applies default theme if not
             */
            short gameScore = mListGames.get(position).get(-i + (numberOfGamesInSeries - 1));
            holder.mArrayTextViewGames[i].setText(String.valueOf(gameScore));
            if (gameScore >= minimumScoreToHighlight)
            {
                holder.mArrayTextViewGames[i].setTextColor(Theme.getTertiaryThemeColor());
                holder.mArrayTextViewGames[i].setAlpha(1f);
            }
            else
            {
                holder.mArrayTextViewGames[i].setTextColor(0xff000000);
                holder.mArrayTextViewGames[i].setAlpha(0.54f);
            }
        }

        //Sets color of edit button
        Drawable drawable = holder.mImageViewEdit.getDrawable().mutate();
        drawable.setColorFilter(Theme.getSecondaryThemeColor(), PorterDuff.Mode.SRC_IN);
        holder.mImageViewEdit.setImageDrawable(drawable);

        holder.mImageViewEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mEventHandler.onEditClick(position);
            }
        });

        /*
         * Below methods are executed when an item in the RecyclerView is
         * clicked or long clicked.
         */
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        int position;
        try {
            position = (Integer) v.getTag();
        } catch (ClassCastException ex) {
            throw new ClassCastException("tag must be position");
        }
        //Calls relevant event handler method
        mEventHandler.onSItemClick(position);
    }

    @Override
    public int getItemCount()
    {
        return mListDates.size();
    }

    @Override
    public void updateTheme()
    {
        minimumScoreToHighlight =
                Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(mActivity)
                .getString(Constants.KEY_HIGHLIGHT_SCORE, "300"));
        notifyDataSetChanged();
    }

    /**
     * Provides methods to implement functionality when items
     * in the RecyclerView are interacted with.
     */
    public interface SeriesEventHandler
    {
        /**
         * Called when an item in the RecyclerView is clicked.
         *
         * @param position position of the item in the list
         */
        void onSItemClick(final int position);

        /**
         * Called when the edit image view for an item in the RecyclerView is clicked.
         *
         * @param position position of the item in the list
         */
        void onEditClick(final int position);
    }
}
