package ca.josephroque.bowlingcompanion.adapter;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.SeriesActivity;
import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;

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

    private List<Long> mListLeagueEventIds;
    private List<String> mListLeagueEventNames;
    private List<Short> mListLeagueEventAverages;
    private List<Byte> mListLeagueEventNumberOfGames;

    private boolean mEventMode;
    final private int mInitialSizeOfList;

    public static class LeagueEventViewHolder extends RecyclerView.ViewHolder
    {
        private TextView mTextViewLeagueEventName;
        private TextView mTextViewLeagueEventAverage;
        private TextView mTextViewLeagueEventNumberOfGames;

        private int mOriginalHeight = -1;
        private boolean mIsViewExpanded = false;

        private LeagueEventViewHolder(View itemLayoutView, Activity activity)
        {
            super(itemLayoutView);
            mTextViewLeagueEventName = (TextView)
                    itemLayoutView.findViewById(R.id.textView_league_event_name);
            mTextViewLeagueEventAverage = (TextView)
                    itemLayoutView.findViewById(R.id.textView_league_event_average);
            mTextViewLeagueEventNumberOfGames = (TextView)
                    itemLayoutView.findViewById(R.id.textView_league_event_games);

            if (!mIsViewExpanded)
            {
                mTextViewLeagueEventNumberOfGames.setVisibility(View.GONE);
                mTextViewLeagueEventNumberOfGames.setEnabled(false);
            }
        }
    }

    public LeagueEventAdapter(
            Activity activity,
            List<Long> listLeagueEventIds,
            List<String> listLeagueEventNames,
            List<Short> listLeagueEventAverages,
            List<Byte> listLeagueEventNumberOfGames,
            boolean eventMode)
    {
        this.mActivity = activity;
        this.mListLeagueEventIds = listLeagueEventIds;
        this.mListLeagueEventNames = listLeagueEventNames;
        this.mListLeagueEventAverages = listLeagueEventAverages;
        this.mListLeagueEventNumberOfGames = listLeagueEventNumberOfGames;
        this.mEventMode = eventMode;
        this.mInitialSizeOfList = mListLeagueEventIds.size();
    }

    @Override
    public LeagueEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_leagues_events, parent, false);
        return new LeagueEventViewHolder(itemLayoutView, mActivity);
    }

    @Override
    public void onBindViewHolder(final LeagueEventViewHolder holder, final int position)
    {
        holder.mTextViewLeagueEventName.setText(mListLeagueEventNames.get(position));
        holder.mTextViewLeagueEventAverage.setText("Avg: "
                + String.valueOf(mListLeagueEventAverages.get(position)));
        holder.mTextViewLeagueEventNumberOfGames.setText("Games per series: "
                + String.valueOf(mListLeagueEventNumberOfGames.get(position)));
        holder.mTextViewLeagueEventNumberOfGames.setVisibility(View.GONE);
        holder.itemView.setBackgroundColor(
                mActivity.getResources().getColor(R.color.secondary_background));

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                animateLeagueEventClicked(holder, v, position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                showDeleteLeagueOrEventDialog(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mListLeagueEventIds.size();
    }

    private void animateLeagueEventClicked(
            final LeagueEventViewHolder holder,
            final View viewClicked,
            final int position)
    {
        if (holder.mOriginalHeight == -1)
        {
            holder.mOriginalHeight = viewClicked.getHeight();
        }

        ValueAnimator valueAnimator;
        if (!holder.mIsViewExpanded)
        {
            holder.mTextViewLeagueEventNumberOfGames.setVisibility(View.VISIBLE);
            holder.mTextViewLeagueEventNumberOfGames.setEnabled(true);
            holder.mIsViewExpanded = true;
            valueAnimator = ValueAnimator.ofInt(
                    holder.mOriginalHeight,
                    holder.mOriginalHeight + holder.mTextViewLeagueEventAverage.getHeight());
        }
        else
        {
            holder.mIsViewExpanded = false;
            valueAnimator = ValueAnimator.ofInt(
                    holder.mOriginalHeight + holder.mTextViewLeagueEventAverage.getHeight(),
                    holder.mOriginalHeight);
            Animation animation = new AlphaAnimation(1f, 0f);
            animation.setDuration(200);
            animation.setAnimationListener(new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart(Animation animation)
                {

                }

                @Override
                public void onAnimationEnd(Animation animation)
                {
                    holder.mTextViewLeagueEventNumberOfGames.setVisibility(View.INVISIBLE);
                    holder.mTextViewLeagueEventNumberOfGames.setEnabled(false);
                }

                @Override
                public void onAnimationRepeat(Animation animation)
                {

                }
            });
            holder.mTextViewLeagueEventNumberOfGames.startAnimation(animation);
        }
        valueAnimator.setDuration(200);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                Integer value = (Integer)animation.getAnimatedValue();
                viewClicked.getLayoutParams().height = value.intValue();
                viewClicked.requestLayout();
            }
        });

        valueAnimator.start();
    }

    private void showDeleteLeagueOrEventDialog(final int position)
    {
        final String leagueName = mListLeagueEventNames.get(position);
        final long leagueID = mListLeagueEventIds.get(position);

        if (leagueName.equals(Constants.NAME_LEAGUE_OPEN))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage("The league \"" + leagueName + "\" cannot be deleted.")
                    .setCancelable(false)
                    .setPositiveButton(Constants.DIALOG_OKAY, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
            return;
        }

        DatabaseHelper.deleteData(mActivity,
                new DatabaseHelper.DataDeleter()
                {
                    @Override
                    public void execute()
                    {
                        deleteLeague(leagueID);
                    }
                },
                leagueName);
    }

    private void deleteLeague(final long selectedLeagueID)
    {
        final int indexOfId = mListLeagueEventIds.indexOf(selectedLeagueID);
        final String leagueName = mListLeagueEventNames.remove(indexOfId);
        mListLeagueEventAverages.remove(indexOfId);
        mListLeagueEventNumberOfGames.remove(indexOfId);
        mListLeagueEventIds.remove(indexOfId);
        notifyItemRemoved(indexOfId);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SQLiteDatabase database = DatabaseHelper.getInstance(mActivity).getWritableDatabase();
                String[] whereArgs = {String.valueOf(selectedLeagueID)};
                database.beginTransaction();
                try
                {
                    database.delete(FrameEntry.TABLE_NAME,
                            FrameEntry.COLUMN_NAME_LEAGUE_ID + "=?",
                            whereArgs);
                    database.delete(GameEntry.TABLE_NAME,
                            GameEntry.COLUMN_NAME_LEAGUE_ID + "=?",
                            whereArgs);
                    database.delete(SeriesEntry.TABLE_NAME,
                            SeriesEntry.COLUMN_NAME_LEAGUE_ID + "=?",
                            whereArgs);
                    database.delete(LeagueEntry.TABLE_NAME,
                            LeagueEntry._ID + "=?",
                            whereArgs);
                    database.setTransactionSuccessful();
                }
                catch (Exception e)
                {
                    Log.w(TAG, "Error deleting league: " + leagueName);
                }
                finally
                {
                    database.endTransaction();
                }
            }
        }).start();
    }

    private class OpenLeagueEventSeriesTask extends AsyncTask<Integer, Void, Integer>
    {
        @Override
        protected Integer doInBackground(Integer... position)
        {
            SharedPreferences preferences =
                    mActivity.getSharedPreferences(Constants.PREFERENCES, Activity.MODE_PRIVATE);
            long bowlerId = preferences.getLong(Constants.PREFERENCE_ID_BOWLER, -1);
            SharedPreferences.Editor preferencesEditor = preferences.edit();
            long selectedLeagueId = mListLeagueEventIds.get(position[0]);
            String selectedLeagueName = mListLeagueEventNames.get(position[0]);

            if (!selectedLeagueName.equals(Constants.NAME_LEAGUE_OPEN) && !mEventMode)
            {
                //Updates the date modified in the database of the selected league
                SQLiteDatabase database = DatabaseHelper.getInstance(mActivity).getWritableDatabase();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ContentValues values = new ContentValues();
                values.put(LeagueEntry.COLUMN_NAME_DATE_MODIFIED, dateFormat.format(new Date()));

                database.beginTransaction();
                try
                {
                    database.update(LeagueEntry.TABLE_NAME,
                            values,
                            LeagueEntry._ID + "=?",
                            new String[]{String.valueOf(selectedLeagueId)});
                    database.setTransactionSuccessful();
                }
                catch (Exception ex)
                {
                    Log.w(TAG, "Error updating league: " + ex.getMessage());
                }
                finally
                {
                    database.endTransaction();
                }

                preferencesEditor.putLong(Constants.PREFERENCE_ID_RECENT_LEAGUE, selectedLeagueId)
                        .putLong(Constants.PREFERENCE_ID_RECENT_BOWLER, bowlerId);
            }

            preferencesEditor
                    .putString(Constants.PREFERENCE_NAME_LEAGUE, selectedLeagueName)
                    .putLong(Constants.PREFERENCE_ID_LEAGUE, selectedLeagueId)
                    .apply();

            return position[0];
        }

        @Override
        protected void onPostExecute(Integer position)
        {
            Intent seriesIntent = new Intent(mActivity, SeriesActivity.class);
            seriesIntent.putExtra(Constants.EXTRA_EVENT_MODE, mEventMode);
            seriesIntent.putExtra(
                    Constants.EXTRA_NUMBER_OF_GAMES, mListLeagueEventNumberOfGames.get(position));
            mActivity.startActivity(seriesIntent);
        }
    }
}
