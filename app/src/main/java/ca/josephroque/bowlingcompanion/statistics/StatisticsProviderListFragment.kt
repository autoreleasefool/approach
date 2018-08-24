package ca.josephroque.bowlingcompanion.statistics

import android.os.Bundle
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.statistics.provider.StatisticsProvider
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment representing a list of [StatisticsProvider]s.
 */
class StatisticsProviderListFragment : ListFragment<StatisticsProvider, StatisticsProviderRecyclerViewAdapter>() {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
        private const val TAG = "SPListFragment"

        /** Argument identifier for the number of [StatisticsProvider]s. */
        private const val ARG_STATISTIC_PROVIDERS_COUNT = "${TAG}_count"

        /** Argument identifier for the [StatisticsProvider] types. */
        private const val ARG_STATISTIC_PROVIDERS_TYPE = "${TAG}_type"

        /** Argument identifier for the [StatisticsProvider]s to display. */
        private const val ARG_STATISTIC_PROVIDER = "${TAG}_providers"

        /**
         * Creates a new instance.
         *
         * @return the new instance
         */
        fun newInstance(statisticsProviders: List<StatisticsProvider>): StatisticsProviderListFragment {
            val fragment = StatisticsProviderListFragment()
            fragment.arguments = Bundle().apply {
                putInt(ARG_STATISTIC_PROVIDERS_COUNT, statisticsProviders.size)
                statisticsProviders.forEachIndexed { index, provider ->
                    putInt("${ARG_STATISTIC_PROVIDERS_TYPE}_$index", provider.describeContents())
                    putParcelable("${ARG_STATISTIC_PROVIDER}_$index", provider)
                }
            }
            return fragment
        }
    }

    /** List of [StatisticProvider]s to display. */
    private lateinit var statisticsProviders: List<StatisticsProvider>

    /** @Override */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val providers: MutableList<StatisticsProvider> = ArrayList()
        statisticsProviders = providers

        val arguments = arguments ?: return
        val providerCount = arguments.getInt(ARG_STATISTIC_PROVIDERS_COUNT)
        for (i in 0 until providerCount) {
            val type = arguments.getInt("${ARG_STATISTIC_PROVIDERS_TYPE}_$i")
            providers.add(StatisticsProvider.getParcelable(arguments, "${ARG_STATISTIC_PROVIDER}_$i", type)!!)
        }
    }

    /** @Override */
    override fun buildAdapter(): StatisticsProviderRecyclerViewAdapter {
        return StatisticsProviderRecyclerViewAdapter(emptyList(), this)
    }

    /** @Override */
    override fun fetchItems(): Deferred<MutableList<StatisticsProvider>> {
        return async(CommonPool) {
            statisticsProviders.toMutableList()
        }
    }
}
