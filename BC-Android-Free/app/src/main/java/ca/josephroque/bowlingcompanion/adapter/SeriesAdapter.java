package ca.josephroque.bowlingcompanion.adapter;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.SeriesActivity;
import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;
import ca.josephroque.bowlingcompanion.theme.ChangeableTheme;
import ca.josephroque.bowlingcompanion.theme.Theme;

/**
 * Created by josephroque on 15-02-22.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.adapter
 * in project Bowling Companion
 */
public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder>
    implements ChangeableTheme
{
    /** Tag to identify class when outputting to console */
    private static final String TAG = "SeriesAdapter";

    /** Instance of activity which created instance of this object */
    private SeriesActivity mActivity;
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
        private ValueAnimator mValueAnimator = null;

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
            SeriesActivity context,
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
    public void onBindViewHolder(final SeriesViewHolder holder, final int position)
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
                holder.mListTextViewSeriesGames.get(i).setTextColor(Theme.getGameScoreHighlightThemeColor());
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
                if (mActivity.getChangeDateOptionsVisible())
                {
                    mActivity.setChangeDateOptionsVisible(false);
                    mActivity.changeDateOfSeries(position);
                }
                else
                {
                    mActivity.setChangeDateOptionsVisible(false);
                    mActivity.openSeries(position);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if (mActivity.getChangeDateOptionsVisible())
                    return true;
                mActivity.setChangeDateOptionsVisible(false);
                showDeleteSeriesDialog(position);
                return true;
            }
        });
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
                else if (event.getActionMasked() == MotionEvent.ACTION_UP && holder.mValueAnimator != null)
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
    public int getItemCount()
    {
        return mListSeriesIds.size();
    }

    @Override
    public void updateTheme()
    {
        notifyDataSetChanged();
    }

    private void showDeleteSeriesDialog(final int position)
    {
        final String seriesDate = mListSeriesDate.get(position);
        final long seriesId = mListSeriesIds.get(position);

        DatabaseHelper.deleteData(mActivity,
                new DatabaseHelper.DataDeleter()
                {
                    @Override
                    public void execute()
                    {
                        deleteSeries(seriesId);
                    }
                },
                seriesDate);
    }

    private void deleteSeries(final long selectedSeriesID)
    {
        final int index = mListSeriesIds.indexOf(selectedSeriesID);
        final String seriesDate = mListSeriesDate.remove(index);
        mListSeriesGames.remove(index);
        mListSeriesIds.remove(index);
        notifyDataSetChanged();

        if (mListSeriesIds.size() == 0)
        {
            SeriesActivity seriesActivity = (SeriesActivity)mActivity;
            seriesActivity.showNewSeriesInstructions();
        }

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                String[] whereArgs = {String.valueOf(selectedSeriesID)};
                SQLiteDatabase database = DatabaseHelper.getInstance(mActivity).getWritableDatabase();

                //Finds all ids of games belonging to the series, adds them to a list
                List<Long> gameIdList = new ArrayList<>();
                Cursor cursor = null;
                try
                {
                    cursor = database.query(GameEntry.TABLE_NAME,
                            new String[]{GameEntry._ID},
                            GameEntry.COLUMN_NAME_SERIES_ID + "=?",
                            whereArgs,
                            null,
                            null,
                            null);
                    if (cursor.moveToFirst())
                    {
                        while (!cursor.isAfterLast())
                        {
                            gameIdList.add(cursor.getLong(cursor.getColumnIndex(GameEntry._ID)));
                            cursor.moveToNext();
                        }
                    }
                }
                finally
                {
                    if (cursor != null && !cursor.isClosed())
                        cursor.close();
                }

                //Deletes all rows in frame table associated to game IDs found above,
                //along with rows in games and series table associated to series ID
                database.beginTransaction();
                try
                {
                    for (int i = 0; i < gameIdList.size(); i++)
                    {
                        database.delete(FrameEntry.TABLE_NAME,
                                FrameEntry.COLUMN_NAME_GAME_ID + "=?",
                                new String[]{String.valueOf(gameIdList.get(i))});
                    }
                    database.delete(GameEntry.TABLE_NAME,
                            GameEntry.COLUMN_NAME_SERIES_ID + "=?",
                            whereArgs);
                    database.delete(SeriesEntry.TABLE_NAME,
                            SeriesEntry._ID + "=?",
                            whereArgs);
                    database.setTransactionSuccessful();
                }
                catch (Exception e)
                {
                    Log.w(TAG, "Error deleting series: " + seriesDate);
                }
                finally
                {
                    database.endTransaction();
                }
            }
        }).start();
    }
}
