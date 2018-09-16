package ca.josephroque.bowlingcompanion.statistics.provider

import android.content.Context
import android.os.Bundle
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.common.fragments.ListFragment
import ca.josephroque.bowlingcompanion.common.interfaces.IIdentifiable
import ca.josephroque.bowlingcompanion.statistics.unit.StatisticsUnitTabbedFragment
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * A fragment representing a list of [StatisticsProvider]s.
 */
class StatisticsProviderListFragment : ListFragment<StatisticsProvider,
        StatisticsProviderRecyclerViewAdapter>(),
        ListFragment.ListFragmentDelegate {

    companion object {
        @Suppress("unused")
        private const val TAG = "SPListFragment"

        private const val ARG_STATISTIC_PROVIDERS_COUNT = "${TAG}_count"
        private const val ARG_STATISTIC_PROVIDERS_TYPE = "${TAG}_type"
        private const val ARG_STATISTIC_PROVIDER = "${TAG}_providers"

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

    override val emptyViewImage = R.drawable.empty_view_statistics
    override val emptyViewText = R.string.empty_view_statistics_providers

    private lateinit var statisticsProviders: List<StatisticsProvider>

    // MARK: Lifecycle functions

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

    override fun onAttach(context: Context?) {
        canIgnoreDelegate = true
        delegate = this
        super.onAttach(context)
    }

    // MARK: BaseFragment

    override fun updateToolbarTitle() {
        navigationActivity?.setToolbarTitle(resources.getString(R.string.statistics))
    }

    // MARK: ListFragment

    override fun buildAdapter(): StatisticsProviderRecyclerViewAdapter {
        return StatisticsProviderRecyclerViewAdapter(emptyList(), this)
    }

    override fun fetchItems(): Deferred<MutableList<StatisticsProvider>> {
        return async(CommonPool) {
            statisticsProviders.toMutableList()
        }
    }

    // MARK: ListFragmentDelegate

    override fun onItemSelected(item: IIdentifiable, longPress: Boolean) {
        if (item is StatisticsProvider) {
            val newFragment = StatisticsUnitTabbedFragment.newInstance(item)
            fragmentNavigation?.pushFragment(newFragment)
        }
    }
}
