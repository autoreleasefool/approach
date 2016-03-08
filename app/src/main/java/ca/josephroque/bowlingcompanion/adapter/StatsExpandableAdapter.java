package ca.josephroque.bowlingcompanion.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.theme.Theme;
import ca.josephroque.bowlingcompanion.view.AnimatedExpandableListView;

/**
 * Created by Joseph Roque on 15-04-03. Manages statistic group headers, names and their associated values for a
 * ListView. Offers a callback interface {@link NameAverageAdapter.NameAverageEventHandler} to handle interaction
 * events.
 */
public class StatsExpandableAdapter
        extends AnimatedExpandableListView.AnimatedExpandableListAdapter
        implements Theme.ChangeableTheme {

    /** Identifies output from this class in Logcat. */
    @SuppressWarnings("unused")
    private static final String TAG = "StatsExpandable";

    /** Context which created this object. */
    private final Context mContext;

    /** List of group headers. */
    private final List<String> mListStatHeaders;
    /** List of list of pairs which hold a name and a value, for each group. */
    private final List<List<Pair<String, String>>> mListStatNamesAndValues;

    /**
     * Assigns member variables to parameters.
     *
     * @param context context which created this object
     * @param listHeaders list of group headers
     * @param listNamesAndValues list of entries to display beneath each group
     */
    public StatsExpandableAdapter(Context context,
                                  List<String> listHeaders,
                                  List<List<Pair<String, String>>> listNamesAndValues) {
        this.mContext = context;
        this.mListStatHeaders = listHeaders;
        this.mListStatNamesAndValues = listNamesAndValues;
    }

    @Override
    public Pair<String, String> getChild(int groupPosition, int childPosition) {
        return mListStatNamesAndValues.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @SuppressWarnings("unchecked")
    // getChild guaranteed to return AbstractMap.SimpleEntry<String, String>
    @Override
    public View getRealChildView(int groupPosition,
                                 int childPosition,
                                 boolean isLastChild,
                                 View convertView,
                                 ViewGroup parent) {
        final Pair<String, String> childNameAndValue = getChild(groupPosition, childPosition);

        StatViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new StatViewHolder();
            convertView = View.inflate(mContext, R.layout.list_stats_item, null);
            viewHolder.mTextViewStatName = (TextView) convertView.findViewById(R.id.tv_stat_name);
            viewHolder.mTextViewStatValue = (TextView) convertView.findViewById(R.id.tv_stat_value);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (StatViewHolder) convertView.getTag();
        }

        viewHolder.mTextViewStatName.setText(childNameAndValue.first);
        viewHolder.mTextViewStatValue.setText(childNameAndValue.second);

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return mListStatNamesAndValues.get(groupPosition).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return mListStatHeaders.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mListStatHeaders.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition,
                             boolean isExpanded,
                             View convertView,
                             ViewGroup parent) {
        String headerTitle = getGroup(groupPosition);

        HeaderViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new HeaderViewHolder();
            convertView = View.inflate(mContext, R.layout.list_stats_header, null);
            viewHolder.mTextViewHeader = (TextView) convertView.findViewById(R.id.tv_stat_header);
            viewHolder.mBackgroundView = convertView.findViewById(R.id.ll_stats_header);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (HeaderViewHolder) convertView.getTag();
        }

        viewHolder.mTextViewHeader.setText(headerTitle);
        viewHolder.mTextViewHeader.setTypeface(null, Typeface.BOLD);
        viewHolder.mBackgroundView.setBackgroundColor(Theme.getSecondaryThemeColor());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * Holds views so if an item view is recycled by the list, references are retained to its children.
     */
    private static class StatViewHolder {

        /** TextView to display name of a stat. */
        private TextView mTextViewStatName;
        /** TextView to display value of a stat. */
        private TextView mTextViewStatValue;
    }

    /**
     * Holds views so if a header view is recycled by the list, references are retained to its children.
     */
    private static class HeaderViewHolder {

        /** TextView to display header of a group. */
        private TextView mTextViewHeader;
        /** Background of the view, to set color for theme. */
        private View mBackgroundView;
    }

    @Override
    public void updateTheme() {
        notifyDataSetChanged();
    }
}
