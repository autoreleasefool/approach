package ca.josephroque.bowlingcompanion.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

import ca.josephroque.bowlingcompanion.R;

/**
 * Created by josephroque on 15-01-12.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.adapter
 * in project Bowling Companion
 */
public class SeriesListAdapter extends ArrayAdapter<Long>
{

    /** The activity used to create this object */
    private final Activity context;
    /** An array of dates formatted as String objects */
    private String[] seriesDates;
    /** List of game scores in each series, in the same order relative to seriesDates */
    private List<List<Integer>> seriesGameScores;

    /**
     * Stores the parameters in class variables
     *
     * @param context activity used to create this object
     * @param seriesIDList a list of series IDs
     * @param seriesDatesList a list of series dates, in an order relative to seriesIDList
     * @param seriesGameScoresList a list of game scores in a series, in an order relative to seriesIDList
     */
    public SeriesListAdapter(Activity context, List<Long> seriesIDList, List<String> seriesDatesList, List<List<Integer>> seriesGameScoresList)
    {
        super(context, R.layout.list_series, seriesIDList);
        this.context = context;

        seriesGameScores = seriesGameScoresList;
        seriesDates = new String[seriesDatesList.size()];
        seriesDatesList.toArray(seriesDates);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_series, null, true);
        TextView txtSeriesDate = (TextView) rowView.findViewById(R.id.text_series_date);
        TextView txtScores = (TextView) rowView.findViewById(R.id.text_series_scores);

        txtSeriesDate.setText(seriesDates[position]);

        StringBuilder seriesScoresBuilder = new StringBuilder(" Scores:");
        for (Iterator<Integer> it = seriesGameScores.get(position).iterator(); it.hasNext(); )
        {
            seriesScoresBuilder.append(" " + String.valueOf(it.next()));
        }
        txtScores.setText(seriesScoresBuilder.toString());

        return rowView;
    }

    /**
     * Updates the class values to the parameters
     *
     * @param seriesDatesList a list of series dates, in an order relative to seriesIDList
     * @param seriesGameScoresList a list of game scores in a series, in an order relative to seriesIDList
     */
    public void update(List<String> seriesDatesList, List<List<Integer>> seriesGameScoresList)
    {
        seriesGameScores = seriesGameScoresList;
        seriesDates = new String[seriesDatesList.size()];
        seriesDatesList.toArray(seriesDates);
    }
}
