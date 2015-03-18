package ca.josephroque.bowlingcompanion.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.List;

import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.theme.Theme;

/**
 * Created by josephroque on 15-03-13.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.adapter
 * in project Bowling Companion
 */
public class NameAverageAdapter extends RecyclerView.Adapter<NameAverageAdapter.NameAverageViewHolder>
    implements View.OnClickListener, View.OnLongClickListener
{
    /** Tag to identify class when outputting to console */
    private static final String TAG = "NameAverageAdapter";

    /** Indicates data represents bowlers */
    public static final byte DATA_BOWLERS = 0;
    /** Indicates data represents leagues and events */
    public static final byte DATA_LEAGUES_EVENTS = 1;

    /** Instance of handler for callback on user action */
    private NameAverageEventHandler mEventHandler;
    /** List of names which will be displayed */
    private List<String> mListNames;
    /** List of averages which will be displayed, in an order relative to mListNames */
    private List<Short> mListAverages;

    /** Type of data being represented by this object */
    private byte mDataType;

    /**
     * Subclass of RecyclerView.ViewHolder to manage view which will display an image,
     * and text to the user
     */
    public static class NameAverageViewHolder extends RecyclerView.ViewHolder
    {
        /** Displays an image representing the type of data in the row */
        private ImageView mImageViewType;
        /** Displays the name of the data in the row */
        private TextView mTextViewName;
        /** Displays the average of the data in the row */
        private TextView mTextViewAverage;
        /** Animates changes in color to the ViewHolder background */
        private ValueAnimator mValueAnimator = null;

        /**
         * Calls super constructor and gets instances of ImageView and TextView objects
         * for member variables from itemLayoutView
         * @param itemLayoutView layout view containing views to display data
         */
        private NameAverageViewHolder(View itemLayoutView)
        {
            super(itemLayoutView);
            mImageViewType = (ImageView)itemLayoutView.findViewById(R.id.iv_nameavg_type);
            mTextViewName = (TextView)itemLayoutView.findViewById(R.id.tv_nameavg_name);
            mTextViewAverage = (TextView)itemLayoutView.findViewById(R.id.tv_nameavg_average);
        }
    }

    /**
     * Sets member variables to parameters
     *
     * @param handler handles on click/long click events on views
     * @param listNames list of names to be displayed in RecyclerView
     * @param listAverages list of averages, relative to listNames to be displayed
     * @param dataType type of data being managed by this object
     */
    public NameAverageAdapter(NameAverageEventHandler handler, List<String> listNames, List<Short> listAverages, byte dataType)
    {
        mEventHandler = handler;
        mListNames = listNames;
        mListAverages = listAverages;
        mDataType = dataType;
    }

    @Override
    public NameAverageViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_name_average, parent, false);
        return new NameAverageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NameAverageViewHolder holder, final int position)
    {
        //Sets text/images depending on data type
        switch(mDataType)
        {
            case DATA_BOWLERS:
                holder.mTextViewName.setText(mListNames.get(position));
                holder.mImageViewType.setImageResource(R.drawable.ic_person);
                break;
            case DATA_LEAGUES_EVENTS:
                holder.mTextViewName.setText(mListNames.get(position).substring(1));
                holder.mImageViewType.setImageResource(
                        mListNames.get(position).startsWith("L")
                        ? R.drawable.ic_league
                        : R.drawable.ic_event);
                break;
        }
        holder.mTextViewAverage.setText("Avg: " +
                String.valueOf(mListAverages.get(position)));

        //Sets actions on click/touch events
        holder.itemView.setOnClickListener(this);
        holder.itemView.setOnLongClickListener(this);
        holder.itemView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(final View v, MotionEvent event)
            {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
                {
                    //Begins color change animation when user holds down this item
                    holder.mValueAnimator =
                            ValueAnimator.ofObject(new ArgbEvaluator(),
                                    Theme.getListItemBackground(),
                                    Theme.getLongPressThemeColor());
                    holder.mValueAnimator.setDuration(Theme.getMediumAnimationDuration());
                    holder.mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                    {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation)
                        {
                            v.setBackgroundColor((Integer)animation.getAnimatedValue());
                        }
                    });
                    holder.mValueAnimator.start();
                }
                else if ((event.getActionMasked() == MotionEvent.ACTION_UP || event.getActionMasked() == MotionEvent.ACTION_MOVE)
                        && holder.mValueAnimator != null)
                {
                    //Cancels the animation when the user moves or releases
                    holder.mValueAnimator.cancel();
                    holder.mValueAnimator = null;
                    v.setBackgroundColor(Theme.getListItemBackground());
                }

                return false;
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        //Calls relevant event handler method
        mEventHandler.onNAItemClick(mEventHandler.getNAViewPositionInRecyclerView(v));
    }

    @Override
    public boolean onLongClick(View v)
    {
        //Calls relevant event handler method
        mEventHandler.onNALongClick(mEventHandler.getNAViewPositionInRecyclerView(v));
        return true;
    }

    @Override
    public int getItemCount() {return mListNames.size();}

    /**
     * Provides methods to implement functionality when items
     * in the RecyclerView are interacted with
     */
    public static interface NameAverageEventHandler
    {

        /**
         * Called when an item in the RecyclerView is clicked
         * @param position position of the item in the list
         */
        public void onNAItemClick(final int position);

        /**
         * Called when an item in the RecyclerView is long clicked
         * @param position position of the item in the list
         */
        public void onNALongClick(final int position);

        /**
         * Should be used to return RecyclerView#getChildPosition(v) on the
         * recycler view which uses this adapter
         * @param v the view to get the position of
         * @return position of v in the parent RecyclerView
         */
        public int getNAViewPositionInRecyclerView(View v);
    }
}
