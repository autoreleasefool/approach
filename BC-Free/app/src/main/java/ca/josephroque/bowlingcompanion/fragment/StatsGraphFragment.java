package ca.josephroque.bowlingcompanion.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.josephroque.bowlingcompanion.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatsGraphFragment
        extends Fragment
{

    /**
     * Creates a new instance of {@code StatsGraphFragment} with the parameters provided.
     *
     * @return a new instance of StatsGraphFragment
     */
    public static StatsGraphFragment newInstance()
    {
        return new StatsGraphFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_stats_graph, container, false);



        return rootView;
    }


}
