package ca.josephroque.bowlingcompanion.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.statistics.provider.StatisticsUnit
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment which displays the stats of a [StatisticsUnit] in a list.
 */
class StatisticsListFragment : ListFragment<StatisticListItem, StatisticsRecyclerViewAdapter>() {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "StatisticsListFragment"

        /** Identifier for the unit to display statistics for. */
        private const val ARG_UNIT = "${TAG}_unit"

        /**
         * Creates a new instance.
         *
         * @param unit unit to display stats for
         * @return the new instance
         */
        fun newInstance(unit: StatisticsUnit): StatisticsListFragment {
            return StatisticsListFragment().apply {
                arguments = Bundle().apply { putParcelable(ARG_UNIT, unit) }
            }
        }
    }

    /** The unit whose statistics are to be displayed. */
    private var unit: StatisticsUnit? = null

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        unit = arguments?.getParcelable(ARG_UNIT)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /** @Override */
    override fun buildAdapter(): StatisticsRecyclerViewAdapter {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    /** @Override */
    override fun fetchItems(): Deferred<MutableList<StatisticListItem>> {
        return async(CommonPool) {
            unit?.statisticListItems?.toMutableList() ?: emptyList<StatisticListItem>().toMutableList()
        }
    }
}

/** An item to display in the list of statistics. */
interface StatisticListItem : IIdentifiable
