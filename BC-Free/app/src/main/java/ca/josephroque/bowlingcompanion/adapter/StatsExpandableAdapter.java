package ca.josephroque.bowlingcompanion.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.AbstractMap;
import java.util.List;

import ca.josephroque.bowlingcompanion.R;
import ca.josephroque.bowlingcompanion.theme.Theme;

/**
 * Created by josephroque on 15-04-03.
 * <p/>
 * Location ca.josephroque.bowlingcompanion.adapter
 * in project Bowling Companion
 */
public class StatsExpandableAdapter extends BaseExpandableListAdapter
    implements Theme.ChangeableTheme
{

    private Context mContext;
    private List<String> mListStatHeaders;
    private List<List<AbstractMap.SimpleEntry<String, String>>> mListStatNamesAndValues;

    public StatsExpandableAdapter(Context context, List<String> listHeaders, List<List<AbstractMap.SimpleEntry<String, String>>> listNamesAndValues)
    {
        this.mContext = context;
        this.mListStatHeaders = listHeaders;
        this.mListStatNamesAndValues = listNamesAndValues;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return mListStatNamesAndValues.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition){return childPosition;}

    @SuppressWarnings("unchecked") //getChild guaranteed to return AbstractMap.SimpleEntry<String, String>
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        final AbstractMap.SimpleEntry<String, String> childNameAndValue = (AbstractMap.SimpleEntry<String, String>)getChild(groupPosition, childPosition);

        StatViewHolder viewHolder;
        if (convertView == null)
        {
            viewHolder = new StatViewHolder();
            convertView = View.inflate(mContext, R.layout.list_stats_item, null);
            viewHolder.mTextViewStatName = (TextView)convertView.findViewById(R.id.tv_stat_name);
            viewHolder.mTextViewStatValue = (TextView)convertView.findViewById(R.id.tv_stat_value);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (StatViewHolder)convertView.getTag();
        }

        viewHolder.mTextViewStatName.setText(childNameAndValue.getKey());
        viewHolder.mTextViewStatValue.setText(childNameAndValue.getValue());

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition){return mListStatNamesAndValues.get(groupPosition).size();}

    @Override
    public Object getGroup(int groupPosition){return mListStatHeaders.get(groupPosition);}

    @Override
    public int getGroupCount(){return mListStatHeaders.size();}

    @Override
    public long getGroupId(int groupPosition){return groupPosition;}

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        String headerTitle = getGroup(groupPosition).toString();

        HeaderViewHolder viewHolder;
        if (convertView == null)
        {
            viewHolder = new HeaderViewHolder();
            convertView = View.inflate(mContext, R.layout.list_stats_header, null);
            viewHolder.mTextViewHeader = (TextView)convertView.findViewById(R.id.tv_stat_header);
            viewHolder.mBackgroundView = convertView.findViewById(R.id.ll_stats_header);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (HeaderViewHolder)convertView.getTag();
        }

        viewHolder.mTextViewHeader.setText(headerTitle);
        viewHolder.mTextViewHeader.setTypeface(null, Typeface.BOLD);
        viewHolder.mBackgroundView.setBackgroundColor(Theme.getSecondaryThemeColor());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {return false;}

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition){return false;}

    private static class StatViewHolder
    {
        private TextView mTextViewStatName;
        private TextView mTextViewStatValue;
    }

    private static class HeaderViewHolder
    {
        private TextView mTextViewHeader;
        private View mBackgroundView;
    }

    @Override
    public void updateTheme(){notifyDataSetChanged();}
}
