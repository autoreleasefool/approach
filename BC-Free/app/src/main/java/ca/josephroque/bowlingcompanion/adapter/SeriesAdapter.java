package ca.josephroque.bowlingcompanion.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;
import java.util.List;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.theme.Theme;

/**
 * Created by josephroque on 15-03-17.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.adapter
 * in project Bowling Companion
 */
public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder>
    implements Theme.ChangeableTheme, View.OnClickListener, View.OnLongClickListener
{
    /** Tag to identify class when outputting to console */
    private static final String TAG = "SeriesAdapter";

    /** Instance of handler for callback on user action */
    private SeriesEventHandler mEventHandler;
    /** List of dates which will be displayed */
    private List<String> mListDates;
    /** List of games which will be displayed, in an order relative to mListDates */
    private List<List<Short>> mListGames;

    public static class SeriesViewHolder extends RecyclerView.ViewHolder
    {
        /** Displays date of the series */
        private TextView mTextViewDate;
        /** Each TextView displays a different score in the series */
        private List<TextView> mListTextViewGames;
        private ValueAnimator mValueAnimator = null;

        public SeriesViewHolder(View itemLayoutView)
        {
            super(itemLayoutView);
            mTextViewDate = (TextView)itemLayoutView.findViewById(R.id.tv_series_date);
            mListTextViewGames = new ArrayList<>();
            for (byte i = 0; i < Constants.MAX_NUMBER_LEAGUE_GAMES; i++)
            {
                switch(i)
                {
                    case 0:
                        mListTextViewGames.add((TextView)itemLayoutView.findViewById(R.id.tv_series_game_1));
                        break;
                    case 1:
                        mListTextViewGames.add((TextView)itemLayoutView.findViewById(R.id.tv_series_game_2));
                        break;
                    case 2:
                        mListTextViewGames.add((TextView)itemLayoutView.findViewById(R.id.tv_series_game_3));
                        break;
                    case 3:
                        mListTextViewGames.add((TextView)itemLayoutView.findViewById(R.id.tv_series_game_4));
                        break;
                    case 4:
                        mListTextViewGames.add((TextView)itemLayoutView.findViewById(R.id.tv_series_game_5));
                        break;
                }
            }
        }
    }

    public SeriesAdapter(
            SeriesEventHandler eventHandler,
            List<String> listDates,
            List<List<Short>> listGames)
    {
        this.mEventHandler = eventHandler;
        this.mListDates = listDates;
        this.mListGames = listGames;
    }

    @Override
    public SeriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_series, parent, false);
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
            holder.mListTextViewGames.get(i).setText(String.valueOf(gameScore));
            if (gameScore >= 300)
            {
                holder.mListTextViewGames.get(i).setTextColor(Theme.getPrimaryThemeColor());
            }
        }

        /*
         * Below methods are executed when an item in the RecyclerView is
         * clicked or long clicked.
         */
        holder.itemView.setOnClickListener(this);
        holder.itemView.setOnLongClickListener(this);
        holder.itemView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(final View v, MotionEvent event)
            {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
                {
                    holder.mValueAnimator =
                            ValueAnimator.ofObject(new ArgbEvaluator(),
                                    Theme.getListItemBackground(),
                                    Theme.getLongPressThemeColor());
                    holder.mValueAnimator.setDuration(Theme.getMediumAnimationDuration());
                    holder.mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                    {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation)
                        {
                            v.setBackgroundColor((Integer)animation.getAnimatedValue());
                        }
                    });
                    holder.mValueAnimator.start();
                }
                else if ((event.getActionMasked() == MotionEvent.ACTION_UP || event.getActionMasked() == MotionEvent.ACTION_MOVE)
                        && holder.mValueAnimator != null)
                {
                    holder.mValueAnimator.cancel();
                    holder.mValueAnimator = null;
                    v.setBackgroundColor(Theme.getListItemBackground());
                }

                return false;
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        mEventHandler.onSItemClick(mEventHandler.getSeriesViewPositionInRecyclerView(v));
    }

    @Override
    public boolean onLongClick(View v)
    {
        mEventHandler.onSLongClick(mEventHandler.getSeriesViewPositionInRecyclerView(v));
        return true;
    }

    @Override
    public int getItemCount() {return mListDates.size();}

    @Override
    public void updateTheme() {notifyDataSetChanged();}

    /**
     * Provides methods to implement functionality when items
     * in the RecyclerView are interacted with
     */
    public static interface SeriesEventHandler
    {
        /**
         * Called when an item in the RecyclerView is clicked
         * @param position position of the item in the list
         */
        public void onSItemClick(final int position);

        /**
         * Called when an item in the RecyclerView is long clicked
         * @param position position of the item in the list
         */
        public void onSLongClick(final int position);

        /**
         * Should be used to return RecyclerView#getChildPosition(v) on the
         * recycler view which uses this adapter
         * @param v the view to get the position of
         * @return position of v in the parent RecyclerView
         */
        public int getSeriesViewPositionInRecyclerView(View v);
    }
}
