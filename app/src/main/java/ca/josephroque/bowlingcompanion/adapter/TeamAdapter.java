package ca.josephroque.bowlingcompanion.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.wrapper.NameAverageId;

/**
 * Created by Joseph Roque on 2016-01-01. Displays the list of available bowlers to the user and indicates those
 * selected for a team. Additionally, displays league names belonging to a selected bowler.
 *
 * @param <T> type of data being displayed
 */
public class TeamAdapter<T extends NameAverageId>
        extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder>
        implements View.OnClickListener {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "TeamAdapter";

    /** Indicates the data represents bowlers. */
    public static final byte DATA_BOWLERS = 0;
    /** Indicates the data represents leagues and events. */
    public static final byte DATA_LEAGUES_EVENTS = 1;

    /** Instance of handler for callback on user action. */
    private TeamEventHandler mEventHandler;
    /** The {@link android.support.v7.widget.RecyclerView} that this adapter is attached to. */
    private RecyclerView mRecyclerView;

    /**
     * List of {@link ca.josephroque.bowlingcompanion.wrapper.NameAverageId} instances to be displayed by name in the
     * adapter.
     */
    private List<T> mListOptions;
    /**
     * Objects displayed by the adapter which are available to be selected. If an object maps to a boolean which is
     * {@code true}, it was selected by the user.
     */
    private final Map<T, Boolean> mMapOptionsSelected;

    /** Type of data being represented by this object. */
    private final byte mDataType;

    /**
     * Sets member variables to parameters.
     *
     * @param handler handles on click events on views
     * @param listNames list of names to be displayed
     * @param mapSelected map of objects which are selected or not
     * @param dataType type of data being managed by this object
     */
    public TeamAdapter(TeamEventHandler handler,
                       List<T> listNames,
                       Map<T, Boolean> mapSelected,
                       byte dataType) {
        mEventHandler = handler;
        mListOptions = listNames;
        mMapOptionsSelected = mapSelected;
        mDataType = dataType;

        if (mDataType != DATA_BOWLERS && mDataType != DATA_LEAGUES_EVENTS)
            throw new IllegalArgumentException("Invalid data type: " + dataType);
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
    }

    @Override
    public TeamViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_checkable, parent, false);
        return new TeamViewHolder(itemView, mDataType);
    }

    @Override
    public void onBindViewHolder(final TeamViewHolder holder, final int position) {
        holder.mTextViewName.setText(mListOptions.get(position).getName());
        if (mDataType == DATA_BOWLERS) {
            Boolean selected = mMapOptionsSelected.get(mListOptions.get(position));
            if (selected != null)
                holder.mCheckBoxItemSelected.setChecked(selected);
        }

        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mListOptions.size();
    }

    @Override
    public void onClick(View view) {
        // Notify the event handler of the selected item
        if (mRecyclerView != null && mEventHandler != null)
            mEventHandler.onItemSelected(mDataType, mRecyclerView.getChildAdapterPosition(view));
    }

    /**
     * Subclass of {@link android.support.v7.widget.RecyclerView.ViewHolder} to manage view which will display a
     * checkbox and text to the user.
     */
    public static final class TeamViewHolder
            extends RecyclerView.ViewHolder {

        /** Checkbox indicating if the item was selected. */
        private CheckBox mCheckBoxItemSelected;
        /** TextView to display the name of the item. */
        private TextView mTextViewName;

        /**
         * Calls super constructor and gets instances of CheckBox and TextView objects for member variables from
         * itemLayoutView.
         *
         * @param itemLayoutView layout view containing views to display data
         * @param dataType type of data being displayed
         */
        private TeamViewHolder(View itemLayoutView, byte dataType) {
            super(itemLayoutView);

            mCheckBoxItemSelected = (CheckBox) itemLayoutView.findViewById(R.id.checkbox_item_selected);
            mTextViewName = (TextView) itemLayoutView.findViewById(R.id.tv_item_name);

            if (dataType == DATA_LEAGUES_EVENTS)
                mCheckBoxItemSelected.setVisibility(View.GONE);
        }
    }

    /**
     * Callback interface for reporting user interactions within the adapter.
     */
    public interface TeamEventHandler {

        /**
         * Invoked when an item in the {@link android.support.v7.widget.RecyclerView} is selected by the user.
         *
         * @param dataType either {@link TeamAdapter#DATA_BOWLERS} or {@link TeamAdapter#DATA_LEAGUES_EVENTS}
         * @param position position of the selected item
         */
        void onItemSelected(byte dataType, int position);
    }
}
