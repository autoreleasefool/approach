package ca.josephroque.bowlingcompanion.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.utilities.NavigationUtils;

/**
 * Created by Joseph Roque on 15-03-28. Manages the data which will be displayed by the RecyclerView in the Navigation
 * Drawer. Offers a callback interface {@code NavigationDrawerAdapter.NavigationCallback} to handle interaction events.
 */
public class NavigationDrawerAdapter
        extends RecyclerView.Adapter<NavigationDrawerAdapter.NavigationViewHolder>
        implements View.OnClickListener {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "NavigationDrawerAdapter";

    /** Represents an item in the navigation drawer which is a simple navigation item. */
    private static final int VIEW_TYPE_NAVIGATION = 0;
    /** Represents an item in the navigation drawer which features a subheader. */
    private static final int VIEW_TYPE_SUBHEADER = 1;
    /** Represents an item in the navigation drawer which is a header. */
    private static final int VIEW_TYPE_HEADER = 2;

    /** Background color of views for selected items. */
    private static final int COLOR_SELECTED_BACKGROUND = 0x28000000;

    /** Callback listener for user events. */
    private NavigationCallback mCallback;
    /** The {@link RecyclerView} this adapter is attached to. */
    private RecyclerView mRecyclerView;

    /** List of options to be displayed in the navigation drawer. */
    private final List<String> mListNavigationItems;
    /**
     * If an item is of the type {@code VIEW_TYPE_NAVIGATION}, it can feature a subtitle, which can be found in this
     * array by the item's position.
     */
    private final HashMap<String, String> mArraySubtitle;
    /** Set of positions which are subheader items. */
    private final Set<String> mSetSubheaderItems;

    /** String to display as header text in the navigation drawer. */
    private String mHeaderTitle;
    /** String to display as header subtitle in the navigation drawer. */
    private String mHeaderSubtitle;

    /** The most recently selected navigation item. */
    private int mCurrentNavigationItem;

    /**
     * Calls super constructor and attempts to cast context to a listener for events.
     *
     * @param callback instance of callback interface
     * @param listItems items which will be displayed in the drawer
     */
    public NavigationDrawerAdapter(NavigationCallback callback,
                                   List<String> listItems) {
        this.mListNavigationItems = listItems;
        mArraySubtitle = new HashMap<>();
        mSetSubheaderItems = new TreeSet<>();
        this.mCallback = callback;
    }

    @Override
    public NavigationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                rootView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_navigation_header, parent, false);
                break;
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
            case VIEW_TYPE_HEADER:
                viewHolder.mTextViewTitle.setText(mHeaderTitle);
                viewHolder.mTextViewSubtitle.setText(mHeaderSubtitle);
                break;
            case VIEW_TYPE_NAVIGATION:
                viewHolder.itemView.setOnClickListener(this);
                int icon = getItemIcon(position);
                if (icon != 0)
                    viewHolder.mImageViewIcon.setImageResource(icon);
                viewHolder.mTextViewTitle.setText(mListNavigationItems.get(position));
                String extra = mArraySubtitle.get(mListNavigationItems.get(position));
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
    public int getItemViewType(int position) {
        if (position == 0)
            return VIEW_TYPE_HEADER;
        else if (mSetSubheaderItems.contains(mListNavigationItems.get(position)))
            return VIEW_TYPE_SUBHEADER;
        else
            return VIEW_TYPE_NAVIGATION;
    }

    @Override
    public int getItemCount() {
        return mListNavigationItems.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        // Releasing references
        mCallback = null;
        mRecyclerView = null;
    }

    @Override
    public void onClick(View src) {
        final int position = mRecyclerView.getChildAdapterPosition(src);
        if (mListNavigationItems.get(position).matches("\\w+ \\d+")) {
            final int tempPosition = mCurrentNavigationItem;
            mCurrentNavigationItem = position;
            notifyItemChanged(tempPosition);
            notifyItemChanged(mCurrentNavigationItem);
        }
        mCallback.onNavigationItemClicked(position);
    }

    /**
     * Gets the icon for an item.
     *
     * @param position position to get icon of
     * @return id of icon drawable
     */
    private int getItemIcon(int position) {
        if (mListNavigationItems.get(position).matches("\\w+ \\d+")) {
            if (mCurrentNavigationItem == position)
                return R.drawable.ic_radio_button_checked_black_24dp;
            else
                return R.drawable.ic_radio_button_unchecked_black_24dp;
        }

        switch (mListNavigationItems.get(position)) {
            case NavigationUtils.NAVIGATION_ITEM_BOWLERS:
                return R.drawable.ic_people_black_24dp;
            case NavigationUtils.NAVIGATION_ITEM_LEAGUES:
                return R.drawable.ic_list_black_24dp;
            case NavigationUtils.NAVIGATION_ITEM_SERIES:
                return R.drawable.ic_event_black_24dp;
            case NavigationUtils.NAVIGATION_ITEM_SETTINGS:
                return R.drawable.ic_settings_black_24dp;
            case NavigationUtils.NAVIGATION_ITEM_FEEDBACK:
                return R.drawable.ic_mail_black_24dp;
            case NavigationUtils.NAVIGATION_ITEM_HELP:
                return R.drawable.ic_help_outline_black_24dp;
            default:
                return 0;
        }
    }

    /**
     * Sets a position to be a subheader. If it is already a navigation item or a switch, it is reassigned.
     *
     * @param title item to set to subheader
     */
    @SuppressWarnings("unused")
    public void setPositionToSubheader(String title) {
        if (!mListNavigationItems.contains(title))
            throw new IllegalArgumentException("Must be valid navigation item");

        if (mArraySubtitle.containsKey(title))
            mArraySubtitle.remove(title);

        mSetSubheaderItems.add(title);
        notifyItemChanged(mListNavigationItems.indexOf(title));
    }

    /**
     * Sets the subtitle of a navigation item.
     *
     * @param title item to get subtitle
     * @param text subtitle text
     */
    public void setSubtitle(String title, String text) {
        if (mSetSubheaderItems.contains(title))
            throw new IllegalArgumentException("Can't set subtitle of subheader item: " + title);

        mArraySubtitle.put(title, text);
        notifyItemChanged(mListNavigationItems.indexOf(title));
    }

    /**
     * Sets the background of a view using the most current method, depending on the API available.
     *
     * @param view to set background of
     * @param background new background drawable
     */
    @SuppressWarnings("deprecation") // uses undeprecated methods in newer apis
    private void setDrawableBackground(View view, Drawable background) {
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
    private Drawable getUnselectedBackground(Context context) {
        int[] attrs = {android.R.attr.selectableItemBackground};
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        Drawable unselectedBackground = typedArray.getDrawable(0);
        typedArray.recycle();
        return unselectedBackground;
    }

    /**
     * Gets the currently selected navigation item.
     *
     * @return mCurrentNavigationItem
     */
    public int getCurrentItem() {
        return mCurrentNavigationItem;
    }

    /**
     * Sets the current navigation item.
     *
     * @param position new item
     */
    public void setCurrentItem(int position) {
        final int tempPosition = mCurrentNavigationItem;
        mCurrentNavigationItem = position;
        notifyItemChanged(mCurrentNavigationItem);
        notifyItemChanged(tempPosition);
    }

    /**
     * Sets the text for the navigation drawer header title.
     *
     * @param title new title text
     */
    public void setHeaderTitle(String title) {
        this.mHeaderTitle = title;
    }

    /**
     * Sets the text for the navigation drawer header subtitle.
     *
     * @param subtitle new subtitle text
     */
    public void setHeaderSubtitle(String subtitle) {
        this.mHeaderSubtitle = subtitle;
    }

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
         * @param viewType view type
         */
        public NavigationViewHolder(View rootView, int viewType) {
            super(rootView);

            switch (viewType) {
                case VIEW_TYPE_NAVIGATION:
                    mImageViewIcon = (ImageView) rootView.findViewById(R.id.iv_nav_icon);
                case VIEW_TYPE_HEADER:
                    mTextViewSubtitle = (TextView) rootView.findViewById(R.id.tv_nav_subtitle);
                default:
                    mTextViewTitle = (TextView) rootView.findViewById(R.id.tv_nav_title);
            }
        }
    }
}
