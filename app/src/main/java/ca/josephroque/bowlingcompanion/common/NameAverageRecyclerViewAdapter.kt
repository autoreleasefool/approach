package ca.josephroque.bowlingcompanion.common

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ca.josephroque.bowlingcompanion.R
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.widget.ImageView
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.teams.Team


/**
 * [RecyclerView.Adapter] that can display a [INameAverage] and makes a call to the
 * specified [OnNameAverageInteractionListener].
 */
class NameAverageRecyclerViewAdapter(private var mValues: List<INameAverage>, private val mListener: OnNameAverageInteractionListener?): RecyclerView.Adapter<NameAverageRecyclerViewAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return mValues.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_name_average, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.mItem = mValues[position]
        holder.mNameView.text = mValues[position].name
        holder.mAverageView.text = mValues[position].getRoundedAverage(1)

        if (holder.mItem is Bowler) {
            holder.mIconView.setImageResource(R.drawable.ic_person_black_24dp)
        } else if (holder.mItem is Team) {
            holder.mIconView.setImageResource(R.drawable.ic_people_black_24dp)
        }

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListPrimary))
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorListAlternate))
        }

        holder.mView.setOnClickListener {
            mListener?.onNAItemClick(holder.mItem!!)
        }
    }

    fun setElements(items: List<INameAverage>) {
        mValues = items
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mNameView: TextView
        val mAverageView: TextView
        val mIconView: ImageView
        var mItem: INameAverage? = null

        init {
            mNameView = mView.findViewById(R.id.tv_name)
            mAverageView = mView.findViewById(R.id.tv_average)
            mIconView = mView.findViewById(R.id.iv_name_average)
        }
    }

    /**
     * Handles interactions with items in the list.
     */
    interface OnNameAverageInteractionListener {
        fun onNAItemClick(item: INameAverage)
        fun onNAItemLongClick(item: INameAverage)
        fun onNAItemDelete(item: INameAverage)
    }

    companion object {
        fun applyDefaultDivider(recyclerView: RecyclerView, context: Context) {
            val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            itemDecorator.setDrawable(ContextCompat.getDrawable(context, R.drawable.divider)!!)
            recyclerView.addItemDecoration(itemDecorator)
        }
    }
}
