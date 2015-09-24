package ca.josephroque.bowlingcompanion.adapter;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.data.Series;
import ca.josephroque.bowlingcompanion.theme.Theme;
import ca.josephroque.bowlingcompanion.utilities.DisplayUtils;

/**
 * Created by Joseph Roque on 15-03-17. Manages series and their associated games for a ListView. Offers a callback
 * interface {@link SeriesAdapter.SeriesEventHandler} to handle interaction events.
 */
public class SeriesAdapter
        extends RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder>
        implements Theme.ChangeableTheme, View.OnClickListener {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "SeriesAdapter";

    /** Represents an item in the list which is active. */
    private static final int VIEWTYPE_ACTIVE = 0;
    /** Represents an item in the list which has been deleted. */
    private static final int VIEWTYPE_DELETED = 1;

    /** Textual representations of match play results. */
    private static final String[] MATCH_PLAY_INDICATORS = {"", "W", "L", "T"};

    /** Activity which created the instance of this object. */
    private Activity mActivity;
    /** Instance of handler for callback on user action. */
    private SeriesEventHandler mEventHandler;
    /** The {@link RecyclerView} this adapter is attached to. */
    private RecyclerView mRecyclerView;

    /** List of series which will be displayed. */
    private final List<Series> mListSeries;

    /** Cached drawable for edit icon. */
    private Drawable mEditDrawable;
    /** The last color set as the edit drawable filter. */
    private int mEditDrawableFilter = -1;

    /** Indicates minimum score values which will be highlighted when displayed. */
    private int mMinimumScoreToHighlight = Constants.DEFAULT_GAME_HIGHLIGHT;
    /** Indicates minimum series total values which will be highlighted when displayed. */
    private int mMinimumSeriesToHighlight = Constants.DEFAULT_SERIES_HIGHLIGHT;

    /**
     * Subclass of RecyclerView.ViewHolder to manage view which will display text to the user.
     */
    public static class SeriesViewHolder
            extends RecyclerView.ViewHolder {

        /** Displays date of the series. */
        private TextView mTextViewDate;
        /** Each TextView displays a different score in the series. */
        private TextView[] mArrayTextViewGames;
        /** Each TextView displays a different match play result in the series. */
        private TextView[] mArrayTextViewMatchPlay;
        /** Displays an icon to allow editing of the date of a series. */
        private ImageView mImageViewEdit;
        /** Displays the sum of all the scores in the series. */
        private TextView mTextViewSeriesTotal;

        /** Displays text to confirm deletion of item. */
        private TextView mTextViewDelete;
        /** Displays icon to confirm deletion of item. */
        private ImageView mImageViewDelete;
        /** Displays text to undo deletion of item. */
        private TextView mTextViewUndo;

        /**
         * Calls super constructor and gets instances of ImageView and TextView objects for member variables from
         * itemLayoutView.
         *
         * @param itemLayoutView layout view containing views to display data
         * @param viewType type of view
         */
        @SuppressWarnings("CheckStyle")
        public SeriesViewHolder(View itemLayoutView, int viewType) {
            super(itemLayoutView);
            switch (viewType) {
                case VIEWTYPE_ACTIVE:
                    mTextViewDate = (TextView) itemLayoutView.findViewById(R.id.tv_series_date);
                    mImageViewEdit = (ImageView) itemLayoutView.findViewById(R.id.iv_edit_date);
                    mTextViewSeriesTotal = (TextView) itemLayoutView.findViewById(R.id.tv_series_total);

                    //Adds text views by id to array
                    mArrayTextViewGames = new TextView[Constants.MAX_NUMBER_LEAGUE_GAMES];
                    mArrayTextViewGames[0] = (TextView) itemLayoutView.findViewById(R.id.tv_series_game_1);
                    mArrayTextViewGames[1] = (TextView) itemLayoutView.findViewById(R.id.tv_series_game_2);
                    mArrayTextViewGames[2] = (TextView) itemLayoutView.findViewById(R.id.tv_series_game_3);
                    mArrayTextViewGames[3] = (TextView) itemLayoutView.findViewById(R.id.tv_series_game_4);
                    mArrayTextViewGames[4] = (TextView) itemLayoutView.findViewById(R.id.tv_series_game_5);

                    mArrayTextViewMatchPlay = new TextView[Constants.MAX_NUMBER_LEAGUE_GAMES];
                    mArrayTextViewMatchPlay[0] = (TextView) itemLayoutView.findViewById(R.id.tv_series_match_play_1);
                    mArrayTextViewMatchPlay[1] = (TextView) itemLayoutView.findViewById(R.id.tv_series_match_play_2);
                    mArrayTextViewMatchPlay[2] = (TextView) itemLayoutView.findViewById(R.id.tv_series_match_play_3);
                    mArrayTextViewMatchPlay[3] = (TextView) itemLayoutView.findViewById(R.id.tv_series_match_play_4);
                    mArrayTextViewMatchPlay[4] = (TextView) itemLayoutView.findViewById(R.id.tv_series_match_play_5);

                    break;
                case VIEWTYPE_DELETED:
                    mImageViewDelete = (ImageView) itemLayoutView.findViewById(R.id.iv_delete);
                    mTextViewDelete = (TextView) itemLayoutView.findViewById(R.id.tv_delete);
                    mTextViewUndo = (TextView) itemLayoutView.findViewById(R.id.tv_undo_delete);
                    break;
                default:
                    throw new IllegalArgumentException("invalid view type: " + viewType);
            }

        }
    }

    /**
     * Sets member variables to parameters.
     *
     * @param activity activity which created this instance
     * @param eventHandler handles on click/long click events on views
     * @param listSeries list of series to be displayed in RecyclerView
     */
    public SeriesAdapter(
            Activity activity,
            SeriesEventHandler eventHandler,
            List<Series> listSeries) {
        this.mActivity = activity;
        this.mEventHandler = eventHandler;
        this.mListSeries = listSeries;
    }

    @Override
    public SeriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case VIEWTYPE_ACTIVE:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_series, parent, false);
                break;
            case VIEWTYPE_DELETED:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_deleted, parent, false);
                break;
            default:
                throw new IllegalArgumentException("invalid view type: " + viewType);
        }
        return new SeriesViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(final SeriesViewHolder holder, final int position) {
        final int viewType = getItemViewType(position);

        switch (viewType) {
            case VIEWTYPE_ACTIVE:
                bindActiveSeriesItem(holder, position);
                break;
            case VIEWTYPE_DELETED:
                bindDeletedSeriesItem(holder, position);
                break;
            default:
                throw new IllegalArgumentException("Invalid view type: " + viewType);
        }
    }

    /**
     * Sets properties of a view holder for a deleted series item.
     *
     * @param holder view holder for deleted item
     * @param position position of deleted item
     */
    private void bindDeletedSeriesItem(SeriesViewHolder holder, int position) {
        final String nameToDelete = mListSeries.get(position).getSeriesDate();
        final long idToDelete = mListSeries.get(position).getSeriesId();
        final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEventHandler != null) {
                    if (v.getId() == R.id.tv_undo_delete)
                        mEventHandler.onSItemUndoDelete(idToDelete);
                    else
                        mEventHandler.onSItemDelete(idToDelete);
                }
            }
        };
        holder.itemView.setOnClickListener(null);
        holder.itemView.setBackgroundColor(Theme.getTertiaryThemeColor());
        holder.mTextViewDelete.setText("Click to delete " + nameToDelete);
        holder.mTextViewDelete.setOnClickListener(onClickListener);
        holder.mTextViewUndo.setOnClickListener(onClickListener);
        holder.mImageViewDelete.setOnClickListener(onClickListener);
    }

    /**
     * Sets properties of a view holder for an active series item.
     *
     * @param holder view holder for active item
     * @param position position of active item
     */
    private void bindActiveSeriesItem(final SeriesViewHolder holder, int position) {
        holder.mTextViewDate.setText(mListSeries.get(position).getSeriesDate());
        int matchPlayPadding = (int) mRecyclerView.getResources().getDimension(R.dimen.match_play_text_padding);
        int matchPlayHalfPadding = (int) mRecyclerView.getResources().getDimension(R.dimen.match_play_text_padding_2);

        List<Short> games = mListSeries.get(position).getSeriesGames();
        List<Byte> matchPlayResults = mListSeries.get(position).getSeriesMatchPlayResults();
        boolean hasMatchPlayResults = PreferenceManager.getDefaultSharedPreferences(holder.itemView.getContext())
                .getBoolean(Constants.KEY_SHOW_MATCH_RESULTS, true);
        if (hasMatchPlayResults) {
            hasMatchPlayResults = false;
            for (Byte b : matchPlayResults) {
                if (b != Constants.MATCH_PLAY_NONE) {
                    hasMatchPlayResults = true;
                    break;
                }
            }
        }

        short seriesTotal = mListSeries.get(position).getSeriesTotal();
        setSeriesTotalText(holder.mTextViewSeriesTotal, seriesTotal);

        final int numberOfGamesInSeries = games.size();
        for (int i = 0; i < numberOfGamesInSeries; i++) {
            // Highlights a score if it is over 300 or applies default theme if not
            short gameScore = games.get(-i + (numberOfGamesInSeries - 1));
            setGameScoreText(holder.mArrayTextViewGames[i], gameScore);

            if (hasMatchPlayResults) {
                byte matchPlay = matchPlayResults.get(-i + (numberOfGamesInSeries - 1));
                holder.mArrayTextViewGames[i].setPadding(matchPlayPadding,
                        matchPlayPadding,
                        matchPlayPadding,
                        matchPlayHalfPadding);
                holder.mArrayTextViewMatchPlay[i].setVisibility(View.VISIBLE);
                holder.mArrayTextViewMatchPlay[i].setText(MATCH_PLAY_INDICATORS[matchPlay]);
                colorMatchPlayText(holder.mArrayTextViewMatchPlay[i], matchPlay);
            } else {
                holder.mArrayTextViewGames[i].setPadding(matchPlayPadding,
                        matchPlayPadding,
                        matchPlayPadding,
                        matchPlayPadding);
                holder.mArrayTextViewMatchPlay[i].setVisibility(View.GONE);
            }
        }

        for (int i = numberOfGamesInSeries; i < Constants.MAX_NUMBER_LEAGUE_GAMES; i++) {
            holder.mArrayTextViewGames[i].setText(null);
            holder.mArrayTextViewMatchPlay[i].setText(null);
            holder.mArrayTextViewMatchPlay[i].setVisibility(View.GONE);
        }

        //Sets color of edit button
        if (mEditDrawable == null)
            mEditDrawable = DisplayUtils.getDrawable(holder.itemView.getResources(), R.drawable.ic_edit_black_24dp);
        if (mEditDrawableFilter != Theme.getSecondaryThemeColor()) {
            mEditDrawableFilter = Theme.getSecondaryThemeColor();
            mEditDrawable.setColorFilter(mEditDrawableFilter, PorterDuff.Mode.SRC_IN);
        }
        holder.mImageViewEdit.setImageDrawable(mEditDrawable);

        holder.mImageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEventHandler.onEditClick(mRecyclerView.getChildAdapterPosition(holder.itemView));
            }
        });

        holder.itemView.setOnClickListener(this);
    }

    /**
     * Sets the color of the textview based on the match play results.
     *
     * @param textViewMatchPlay text view to color
     * @param matchPlay match play results
     */
    private void colorMatchPlayText(TextView textViewMatchPlay, byte matchPlay) {
        if (!PreferenceManager.getDefaultSharedPreferences(textViewMatchPlay.getContext())
                .getBoolean(Constants.KEY_HIGHLIGHT_MATCH_RESULTS, true)) {
            matchPlay = Constants.MATCH_PLAY_NONE;
        }

        switch (matchPlay) {
            case Constants.MATCH_PLAY_WON:
                textViewMatchPlay.setTextColor(DisplayUtils.getColorResource(textViewMatchPlay.getResources(),
                        R.color.theme_green_tertiary));
                textViewMatchPlay.setAlpha(1f);
                break;
            case Constants.MATCH_PLAY_LOST:
                textViewMatchPlay.setTextColor(DisplayUtils.getColorResource(textViewMatchPlay.getResources(),
                        R.color.theme_red_tertiary));
                textViewMatchPlay.setAlpha(1f);
                break;
            default:
                textViewMatchPlay.setTextColor(DisplayUtils.COLOR_BLACK);
                textViewMatchPlay.setAlpha(DisplayUtils.BLACK_SECONDARY_TEXT_ALPHA);
                break;
        }
    }

    /**
     * Sets the text view text to the series total of the game and highlights it accordingly.
     *
     * @param textViewTotal text view to set and highlight
     * @param seriesTotal total of the series
     */
    private void setSeriesTotalText(TextView textViewTotal, short seriesTotal) {
        textViewTotal.setText(Short.toString(seriesTotal));
        if (seriesTotal >= mMinimumSeriesToHighlight) {
            textViewTotal.setBackgroundColor(Theme.getStatusThemeColor());
            textViewTotal.setTextColor(DisplayUtils.COLOR_WHITE);
            textViewTotal.setAlpha(1f);
        } else {
            textViewTotal.setBackgroundColor(0x00000000);
            textViewTotal.setTextColor(DisplayUtils.COLOR_BLACK);
            textViewTotal.setAlpha(DisplayUtils.BLACK_SECONDARY_TEXT_ALPHA);
        }
    }

    /**
     * Sets the text view text to the score of the game and highlights it accordingly.
     *
     * @param textViewGame text view to set and highlight
     * @param gameScore score of the game
     */
    private void setGameScoreText(TextView textViewGame, short gameScore) {
        textViewGame.setText(Short.toString(gameScore));
        if (gameScore >= mMinimumScoreToHighlight) {
            textViewGame.setTextColor(Theme.getTertiaryThemeColor());
            textViewGame.setAlpha(1f);
        } else {
            textViewGame.setTextColor(DisplayUtils.COLOR_BLACK);
            textViewGame.setAlpha(DisplayUtils.BLACK_SECONDARY_TEXT_ALPHA);
        }
    }

    @Override
    public void onClick(View v) {
        //Calls relevant event handler method
        if (mEventHandler != null && mRecyclerView != null)
            mEventHandler.onSItemClick(mRecyclerView.getChildAdapterPosition(v));
    }

    @Override
    public int getItemCount() {
        return mListSeries.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (mListSeries.get(position).wasDeleted())
                ? VIEWTYPE_DELETED
                : VIEWTYPE_ACTIVE;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        // Releasing references
        mActivity = null;
        mEventHandler = null;
        mRecyclerView = null;
        mEditDrawable = null;
    }

    @Override
    public void updateTheme() {
        if (mActivity != null) {
            Log.d(TAG, PreferenceManager.getDefaultSharedPreferences(mActivity)
                    .getString(Constants.KEY_HIGHLIGHT_SCORE, "-1"));
            mMinimumScoreToHighlight = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(mActivity)
                    .getString(Constants.KEY_HIGHLIGHT_SCORE, "-1"));
            if (mMinimumScoreToHighlight == -1)
                mMinimumScoreToHighlight = Constants.DEFAULT_GAME_HIGHLIGHT;
            else
                mMinimumScoreToHighlight = mMinimumScoreToHighlight * Constants.HIGHLIGHT_INCREMENT;

            Log.d(TAG, "" + PreferenceManager.getDefaultSharedPreferences(mActivity)
                    .getInt(Constants.KEY_HIGHLIGHT_SERIES, -1));
            mMinimumSeriesToHighlight = PreferenceManager.getDefaultSharedPreferences(mActivity)
                    .getInt(Constants.KEY_HIGHLIGHT_SERIES, -1);
            if (mMinimumSeriesToHighlight == -1)
                mMinimumSeriesToHighlight = Constants.DEFAULT_SERIES_HIGHLIGHT;
            else
                mMinimumSeriesToHighlight = mMinimumSeriesToHighlight * Constants.HIGHLIGHT_INCREMENT;
        }
        notifyDataSetChanged();
    }

    /**
     * Provides methods to implement functionality when items in the RecyclerView are interacted with.
     */
    public interface SeriesEventHandler {

        /**
         * Called when an item in the RecyclerView is clicked.
         *
         * @param position position of the item in the list
         */
        void onSItemClick(final int position);

        /**
         * Called when an item in the RecyclerView is confirmed by user for deletion.
         *
         * @param id id of the deleted item
         */
        void onSItemDelete(long id);

        /**
         * Called when the user undoes a delete on an item in the RecyclerView.
         *
         * @param id id of the undeleted item
         */
        void onSItemUndoDelete(long id);

        /**
         * Called when the edit image view for an item in the RecyclerView is clicked.
         *
         * @param position position of the item in the list
         */
        void onEditClick(final int position);
    }
}
