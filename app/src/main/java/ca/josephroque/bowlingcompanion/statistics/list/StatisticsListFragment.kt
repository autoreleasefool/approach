package ca.josephroque.bowlingcompanion.statistics.list

import android.os.Bundle
import android.support.v7.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.statistics.unit.StatisticsUnit
import ca.josephroque.bowlingcompanion.utils.BCError
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
        @Suppress("unused")
        private const val TAG = "StatisticsListFragment"

        private const val ARG_UNIT = "${TAG}_unit"

        private const val TAP_FOR_GRAPH_SHOWN = "${TAG}_tap_for_graph_shown"

        fun newInstance(unit: StatisticsUnit): StatisticsListFragment {
            return StatisticsListFragment().apply {
                arguments = Bundle().apply { putParcelable(ARG_UNIT, unit) }
            }
        }
    }

    override val emptyViewImage = R.drawable.empty_view_statistics
    override val emptyViewText = R.string.empty_view_statistics

    private lateinit var unit: StatisticsUnit

    // MARK: Lifecycle functions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        unit = arguments?.getParcelable(ARG_UNIT)!!
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        val context = context ?: return
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        if (!preferences.getBoolean(TAP_FOR_GRAPH_SHOWN, false)) {
            BCError(
                    title = R.string.did_you_know,
                    message = R.string.tap_to_show_statistics_graph,
                    severity = BCError.Severity.Info
            ).show(context) {
                preferences.edit().putBoolean(TAP_FOR_GRAPH_SHOWN, true).apply()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        unit.clearCache()
    }

    // MARK: BaseFragment

    override fun updateToolbarTitle() {
        // Intentionally left blank
    }

    // MARK: ListFragment

    override fun buildAdapter(): StatisticsRecyclerViewAdapter {
        return StatisticsRecyclerViewAdapter(emptyList(), this)
    }

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
