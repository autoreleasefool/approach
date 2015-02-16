package ca.josephroque.bowlingcompanion.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ca.josephroque.bowlingcompanion.R;

/**
 * Created by josephroque on 15-02-15.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.adapter
 * in project Bowling Companion
 */
public class BowlerListAdapter extends ArrayAdapter<Long>
{
    /** The Activity used to create this object */
    private final Activity context;
    /** A list of bowler names */
    private List<String> bowlerNames;
    /** A list of averages which is assumed to be in an order relative to bowlerNames */
    private List<Integer> bowlerAverages;
    /** A list of ids which is assumed to be in an order relative to bowlerNames */
    private List<Long> bowlerIDs;

    /**
     * Stores the parameters in class variables
     * @param context activity used to create this object
     * @param bowlerNames a list of bowler names
     * @param bowlerAverages a list of bowler averages, in an order relative to bowlerNames
     * @param bowlerIDs a list of bowler IDs, in an order relative to bowlerNames
     */
    public BowlerListAdapter(Activity context, List<String> bowlerNames, List<Integer> bowlerAverages, List<Long> bowlerIDs)
    {
        super(context, R.layout.list_bowlers, bowlerIDs);
        this.context = context;

        update(bowlerNames, bowlerAverages, bowlerIDs);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_bowlers, null, true);

        ImageView imgBowlerOrTeam = (ImageView) rowView.findViewById(R.id.image_bowler_team_type);
        TextView txtBowlerName = (TextView) rowView.findViewById(R.id.text_bowler_name);
        TextView txtBowlerAverage = (TextView) rowView.findViewById(R.id.text_bowler_average);

        if (bowlerNames.size() > 0)
        {
            imgBowlerOrTeam.setImageResource(R.drawable.ic_person);
            txtBowlerName.setText(bowlerNames.get(position));
            txtBowlerAverage.setText(String.valueOf(bowlerAverages.get(position)));
        }

        return rowView;
    }

    /**
     * Updates the class values to the parameters
     *
     * @param bowlerNamesList a list of bowler names
     * @param bowlerAveragesList a list of bowler averages
     * @param bowlerIDsList a list of bowler IDs
     */
    public void update(List<String> bowlerNamesList, List<Integer> bowlerAveragesList, List<Long> bowlerIDsList)
    {
        bowlerNames = bowlerNamesList;
        bowlerAverages = bowlerAveragesList;
        bowlerIDs = bowlerIDsList;
    }
}
