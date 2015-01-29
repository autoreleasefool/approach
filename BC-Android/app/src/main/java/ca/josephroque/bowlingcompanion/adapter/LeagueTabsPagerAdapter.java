package ca.josephroque.bowlingcompanion.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ca.josephroque.bowlingcompanion.fragments.LeagueFragment;
import ca.josephroque.bowlingcompanion.fragments.TournamentFragment;

/**
 * Created by josephroque on 15-01-28.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.adapter
 * in project Bowling Companion
 */
public class LeagueTabsPagerAdapter extends FragmentPagerAdapter
{

    public LeagueTabsPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int index)
    {
        switch(index)
        {
            case 0:
                return new LeagueFragment();
            case 1:
                return new TournamentFragment();
        }

        return null;
    }

    @Override
    public int getCount()
    {
        return 2;
    }
}
