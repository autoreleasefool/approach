package ca.josephroque.bowlingcompanion.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.utilities.NavigationUtils;

/**
 * Created by Joseph Roque on 15-03-28.
 * <p/>
 * Manages the data which will be displayed by the RecyclerView in the Navigation Drawer.
 * Offers a callback interface {@link NavigationDrawerAdapter.NavigationCallback} to handle
 * interaction events.
 */
public class NavigationDrawerAdapter
        extends RecyclerView.Adapter<NavigationDrawerAdapter.NavigationViewHolder>
        implements View.OnClickListener
{

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "NavigationDrawerAdapter";

    /** Represents an item in the navigation drawer which is a simple navigation item. */
    private static final int VIEW_TYPE_NAVIGATION = 0;
    /** Represents an item in the navigation drawer which features a subheader. */
    private static final int VIEW_TYPE_SUBHEADER = 1;

    /** Background color of views for selected items. */
    private static final int COLOR_SELECTED_BACKGROUND = 0x28000000;

    /** Callback listener for user events. */
    private NavigationCallback mCallback;

    /** List of options to be displayed in the navigation drawer. */
    private List<String> mListNavigationItems;
    /**
     * If an item is of the type {@code VIEW_TYPE_NAVIGATION}, it can feature a subtitle, which can
     * be found in this array by the item's position.
     */
    private SparseArray<String> mArraySubtitle;
    /** Set of positions which are subheader items. */
    private Set<String> mSetSubheaderItems;

    /** The most recently selected navigation item. */
    private int mCurrentNavigationItem;

    /**
     * Calls super constructor and attempts to cast context to a listener for events.
     *
     * @param callback instance of callback interface
     * @param listItems items which will be displayed in the drawer
     */
    public NavigationDrawerAdapter(NavigationCallback callback,
                                   List<String> listItems)
    {
        this.mListNavigationItems = listItems;
        mArraySubtitle = new SparseArray<>();
        mSetSubheaderItems = new TreeSet<>();
        this.mCallback = callback;
    }

    @Override
    public NavigationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView;
        switch (viewType)
        {
            case VIEW_TYPE_SUBHEADER:
                rootView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_navigation_subheader, parent, false);
                break;
            case VIEW_TYPE_NAVIGATION:
                rootView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_navigation, parent, false);
                break;
            default:
                throw new IllegalStateException("invalid view type: " + viewType);
        }
        return new NavigationViewHolder(rootView, viewType);
    }

    @Override
    public void onBindViewHolder(NavigationViewHolder viewHolder, int position) {
        final int viewType = getItemViewType(position);

        switch (viewType) {
            case VIEW_TYPE_NAVIGATION:
                viewHolder.itemView.setTag(position);
                viewHolder.itemView.setOnClickListener(this);
                int icon = getItemIcon(position);
                if (icon != 0)
                    viewHolder.mImageViewIcon.setImageResource(icon);
                viewHolder.mTextViewTitle.setText(mListNavigationItems.get(position));
                String extra = mArraySubtitle.get(position);
                if (extra != null)
                    viewHolder.mTextViewSubtitle.setText(extra);

                if (mCurrentNavigationItem == position)
                    viewHolder.itemView.setBackgroundColor(COLOR_SELECTED_BACKGROUND);
                else
                    setDrawableBackground(viewHolder.itemView,
                            getUnselectedBackground(viewHolder.itemView.getContext()));
                break;
            case VIEW_TYPE_SUBHEADER:
                setDrawableBackground(viewHolder.itemView,
                        getUnselectedBackground(viewHolder.itemView.getContext()));
                viewHolder.mTextViewTitle.setText(mListNavigationItems.get(position));
                break;
            default:
                throw new IllegalStateException("invalid view type: " + viewType);
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        if (mSetSubheaderItems.contains(mListNavigationItems.get(position)))
            return VIEW_TYPE_SUBHEADER;
        else
            return VIEW_TYPE_NAVIGATION;
    }

    @Override
    public int getItemCount()
    {
        return mListNavigationItems.size();
    }

    @Override
    public void onClick(View src)
    {
        // Header offset is accounted for when tag is set
        int position;
        try
        {
            position = (Integer) src.getTag();
        }
        catch (Exception ex)
        {
            throw new IllegalStateException("itemView tag must be position");
        }

        final int tempPosition = mCurrentNavigationItem;
        mCurrentNavigationItem = position;
        notifyItemChanged(tempPosition);
        notifyItemChanged(mCurrentNavigationItem);

        mCallback.onNavigationItemClicked(position);
    }

    private int getItemIcon(int position)
    {
        if (mListNavigationItems.get(position).matches("\\w+ \\d+"))
        {
            if (mCurrentNavigationItem == position)
                return R.drawable.ic_radio_button_on;
            else
                return R.drawable.ic_radio_button_off;
        }

        switch (mListNavigationItems.get(position))
        {
            case NavigationUtils.NAVIGATION_ITEM_BOWLERS:
                return R.drawable.ic_action_group;
            case NavigationUtils.NAVIGATION_ITEM_LEAGUES:
                return R.drawable.ic_list_black_24dp;
            case NavigationUtils.NAVIGATION_ITEM_SERIES:
                return R.drawable.ic_event_black_24dp;
            case NavigationUtils.NAVIGATION_ITEM_SETTINGS:
                return R.drawable.ic_settings_black_24dp;
            case NavigationUtils.NAVIGATION_ITEM_FEEDBACK:
                return R.drawable.ic_mail_black_24dp;
            default:
                return 0;
        }
    }

    /**
     * Sets a position to be a subheader. If it is already a navigation item or a switch, it is
     * reassigned.
     *
     * @param position position of subheader
     */
    @SuppressWarnings("unused")
    public void setPositionToSubheader(int position) {
        final int headerOffset = (mIsHeaderEnabled)
                ? 1
                : 0;

        if (position >= getItemCount() - headerOffset || position < 0) {
            throw new IllegalArgumentException(
                    "Must be between 0 and " + (getItemCount() - 1 - headerOffset));
        }

        // When a 'position' value comes from outside of the navigation drawer, whether or not there
        // is a header doesn't matter, so the positions are constant and do not need to be offset by
        // 1 before they are used

        if (mSetSwitchItems.contains(position)) {
            mSetSwitchItems.remove(position);
            mArraySwitchActive.remove(position);
        } else if (mArraySubtitle.indexOfKey(position) >= 0) {
            mArraySubtitle.remove(position);
        }

        mSetSubheaderItems.add(position);
        notifyItemChanged(position);
    }

    /**
     * Sets the subtitle of a navigation item.
     *
     * @param position position of subtitle
     * @param text subtitle text
     */
    public void setSubtitle(String title, String text)
    {
        if (mSetSubheaderItems.contains(mListNavigationItems.get(position)))
            throw new IllegalArgumentException("Can't set subtitle of subheader item: " + position);

        mArraySubtitle.put(position, text);
        notifyItemChanged(position);
    }

    /**
     * Sets the background of a view using the most current method, depending on the API available.
     *
     * @param view to set background of
     * @param background new background drawable
     */
    @SuppressWarnings("deprecation") // uses undeprecated methods in newer apis
    private void setDrawableBackground(View view, Drawable background)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            view.setBackground(background.mutate());
        else
            view.setBackgroundDrawable(background.mutate());
    }

    /**
     * Gets the drawable to set for unselected items in the drawer.
     *
     * @param context to create drawable
     * @return selectable item background
     */
    private Drawable getUnselectedBackground(Context context)
    {
        int[] attrs = {android.R.attr.selectableItemBackground};
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        Drawable unselectedBackground = typedArray.getDrawable(0);
        typedArray.recycle();
        return unselectedBackground;
    }

    //@Override
    /*public View getView(int position, View view, ViewGroup viewGroup)
    {
        DrawerViewHolder viewHolder;

        if (view == null)
        {
            viewHolder = new DrawerViewHolder();
            view = View.inflate(getContext(), R.layout.list_drawer, null);
            viewHolder.mImageViewIcon = (ImageView) view.findViewById(R.id.iv_list_drawer);
            viewHolder.mTextViewOption = (TextView) view.findViewById(R.id.tv_list_drawer);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (DrawerViewHolder) view.getTag();
        }

        String option = mListOptions.get(position);
        setNavIconByOption(option, viewHolder);

        return view;
    }*/

    /**
     * Sets icon of items in navigation drawer depending on their name.
     * @param option name of item
     * @param viewHolder views
     */
    /*@SuppressWarnings("StringEquality") //String constants are added to list, so
                                        //direct comparison can be used
    private void setNavIconByOption(String option, DrawerViewHolder viewHolder)
    {
        //Displays a different icon next to different navigational options
        viewHolder.mTextViewOption.setText(option);
        if (option == Constants.NAV_OPTION_HOME)
        {
            viewHolder.mImageViewIcon.setVisibility(View.VISIBLE);
            viewHolder.mImageViewIcon.setImageResource(R.drawable.ic_home);
        }
        else if (option == Constants.NAV_OPTION_BOWLERS)
        {
            viewHolder.mImageViewIcon.setVisibility(View.VISIBLE);
            viewHolder.mImageViewIcon.setImageResource(R.drawable.ic_action_group);
        }
        else if (option == Constants.NAV_OPTION_LEAGUES_EVENTS)
        {
            viewHolder.mImageViewIcon.setVisibility(View.VISIBLE);
            viewHolder.mImageViewIcon.setImageResource(R.drawable.ic_action_storage);
        }
        else if (option == Constants.NAV_OPTION_SERIES)
        {
            viewHolder.mImageViewIcon.setVisibility(View.VISIBLE);
            viewHolder.mImageViewIcon.setImageResource(R.drawable.ic_action_series);
        }
        else if (option == Constants.NAV_OPTION_GAME_DETAILS)
        {
            viewHolder.mImageViewIcon.setVisibility(View.VISIBLE);
            viewHolder.mImageViewIcon.setImageResource(R.drawable.ic_action_pin);
        }
        else if (option == Constants.NAV_OPTION_STATS)
        {
            viewHolder.mImageViewIcon.setVisibility(View.VISIBLE);
            viewHolder.mImageViewIcon.setImageResource(R.drawable.ic_action_stats);
        }
        else if (option == Constants.NAV_OPTION_SETTINGS)
        {
            viewHolder.mImageViewIcon.setVisibility(View.VISIBLE);
            viewHolder.mImageViewIcon.setImageResource(R.drawable.ic_action_settings);
        }
        else if (option.matches("\\w+ \\d+"))
        {
            viewHolder.mImageViewIcon.setVisibility(View.VISIBLE);
            viewHolder.mImageViewIcon.setImageResource(
                    (option.substring(option.indexOf(" ") + 1).equals(mCurrentGame)
                            ? R.drawable.ic_radio_button_on : R.drawable.ic_radio_button_off));
        }
        else
        {
            viewHolder.mImageViewIcon.setVisibility(View.GONE);
        }
    }*/

    /**
     * Assigns a new value to {@code mCurrentGame}.
     *
     * @param currentGame new value for mCurrentGame (1 will be added to it)
     */
    /*public void setCurrentGame(byte currentGame)
    {
        mCurrentGame = String.valueOf(currentGame + 1);
    }*/

    /**
     * Returns a cast of mCurrentGame to byte minus one.
     *
     * @return byte representation of mCurrentGame - 1
     */
    /*public byte getCurrentGame()
    {
        return (byte) (Byte.parseByte(mCurrentGame) - 1);
    }*/

    /*@Override
    public void onItemClick(AdapterView parent, View view, int position, long id)
    {
        String option = mListOptions.get(position);

        //If the option selected was a game number
        if (option.matches("\\w+ \\d+"))
            mDrawerClickListener.onGameItemClicked(
                    (byte) (Byte.parseByte(option.substring(5)) - 1));
            //else, a certain fragment should be navigated to
        else
            mDrawerClickListener.onFragmentItemClicked(option);
    }*/

    /**
     * Callback interface to report user interaction.
     */
    public interface NavigationCallback {

        /**
         * Invoked when a user clicks an item in the navigation drawer.
         *
         * @param position position of item clicked
         */
        void onNavigationItemClicked(int position);
    }

    /**
     * Recycles views for RecyclerView so they do not need to be reinflated.
     */
    public static final class NavigationViewHolder
            extends RecyclerView.ViewHolder {

        /** Displays an icon for an item in the navigation drawer. */
        private ImageView mImageViewIcon;
        /** Displays a title for an item in the navigation drawer. */
        private TextView mTextViewTitle;
        /** Displays a subtitle for an item in the navigation drawer. */
        private TextView mTextViewSubtitle;

        /**
         * Gets references to member variables depending on {@code viewType}.
         *
         * @param rootView root item layout
         */
        public NavigationViewHolder(View rootView) {
            super(rootView);

            mTextViewTitle = (TextView) rootView.findViewById(R.id.tv_nav_title);
            mImageViewIcon = (ImageView) rootView.findViewById(R.id.iv_nav_icon);
            mTextViewSubtitle = (TextView) rootView.findViewById(R.id.tv_nav_subtitle);
        }
    }
}
