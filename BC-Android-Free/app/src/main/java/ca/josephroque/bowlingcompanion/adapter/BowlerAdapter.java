package ca.josephroque.bowlingcompanion.adapter;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import ca.josephroque.bowlingcompanion.database.Contract.*;
import ca.josephroque.bowlingcompanion.database.DatabaseHelper;

/**
 * Created by josephroque on 15-02-16.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.adapter
 * in project Bowling Companion
 */
public class BowlerAdapter extends RecyclerView.Adapter<BowlerAdapter.BowlerViewHolder>
{
    private static final String TAG = "BowlerAdapter";

    private Activity mActivity;
    private List<String> mBowlerNames;
    private List<Integer> mBowlerAverages;
    private List<Long> mBowlerIDs;

    public static class BowlerViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView imageViewBowlerOrTeam;
        public TextView textViewBowlerName;
        public TextView textViewBowlerAverage;

        public BowlerViewHolder(View mItemLayoutView)
        {
            super(mItemLayoutView);
            imageViewBowlerOrTeam = (ImageView)mItemLayoutView.findViewById(R.id.imageView_bowler_team);
            textViewBowlerName = (TextView)mItemLayoutView.findViewById(R.id.textView_bowler_name);
            textViewBowlerAverage = (TextView)mItemLayoutView.findViewById(R.id.textView_bowler_average);
        }
    }

    public BowlerAdapter(Activity context, List<Long> mBowlerIDs, List<String> mBowlerNames, List<Integer> mBowlerAverages)
    {
        mActivity = context;
        this.mBowlerIDs = mBowlerIDs;
        this.mBowlerNames = mBowlerNames;
        this.mBowlerAverages = mBowlerAverages;
    }

    @Override
    public BowlerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_bowlers, parent, false);

        return new BowlerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BowlerViewHolder holder, final int position)
    {
        holder.imageViewBowlerOrTeam.setImageResource(R.drawable.ic_person);
        holder.textViewBowlerName.setText(mBowlerNames.get(position));
        holder.textViewBowlerAverage.setText(String.valueOf(mBowlerAverages.get(position)));

        holder.itemView.setBackgroundColor(
                Color.parseColor(Constants.COLOR_BACKGROUND_SECONDARY));

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                showDeleteBowlerDialog(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mBowlerNames.size();
    }

    private void showDeleteBowlerDialog(final int position)
    {
        final String bowlerName = mBowlerNames.get(position);
        final long bowlerID = mBowlerIDs.get(position);

        DatabaseHelper.deleteData(mActivity,
                new DatabaseHelper.DataDeleter()
                {
                    @Override
                    public void execute()
                    {
                        deleteBowler(bowlerID);
                    }
                },
                false,
                bowlerName);
    }

    private void deleteBowler(final long selectedBowlerID)
    {
        final int index = mBowlerIDs.indexOf(selectedBowlerID);
        final String bowlerName = mBowlerNames.remove(index);
        mBowlerIDs.remove(index);
        notifyDataSetChanged();

        /*if (recentBowlerID == selectedBowlerID)
        {
            getSharedPreferences(Constants.MY_PREFS, MODE_PRIVATE).edit()
                    .putLong(Constants.PREFERENCES_ID_BOWLER_RECENT, -1)
                    .putLong(Constants.PREFERENCES_ID_LEAGUE_RECENT, -1)
                    .apply();
            recentBowlerID = -1;
            recentLeagueID = -1;

            quickGameButton.post(new Runnable()
            {
                @Override
                public void run()
                {
                    quickGameButton.setText(R.string.text_quick_game_button);
                }
            });
        }*/

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SQLiteDatabase database = DatabaseHelper.getInstance(mActivity).getWritableDatabase();
                String[] whereArgs = {String.valueOf(selectedBowlerID)};
                database.beginTransaction();
                try
                {
                    database.delete(FrameEntry.TABLE_NAME,
                            FrameEntry.COLUMN_NAME_BOWLER_ID + "=?",
                            whereArgs);
                    database.delete(GameEntry.TABLE_NAME,
                            GameEntry.COLUMN_NAME_BOWLER_ID + "=?",
                            whereArgs);
                    database.delete(SeriesEntry.TABLE_NAME,
                            SeriesEntry.COLUMN_NAME_BOWLER_ID + "=?",
                            whereArgs);
                    database.delete(LeagueEntry.TABLE_NAME,
                            LeagueEntry.COLUMN_NAME_BOWLER_ID + "=?",
                            whereArgs);
                    database.delete(BowlerEntry.TABLE_NAME,
                            BowlerEntry._ID + "=?",
                            whereArgs);
                    database.setTransactionSuccessful();
                }
                catch (Exception e)
                {
                    Log.w(TAG, "Error deleting bowler: " + bowlerName);
                }
                finally
                {
                    database.endTransaction();
                }
            }
        }).start();
    }
}
