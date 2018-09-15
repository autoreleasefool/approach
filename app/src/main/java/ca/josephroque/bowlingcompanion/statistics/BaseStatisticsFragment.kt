package ca.josephroque.bowlingcompanion.statistics

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import ca.josephroque.bowlingcompanion.common.fragments.BaseFragment
import ca.josephroque.bowlingcompanion.statistics.provider.StatisticsProvider
import ca.josephroque.bowlingcompanion.statistics.provider.StatisticsProviderListFragment
import ca.josephroque.bowlingcompanion.statistics.unit.StatisticsUnitTabbedFragment

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Display the user's statistics.
 */
class BaseStatisticsFragment : BaseFragment() {

    companion object {
        @Suppress("unused")
        private const val TAG = "BaseStatisticsFragment"

        private const val ARG_STATISTICS_PROVIDER_COUNT = "${TAG}_count"
        private const val ARG_STATISTICS_PROVIDER_TYPE = "${TAG}_type"
        private const val ARG_STATISTICS_PROVIDER = "${TAG}_stats"

        fun newInstance(): BaseStatisticsFragment {
            return BaseStatisticsFragment()
        }

        fun buildArguments(statisticsProviders: List<StatisticsProvider>): Bundle {
            return Bundle().apply {
                putInt(ARG_STATISTICS_PROVIDER_COUNT, statisticsProviders.size)
                putIntArray(ARG_STATISTICS_PROVIDER_TYPE, statisticsProviders.map { it.describeContents() }.toIntArray())
                statisticsProviders.forEachIndexed { index, it ->
                    putParcelable("${ARG_STATISTICS_PROVIDER}_$index", it)
                }
            }
        }
    }

    private lateinit var statisticsProviders: List<StatisticsProvider>

    // MARK: Lifecycle functions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onStart() {
        super.onStart()

        val providerCount = arguments?.getInt(ARG_STATISTICS_PROVIDER_COUNT) ?: 0
        statisticsProviders = if (providerCount > 0) {
            val types = arguments?.getIntArray(ARG_STATISTICS_PROVIDER_TYPE)
            arrayListOf<StatisticsProvider>().apply {
                types!!.forEachIndexed { index, it ->
                    val parcelable = StatisticsProvider.getParcelable(arguments, "${ARG_STATISTICS_PROVIDER}_$index", it)
                    add(parcelable!!)
                }
            }
        } else {
            emptyList()
        }

        // When there are no other fragments, push the relevant fragment onto the stack
        val isOnlyFragment = (fragmentNavigation?.stackSize ?: 1) == 1
        if (isOnlyFragment) {
            val newFragment: BaseFragment = when {
                statisticsProviders.isEmpty() -> TODO("not implemented")
                statisticsProviders.size == 1 -> StatisticsUnitTabbedFragment.newInstance(statisticsProviders[0])
                else -> StatisticsProviderListFragment.newInstance(statisticsProviders)
            }

            fragmentNavigation?.pushFragment(newFragment)
        }
    }

    // MARK: BaseFragment

    override fun updateToolbarTitle() {
        // Intentionally left blank
    }
}
