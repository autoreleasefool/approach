package ca.josephroque.bowlingcompanion.adapter;

import android.app.Activity;
import android.content.Context;
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
 * Location ca.josephroque.bowlingcompanion
 * in project Bowling Companion
 */
public class SeriesListAdapter extends ArrayAdapter<Long>
{

    private final Activity context;
    private String[] seriesDates;
    private List<List<Integer>> seriesGameScores;

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

        StringBuilder seriesScoresBuilder = new StringBuilder("Scores:");
        for (Iterator<Integer> it = seriesGameScores.get(position).iterator(); it.hasNext(); )
        {
            seriesScoresBuilder.append(" " + String.valueOf(it.next()));
        }
        txtScores.setText(seriesScoresBuilder.toString());

        return rowView;
    }
}
