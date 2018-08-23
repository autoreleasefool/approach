package ca.josephroque.bowlingcompanion.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.josephroque.bowlingcompanion.common.fragments.BaseFragment
import ca.josephroque.bowlingcompanion.statistics.provider.StatisticsProvider
import kotlinx.android.synthetic.main.fragment_common_tabs.tabbed_fragment_tabs as fragmentTabs

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Display the user's statistics.
 */
class BaseStatisticsFragment : BaseFragment() {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "BaseStatisticsFragment"

        /** Argument identifier for the number of [StatisticsProvider]s that are present. */
        private const val ARG_STATISTICS_PROVIDER_COUNT = "${TAG}_count"

        /** Argument identifier for passing an array of [StatisticsProvider] type. */
        private const val ARG_STATISTICS_PROVIDER_TYPE = "${TAG}_type"

        /** Argument identifier for passing an array of [StatisticsProvider] to this fragment. */
        private const val ARG_STATISTICS_PROVIDER = "${TAG}_stats"

        /**
         * Creates a new instance.
         *
         * @return the new instance
         */
        fun newInstance(): BaseStatisticsFragment {
            return BaseStatisticsFragment()
        }

        /**
         * Build the bundle to provide as arguments for this fragment.
         *
         * @param statisticsProviders the statistics providers
         * @return [Bundle] to use for arguments
         */
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

    /** The stats being displayed. */
    private lateinit var statisticsProviders: List<StatisticsProvider>

    /** @Override */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
            // TODO: handle empty list special case
            emptyList()
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /** @Override */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // When there are no other fragments, push the relevant fragment onto the stack
        val isOnlyFragment = (fragmentNavigation?.stackSize ?: 1) > 1
        if (isOnlyFragment) {
            val newFragment = when {
                // TODO: statisticsProviders.size == 0 ->
                statisticsProviders.size == 1 -> StatisticsUnitTabbedFragment.newInstance(statisticsProviders[0])
                else -> TODO("not implemented")
                // TODO: else -> StatisticsProviderListFragment.newInstance(statisticsProviders)
            }

            fragmentNavigation?.pushFragment(newFragment)
        }
    }
}
