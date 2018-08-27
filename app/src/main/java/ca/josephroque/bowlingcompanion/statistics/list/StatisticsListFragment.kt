package ca.josephroque.bowlingcompanion.statistics.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.statistics.unit.StatisticsUnit
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
    private lateinit var unit: StatisticsUnit

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        unit = arguments?.getParcelable(ARG_UNIT)!!
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /** @Override */
    override fun updateToolbarTitle() {
        // Intentionally left blank
    }

    /** @Override */
    override fun buildAdapter(): StatisticsRecyclerViewAdapter {
        return StatisticsRecyclerViewAdapter(emptyList(), this)
    }

    /** @Override */
    override fun fetchItems(): Deferred<MutableList<StatisticListItem>> {
        context?.let {
            return unit.getStatistics(it)
        }

        return async(CommonPool) {
            mutableListOf<StatisticListItem>()
        }
    }
}

/** An item to display in the list of statistics. */
interface StatisticListItem : IIdentifiable
