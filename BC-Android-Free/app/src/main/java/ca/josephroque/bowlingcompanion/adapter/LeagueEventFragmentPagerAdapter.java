package ca.josephroque.bowlingcompanion.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ca.josephroque.bowlingcompanion.fragments.EventFragment;
import ca.josephroque.bowlingcompanion.fragments.LeagueFragment;

/**
 * Created by josephroque on 15-02-18.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.adapter
 * in project Bowling Companion
 */
public class LeagueEventFragmentPagerAdapter extends FragmentPagerAdapter
{
    final int PAGE_COUNT = 2;
    private String[] tabTitles = {"Leagues", "Events"};

    public LeagueEventFragmentPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public int getCount()
    {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch(position)
        {
            case 0: return new LeagueFragment();
            case 1: return new EventFragment();
            default: return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return tabTitles[position];
    }
}
