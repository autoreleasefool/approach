package ca.josephroque.bowlingcompanion.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

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
    private Activity mActivity;
    private OnDrawerClickListener mDrawerClickListener;
    private List<String> mListOptions;

    public DrawerAdapter(Activity context, List<String> listOptions)
    {
        super(context, R.layout.list_drawer, listOptions);
        this.mActivity = context;
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

    @Override
    public View getView(int position, View view, ViewGroup viewGroup)
    {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        ViewHolder viewHolder;

        if (view == null)
        {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.list_drawer, null, true);
            viewHolder.mImageViewIcon = (ImageView)view.findViewById(R.id.iv_list_drawer);
            viewHolder.mTextViewOption = (TextView)view.findViewById(R.id.tv_list_drawer);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.mTextViewOption.setText(mListOptions.get(position));
        if (mListOptions.get(position).equals("Home"))
        {
            viewHolder.mImageViewIcon.setVisibility(View.VISIBLE);
            viewHolder.mImageViewIcon.setImageResource(R.drawable.ic_home);
        }
        else if (mListOptions.get(position).equals("Bowlers"))
        {
            viewHolder.mImageViewIcon.setVisibility(View.VISIBLE);
            viewHolder.mImageViewIcon.setImageResource(R.drawable.ic_action_group);
        }
        else if (mListOptions.get(position).equals("Leagues & Events"))
        {
            viewHolder.mImageViewIcon.setVisibility(View.VISIBLE);
            viewHolder.mImageViewIcon.setImageResource(R.drawable.ic_action_storage);
        }
        else if (mListOptions.get(position).equals("Series"))
        {
            viewHolder.mImageViewIcon.setVisibility(View.VISIBLE);
            viewHolder.mImageViewIcon.setImageResource(R.drawable.ic_action_series);
        }
        else if (mListOptions.get(position).equals("Game Details"))
        {
            viewHolder.mImageViewIcon.setVisibility(View.VISIBLE);
            viewHolder.mImageViewIcon.setImageResource(R.drawable.ic_action_pin);
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
