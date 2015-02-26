package ca.josephroque.bowlingcompanion.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ca.josephroque.bowlingcompanion.fragments.LeagueEventFragment;

/**
 * Created by josephroque on 15-02-18.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.adapter
 * in project Bowling Companion
 */
public class LeagueEventFragmentPagerAdapter extends FragmentPagerAdapter
{
    /** Total number of fragments displayed by this adapter */
    final int PAGE_COUNT = 2;
    /** Titles of fragment tabs */
    private String[] tabTitles = {"Leagues", "Events"};

    /**
     * Default constructor
     *
     * @param fm FragmentManager of activity
     */
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
            case 0: return LeagueEventFragment.newInstance(false);
            case 1: return LeagueEventFragment.newInstance(true);
            default: return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return tabTitles[position];
    }
}
