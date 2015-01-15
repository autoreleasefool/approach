package ca.josephroque.bowlingcompanion.adapter;

import android.app.Activity;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.josephroque.bowlingcompanion.R;

/**
 * Created by josephroque on 15-01-10.
 */
public class LeagueAverageListAdapter extends ArrayAdapter<Long>
{

    private final Activity context;
    private String[] leagueNames;
    private Integer[] leagueAverages;
    private Integer[] leagueNumberOfGames;

    public LeagueAverageListAdapter(Activity context, List<Long> leagueIDs, List<String> leagueNamesList, List<Integer> leagueAveragesList, List<Integer> leagueNumberOfGamesList)
    {
        super(context, R.layout.list_league_average, leagueIDs);
        this.context = context;

        update(leagueNamesList, leagueAveragesList, leagueNumberOfGamesList);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_league_average, null, true);
        TextView txtLeague = (TextView) rowView.findViewById(R.id.text_league_name);
        TextView txtAverage = (TextView) rowView.findViewById(R.id.text_league_average);
        TextView txtNumberOfGames = (TextView) rowView.findViewById(R.id.text_league_number_of_games);

        if (leagueNames.length > 0)
        {
            txtLeague.setText(leagueNames[position]);
            txtAverage.setText(" Avg: " + String.valueOf(leagueAverages[position]));
            txtNumberOfGames.setText(" Games per week: " + String.valueOf(leagueNumberOfGames[position]));
        }

        return rowView;
    }

    public void update(List<String> leagueNamesList, List<Integer> leagueAveragesList, List<Integer> leagueNumberOfGamesList)
    {
        leagueNames = new String[leagueNamesList.size()];
        leagueNamesList.toArray(leagueNames);
        leagueAverages = new Integer[leagueAveragesList.size()];
        leagueAveragesList.toArray(leagueAverages);
        leagueNumberOfGames = new Integer[leagueNumberOfGamesList.size()];
        leagueNumberOfGamesList.toArray(leagueNumberOfGames);
    }
}
