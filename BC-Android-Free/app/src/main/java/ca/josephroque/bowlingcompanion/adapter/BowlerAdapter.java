package ca.josephroque.bowlingcompanion.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ca.josephroque.bowlingcompanion.R;

/**
 * Created by josephroque on 15-02-16.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.adapter
 * in project Bowling Companion
 */
public class BowlerAdapter extends RecyclerView.Adapter<BowlerAdapter.BowlerViewHolder>
{
    private List<String> mBowlerNames;
    private List<Integer> mBowlerAverages;
    private List<Long> mBowlerIDs;

    public static class BowlerViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView imageViewBowlerOrTeam;
        public TextView textViewBowlerName;
        public TextView textViewBowlerAverage;

        public BowlerViewHolder(View v)
        {
            super(v);
            imageViewBowlerOrTeam = (ImageView)v.findViewById(R.id.imageView_bowler_team);
            textViewBowlerName = (TextView)v.findViewById(R.id.textView_bowler_name);
            textViewBowlerAverage = (TextView)v.findViewById(R.id.textView_bowler_average);
        }
    }

    public BowlerAdapter(List<Long> mBowlerIDs, List<String> mBowlerNames, List<Integer> mBowlerAverages)
    {
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
    public void onBindViewHolder(BowlerViewHolder holder, int position)
    {
        holder.imageViewBowlerOrTeam.setImageResource(R.drawable.ic_person);
        holder.textViewBowlerName.setText(mBowlerNames.get(position));
        holder.textViewBowlerAverage.setText(String.valueOf(mBowlerAverages.get(position)));
    }

    @Override
    public int getItemCount()
    {
        return mBowlerNames.size();
    }
}
