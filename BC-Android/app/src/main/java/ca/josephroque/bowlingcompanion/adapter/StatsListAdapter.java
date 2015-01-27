package ca.josephroque.bowlingcompanion.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ca.josephroque.bowlingcompanion.R;

/**
 * Created by josephroque on 15-01-26.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.adapter
 * in project Bowling Companion
 */
public class StatsListAdapter extends ArrayAdapter<String>
{

    /** The Activity used to create this object */
    private Activity context;
    /** List of names of the stats to be displayed */
    private List<String> statNamesList;
    /** List of values to match the stats in statNamesList */
    private List<String> statValuesList;

    /**
     * Stores the parameters in class variables
     *
     * @param context Activity used to create this object
     * @param statNames list of stat names
     * @param statValues list of values, relative to the order of statNames
     */
    public StatsListAdapter(Activity context, List<String> statNames, List<String> statValues)
    {
        super(context, R.layout.list_stats, statNames);
        this.context = context;
        this.statNamesList = statNames;
        this.statValuesList = statValues;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_stats, null, true);
        ((TextView)rowView.findViewById(R.id.text_stat_name)).setText(statNamesList.get(position) + ": ");
        ((TextView)rowView.findViewById(R.id.text_stat_value)).setText(statValuesList.get(position));
        return rowView;
    }
}
