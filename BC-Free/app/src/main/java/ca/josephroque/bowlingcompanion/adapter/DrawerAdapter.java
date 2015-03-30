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
    private OnDrawerClickListener mDrawerClickListener;
    private List<String> mListOptions;

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
        else
        {
            viewHolder.mImageViewIcon.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id)
    {
        String option = mListOptions.get(position);

        if (option.matches("\\w+ \\d+"))
            mDrawerClickListener.onGameItemClicked((byte)(Byte.parseByte(option.substring(5)) - 1));
        else
            mDrawerClickListener.onFragmentItemClicked(option);
    }

    public static interface OnDrawerClickListener
    {
        public void onFragmentItemClicked(String fragmentItem);
        public void onGameItemClicked(byte gameNumber);
    }

    private static class ViewHolder
    {
        private ImageView mImageViewIcon;
        private TextView mTextViewOption;
    }
}
