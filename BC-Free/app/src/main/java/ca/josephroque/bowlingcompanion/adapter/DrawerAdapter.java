package ca.josephroque.bowlingcompanion.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ca.josephroque.bowlingcompanion.Constants;
import ca.josephroque.bowlingcompanion.R;

/**
 * Created by josephroque on 15-03-28.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.adapter
 * in project Bowling Companion
 */
public class DrawerAdapter extends ArrayAdapter<String>
    implements ListView.OnItemClickListener
{
    /** Callback listener for user events */
    private OnDrawerClickListener mDrawerClickListener;
    /** List of options to be displayed by the ListView */
    private List<String> mListOptions;
    /** Current game, which corresponds to a game tag with a filled radio button in the view */
    private String mCurrentGame = "0";

    /**
     * Calls super constructor and attempts to cast context to a listener for events
     * @param context context which created this object - must implement OnDrawerClickListener
     * @param listOptions options which will be displayed by the list
     */
    public DrawerAdapter(Context context, List<String> listOptions)
    {
        super(context, R.layout.list_drawer, listOptions);
        this.mListOptions = listOptions;

        try
        {
            /**
             * Attempts to cast parent context to OnDrawerClickListener and store a reference
             * to it for callbacks on events. If the cast fails, an error is thrown
             */
            mDrawerClickListener = (OnDrawerClickListener)context;
        }
        catch (ClassCastException ex)
        {
            throw new ClassCastException(context.toString() +
                    " must implement OnDrawerClickListener");
        }
    }

    @SuppressWarnings("StringEquality") //String constants are added to list, so
                                        //direct comparison can be used
    @Override
    public View getView(int position, View view, ViewGroup viewGroup)
    {
        ViewHolder viewHolder;

        if (view == null)
        {
            viewHolder = new ViewHolder();
            view = View.inflate(getContext(), R.layout.list_drawer, null);
            viewHolder.mImageViewIcon = (ImageView)view.findViewById(R.id.iv_list_drawer);
            viewHolder.mTextViewOption = (TextView)view.findViewById(R.id.tv_list_drawer);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)view.getTag();
        }

        //Displays a different icon next to different navigational options
        viewHolder.mTextViewOption.setText(mListOptions.get(position));
        if (mListOptions.get(position) == Constants.NAV_OPTION_HOME)
        {
            viewHolder.mImageViewIcon.setVisibility(View.VISIBLE);
            viewHolder.mImageViewIcon.setImageResource(R.drawable.ic_home);
        }
        else if (mListOptions.get(position) == Constants.NAV_OPTION_BOWLERS)
        {
            viewHolder.mImageViewIcon.setVisibility(View.VISIBLE);
            viewHolder.mImageViewIcon.setImageResource(R.drawable.ic_action_group);
        }
        else if (mListOptions.get(position) == Constants.NAV_OPTION_LEAGUES_EVENTS)
        {
            viewHolder.mImageViewIcon.setVisibility(View.VISIBLE);
            viewHolder.mImageViewIcon.setImageResource(R.drawable.ic_action_storage);
        }
        else if (mListOptions.get(position) == Constants.NAV_OPTION_SERIES)
        {
            viewHolder.mImageViewIcon.setVisibility(View.VISIBLE);
            viewHolder.mImageViewIcon.setImageResource(R.drawable.ic_action_series);
        }
        else if (mListOptions.get(position) == Constants.NAV_OPTION_GAME_DETAILS)
        {
            viewHolder.mImageViewIcon.setVisibility(View.VISIBLE);
            viewHolder.mImageViewIcon.setImageResource(R.drawable.ic_action_pin);
        }
        else if (mListOptions.get(position) == Constants.NAV_OPTION_STATS)
        {
            viewHolder.mImageViewIcon.setVisibility(View.VISIBLE);
            viewHolder.mImageViewIcon.setImageResource(R.drawable.ic_action_stats);
        }
        else if (mListOptions.get(position).matches("\\w+ \\d+"))
        {
            viewHolder.mImageViewIcon.setVisibility(View.VISIBLE);
            viewHolder.mImageViewIcon.setImageResource(
                    (mListOptions.get(position).substring(mListOptions.get(position).indexOf(" ") + 1).equals(mCurrentGame)
                    ? R.drawable.ic_radio_button_on : R.drawable.ic_radio_button_off));
        }
        else
        {
            viewHolder.mImageViewIcon.setVisibility(View.GONE);
        }

        return view;
    }

    /**
     * Assigns a new value to mCurrentGame
     * @param currentGame new value for mCurrentGame (1 will be added to it)
     */
    public void setCurrentGame(byte currentGame) {mCurrentGame = String.valueOf(currentGame + 1);}

    /**
     * Returns a cast of mCurrentGame to byte minus one
     * @return byte representation of mCurrentGame - 1
     */
    public byte getCurrentGame() {return (byte)(Byte.parseByte(mCurrentGame) - 1);}

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id)
    {
        String option = mListOptions.get(position);

        //If the option selected was a game number
        if (option.matches("\\w+ \\d+"))
            mDrawerClickListener.onGameItemClicked((byte)(Byte.parseByte(option.substring(5)) - 1));
        //else, a certain fragment should be navigated to
        else
            mDrawerClickListener.onFragmentItemClicked(option);
    }

    /**
     * Callback listener for user events on the list
     */
    public static interface OnDrawerClickListener
    {
        /**
         * Indicates a fragment should be navigated to
         * @param fragmentItem name of the fragment to navigate to
         */
        public void onFragmentItemClicked(String fragmentItem);

        /**
         * Indicates a game should be switched to
         * @param gameNumber number of the game to switch to
         */
        public void onGameItemClicked(byte gameNumber);
    }

    /**
     * Holds the views for a certain list item
     */
    private static class ViewHolder
    {
        /** Displays icon which represents the navigational option*/
        private ImageView mImageViewIcon;
        /** Text which describes the navigational option */
        private TextView mTextViewOption;
    }
}
