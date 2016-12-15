package ca.josephroque.bowlingcompanion.adapter;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.theme.Theme;
import ca.josephroque.bowlingcompanion.utilities.DisplayUtils;
import ca.josephroque.bowlingcompanion.wrapper.LeagueEvent;
import ca.josephroque.bowlingcompanion.wrapper.NameAverageId;

/**
 * Created by Joseph Roque on 15-03-13. Manages names of bowlers or leagues/events and their associated averages for a
 * ListView. Offers a callback interface {@link NameAverageAdapter.NameAverageEventHandler} to handle interaction
 * events.
 *
 * @param <T> Object of type NameAverageId which is displayed by this adapter
 */
public class NameAverageAdapter<T extends NameAverageId>
        extends RecyclerView.Adapter<NameAverageAdapter.NameAverageViewHolder>
        implements View.OnClickListener,
        View.OnLongClickListener {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "NameAverageAdapter";

    /** Represents an item in the list which is active. */
    private static final int VIEWTYPE_ACTIVE = 0;
    /** Represents an item in the list which has been deleted. */
    private static final int VIEWTYPE_DELETED = 1;

    /** Indicates data represents bowlers. */
    public static final byte DATA_BOWLERS = 0;
    /** Indicates data represents leagues and events. */
    public static final byte DATA_LEAGUES_EVENTS = 1;

    /** Instance of handler for callback on user action. */
    private NameAverageEventHandler mEventHandler;
    /** The {@link RecyclerView} that this adapter is attached to. */
    private RecyclerView mRecyclerView;

    /** List of names and averages to be displayed by the adapter. */
    private final List<T> mListNamesAndAverages;
    /** Cached drawables to display as icons for items. */
    private Drawable[] mItemDrawables;

    /** Type of data being represented by this object. */
    private final byte mDataType;

    /** Indicates how averages will be formatted - as an integer or to a single decimal place. */
    private boolean mAverageAsDecimal = false;

    /**
     * Subclass of RecyclerView.ViewHolder to manage view which will display an image, and text to the user.
     */
    public static final class NameAverageViewHolder
            extends RecyclerView.ViewHolder {

        /** Displays an image representing the type of data in the row. */
        private ImageView mImageViewType;
        /** Displays the name of the data in the row. */
        private TextView mTextViewName;
        /** Displays the average of the data in the row. */
        private TextView mTextViewAverage;

        /**
         * Calls super constructor and gets instances of ImageView and TextView objects for member variables from
         * itemLayoutView.
         *
         * @param itemLayoutView layout view containing views to display data
         * @param viewType type of view
         */
        private NameAverageViewHolder(View itemLayoutView, int viewType) {
            super(itemLayoutView);
            switch (viewType) {
                case VIEWTYPE_ACTIVE:
                    mImageViewType = (ImageView) itemLayoutView.findViewById(R.id.iv_name_average_type);
                    mTextViewName = (TextView) itemLayoutView.findViewById(R.id.tv_name_avg_name);
                    mTextViewAverage = (TextView) itemLayoutView.findViewById(
                            R.id.tv_name_average_average);
                    break;
                case VIEWTYPE_DELETED:
                    mImageViewType = (ImageView) itemLayoutView.findViewById(R.id.iv_delete);
                    mTextViewName = (TextView) itemLayoutView.findViewById(R.id.tv_delete);
                    mTextViewAverage = (TextView) itemLayoutView.findViewById(R.id.tv_undo_delete);
                    break;
                default:
                    throw new IllegalArgumentException("view type is invalid: " + viewType);
            }
        }
    }

    /**
     * Sets member variables to parameters.
     *
     * @param handler handles on click/long click events on views
     * @param listNameAverages list of names and averages to be displayed
     * @param dataType type of data being managed by this object
     */
    public NameAverageAdapter(NameAverageEventHandler handler,
                              List<T> listNameAverages,
                              byte dataType) {
        mEventHandler = handler;
        mListNamesAndAverages = listNameAverages;
        mDataType = dataType;

        switch (mDataType) {
            case DATA_BOWLERS:
                mItemDrawables = new Drawable[1];
                break;
            case DATA_LEAGUES_EVENTS:
                mItemDrawables = new Drawable[2];
                break;
            default:
                throw new IllegalArgumentException("invalid data type: " + mDataType);
        }
    }

    @Override
    public NameAverageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case VIEWTYPE_ACTIVE:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_name_average, parent, false);
                break;
            case VIEWTYPE_DELETED:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_deleted, parent, false);
                break;
            default:
                throw new IllegalArgumentException("view type is invalid: " + viewType);
        }

        return new NameAverageViewHolder(itemView, viewType);
    }

    @SuppressWarnings("CheckStyle")
    @Override
    public void onBindViewHolder(final NameAverageViewHolder holder, final int position) {
        final int viewType = getItemViewType(position);

        switch (viewType) {
            case VIEWTYPE_ACTIVE:
                // Sets text/images depending on data type
                switch (mDataType) {
                    case DATA_BOWLERS:
                        holder.mTextViewName.setText(mListNamesAndAverages.get(position).getName());
                        if (mItemDrawables[0] == null)
                            mItemDrawables[0]
                                    = DisplayUtils.getDrawable(holder.itemView.getResources(),
                                    R.drawable.ic_person_black_24dp);
                        holder.mImageViewType.setImageDrawable(mItemDrawables[0]);
                        break;
                    case DATA_LEAGUES_EVENTS:
                        holder.mTextViewName.setText(mListNamesAndAverages.get(position).getName());
                        if (mItemDrawables[0] == null || mItemDrawables[1] == null) {
                            mItemDrawables[0]
                                    = DisplayUtils.getDrawable(holder.itemView.getResources(),
                                    R.drawable.ic_l_black_24dp);
                            mItemDrawables[1]
                                    = DisplayUtils.getDrawable(holder.itemView.getResources(),
                                    R.drawable.ic_e_black_24dp);
                        }
                        LeagueEvent leagueEvent = (LeagueEvent) mListNamesAndAverages.get(position);
                        holder.mImageViewType.setImageDrawable(
                                !leagueEvent.isEvent()
                                        ? mItemDrawables[0]
                                        : mItemDrawables[1]);
                        break;
                    default:
                        throw new IllegalStateException("invalid mDataType: " + mDataType);
                }

                String average = DisplayUtils.getFormattedAverage(
                        Math.abs(mListNamesAndAverages.get(position).getAverage()), mAverageAsDecimal);

                holder.mTextViewAverage.setText(String.format(holder.mTextViewAverage.getResources()
                                .getString(R.string.text_average_placeholder), average));
                if (mListNamesAndAverages.get(position).getAverage() < 0) {
                    holder.mTextViewAverage.setTextColor(ContextCompat.getColor(holder.mTextViewAverage.getContext(),
                            R.color.invalid_average));
                } else {
                    holder.mTextViewAverage.setTextColor(ContextCompat.getColor(holder.mTextViewAverage.getContext(),
                            android.R.color.black));
                }

                if (position % 2 == 1) {
                    holder.itemView.setBackgroundColor(DisplayUtils.getColorResource(holder.itemView.getResources(),
                            R.color.secondary_background_offset));
                } else {
                    holder.itemView.setBackgroundColor(DisplayUtils.getColorResource(holder.itemView.getResources(),
                            R.color.secondary_background));
                }

                // Sets actions on click/touch events
                holder.itemView.setOnClickListener(this);
                holder.itemView.setOnLongClickListener(this);
                break;
            case VIEWTYPE_DELETED:
                String nameToDelete = mListNamesAndAverages.get(position).getName();
                final long idToDelete = mListNamesAndAverages.get(position).getId();
                final View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mEventHandler != null) {
                            if (v.getId() == R.id.tv_undo_delete)
                                mEventHandler.onNAItemUndoDelete(idToDelete);
                            else
                                mEventHandler.onNAItemDelete(idToDelete);
                        }
                    }
                };
                holder.itemView.setOnClickListener(null);
                holder.itemView.setOnLongClickListener(null);
                holder.itemView.setBackgroundColor(Theme.getTertiaryThemeColor());
                holder.mTextViewName.setText(String.format(holder.mTextViewName.getResources()
                        .getString(R.string.text_click_to_delete), nameToDelete));
                holder.mTextViewName.setOnClickListener(onClickListener);
                holder.mTextViewAverage.setOnClickListener(onClickListener);
                holder.mImageViewType.setOnClickListener(onClickListener);
                break;
            default:
                throw new IllegalArgumentException("invalid view type: " + viewType);
        }
    }

    @Override
    public void onClick(View v) {
        // Calls relevant event handler method
        if (mEventHandler != null && mRecyclerView != null)
            mEventHandler.onNAItemClick(mRecyclerView.getChildAdapterPosition(v));
    }

    @Override
    public boolean onLongClick(View v) {
        if (mEventHandler != null && mRecyclerView != null) {
            mEventHandler.onNAItemLongClick(mRecyclerView.getChildAdapterPosition(v));
            return true;
        }
        return false;
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
        mRecyclerView = null;
        mEventHandler = null;
        mItemDrawables = null;
    }

    @Override
    public int getItemCount() {
        return mListNamesAndAverages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (mListNamesAndAverages.get(position).wasDeleted())
                ? VIEWTYPE_DELETED
                : VIEWTYPE_ACTIVE;
    }

    /**
     * Updates the adapter to format the averages to either one decimal place or none.
     *
     * @param averageAsDecimal {@code true} to show up to one decimal place, {@code false} otherwise
     */
    public void setDisplayAverageAsDecimal(boolean averageAsDecimal) {
        if (averageAsDecimal == mAverageAsDecimal)
            // Nothing changed
            return;

        mAverageAsDecimal = averageAsDecimal;
        notifyDataSetChanged();
    }

    /**
     * Provides methods to implement functionality when items in the RecyclerView are interacted with.
     */
    public interface NameAverageEventHandler {

        /**
         * Called when an item in the RecyclerView is clicked.
         *
         * @param position position of the item in the list
         */
        void onNAItemClick(final int position);

        /**
         * Called when an item in the RecyclerView is long clicked.
         *
         * @param position position of the item in the list
         */
        void onNAItemLongClick(final int position);

        /**
         * Called when an item in the RecyclerView is confirmed by user for deletion.
         *
         * @param id id of the deleted item
         */
        void onNAItemDelete(long id);

        /**
         * Called when the user undoes a delete on an item in the RecyclerView.
         *
         * @param id id of the undeleted item
         */
        void onNAItemUndoDelete(long id);
    }
}
