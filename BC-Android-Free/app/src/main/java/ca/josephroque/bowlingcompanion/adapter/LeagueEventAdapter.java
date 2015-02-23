package ca.josephroque.bowlingcompanion.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    public static class LeagueEventViewHolder extends RecyclerView.ViewHolder
    {
        private TextView mTextViewLeagueEventName;
        private TextView mTextViewLeagueEventAverage;

        private LeagueEventViewHolder(View itemLayoutView)
        {
            super(itemLayoutView);
            mTextViewLeagueEventName = (TextView)
                    itemLayoutView.findViewById(R.id.textView_league_event_name);
            mTextViewLeagueEventAverage = (TextView)
                    itemLayoutView.findViewById(R.id.textView_league_event_average);
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
    }

    @Override
    public LeagueEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_leagues_events, parent, false);
        return new LeagueEventViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final LeagueEventViewHolder holder, final int position)
    {
        holder.mTextViewLeagueEventName.setText(mListLeagueEventNames.get(position));
        holder.mTextViewLeagueEventAverage.setText("Avg: "
                + String.valueOf(mListLeagueEventAverages.get(position)));
        holder.itemView.setBackgroundColor(
                mActivity.getResources().getColor(R.color.secondary_background));

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new OpenLeagueEventSeriesTask().execute(position);
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

    private class OpenLeagueEventSeriesTask extends AsyncTask<Integer, Void, Object[]>
    {
        @Override
        protected Object[] doInBackground(Integer... position)
        {
            SharedPreferences preferences =
                    mActivity.getSharedPreferences(Constants.PREFERENCES, Activity.MODE_PRIVATE);
            long bowlerId = preferences.getLong(Constants.PREFERENCE_ID_BOWLER, -1);
            SharedPreferences.Editor preferencesEditor = preferences.edit();
            long selectedLeagueId = mListLeagueEventIds.get(position[0]);
            String selectedLeagueName = mListLeagueEventNames.get(position[0]);

            SQLiteDatabase database = DatabaseHelper.getInstance(mActivity).getWritableDatabase();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = dateFormat.format(new Date());

            if (!selectedLeagueName.equals(Constants.NAME_LEAGUE_OPEN))
            {
                //Updates the date modified in the database of the selected league
                ContentValues values = new ContentValues();
                values.put(LeagueEntry.COLUMN_NAME_DATE_MODIFIED, currentDate);

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
            }

            preferencesEditor
                    .putString(Constants.PREFERENCE_NAME_LEAGUE, selectedLeagueName)
                    .putLong(Constants.PREFERENCE_ID_LEAGUE, selectedLeagueId);

            if (mEventMode)
            {
                String rawSeriesQuery = "SELECT "
                        + LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES + ", "
                        + SeriesEntry.TABLE_NAME + "." + SeriesEntry._ID + " AS sid"
                        + " FROM " + LeagueEntry.TABLE_NAME + " AS league"
                        + " LEFT JOIN " + SeriesEntry.TABLE_NAME
                        + " ON league." + LeagueEntry._ID + "=" + SeriesEntry.COLUMN_NAME_LEAGUE_ID
                        + " WHERE league." + LeagueEntry._ID + "=?";
                String[] rawSeriesArgs = {String.valueOf(selectedLeagueId)};

                Cursor cursor = database.rawQuery(rawSeriesQuery, rawSeriesArgs);
                cursor.moveToFirst();
                byte numberOfGames = (byte)cursor.getInt(cursor.getColumnIndex(LeagueEntry.COLUMN_NAME_NUMBER_OF_GAMES));
                long seriesId = cursor.getLong(cursor.getColumnIndex("sid"));

                preferencesEditor.apply();
                return new Object[]{seriesId, numberOfGames};
            }
            else
            {
                preferencesEditor.putLong(Constants.PREFERENCE_ID_RECENT_LEAGUE, selectedLeagueId)
                        .putLong(Constants.PREFERENCE_ID_RECENT_BOWLER, bowlerId)
                        .apply();
                return new Object[]{mListLeagueEventNumberOfGames.get(position[0])};
            }
        }

        @Override
        protected void onPostExecute(Object[] params)
        {
            if (mEventMode)
            {
                long seriesId = (Long)params[0];
                byte numberOfGames = (Byte)params[1];
                SeriesActivity.openEventSeries(mActivity, seriesId, numberOfGames);
            }
            else
            {
                byte numberOfGames = (Byte)params[0];
                Intent seriesIntent = new Intent(mActivity, SeriesActivity.class);
                seriesIntent.putExtra(
                        Constants.EXTRA_NUMBER_OF_GAMES, numberOfGames);
                mActivity.startActivity(seriesIntent);
            }
        }
    }
}
