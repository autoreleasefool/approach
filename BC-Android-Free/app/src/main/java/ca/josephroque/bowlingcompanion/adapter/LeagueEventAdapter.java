package ca.josephroque.bowlingcompanion.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;
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

    public static class LeagueEventViewHolder extends RecyclerView.ViewHolder
    {
        private TextView mTextViewLeagueEventName;
        private TextView mTextViewLeagueEventAverage;
        private TextView mTextViewLeagueEventNumberOfGames;

        private LeagueEventViewHolder(View itemLayoutView)
        {
            super(itemLayoutView);
            mTextViewLeagueEventName = (TextView)
                    itemLayoutView.findViewById(R.id.textView_league_event_name);
            mTextViewLeagueEventAverage = (TextView)
                    itemLayoutView.findViewById(R.id.textView_league_event_average);
            mTextViewLeagueEventNumberOfGames = (TextView)
                    itemLayoutView.findViewById(R.id.textView_league_event_games);
        }
    }

    public LeagueEventAdapter(
            Activity activity,
            List<Long> listLeagueEventIds,
            List<String> listLeagueEventNames,
            List<Short> listLeagueEventAverages,
            List<Byte> listLeagueEventNumberOfGames)
    {
        this.mActivity = activity;
        this.mListLeagueEventIds = listLeagueEventIds;
        this.mListLeagueEventNames = listLeagueEventNames;
        this.mListLeagueEventAverages = listLeagueEventAverages;
        this.mListLeagueEventNumberOfGames = listLeagueEventNumberOfGames;
    }

    @Override
    public LeagueEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_leagues_events, parent, false);
        return new LeagueEventViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(LeagueEventViewHolder holder, final int position)
    {
        holder.mTextViewLeagueEventName.setText(mListLeagueEventNames.get(position));
        holder.mTextViewLeagueEventAverage.setText(String.valueOf(mListLeagueEventAverages.get(position)));
        holder.mTextViewLeagueEventNumberOfGames.setText(String.valueOf(mListLeagueEventNumberOfGames.get(position)));
        holder.itemView.setBackgroundColor(
                mActivity.getResources().getColor(R.color.secondary_background));

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO: openLeagueSeriesTask().execute(position)
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
}
