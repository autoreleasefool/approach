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
 * Created by josephroque on 15-01-10.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.adapter
 * in project Bowling Companion
 */
public class LeagueAverageListAdapter extends ArrayAdapter<Long>
{

    /** The Activity used to create this object */
    private final Activity context;
    /** A list of league names */
    private String[] leagueNames;
    /** A list of averages which is assumed to be in the same order as leagueNames */
    private Integer[] leagueAverages;
    /** A list of the number of games which is assumed to be in the same order as leagueNames */
    private Integer[] leagueNumberOfGames;

    /**
     * Stores the parameters in class variables
     * @param context activity used to create this object
     * @param leagueIDs a list of ids which the adapter will return when an item is selected
     * @param leagueNamesList a list of league names, in an order relative to leagueIDs
     * @param leagueAveragesList a list of league averages, in an order relative to leagueIDs
     * @param leagueNumberOfGamesList a list of number of games, in an order relative to leagueIDs
     */
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
            txtNumberOfGames.setText(" Games per series: " + String.valueOf(leagueNumberOfGames[position]));
        }

        return rowView;
    }

    /**
     * Updates the class values to the parameters
     *
     * @param leagueNamesList a list of league names
     * @param leagueAveragesList a list of league averages
     * @param leagueNumberOfGamesList a list of number of games
     */
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
