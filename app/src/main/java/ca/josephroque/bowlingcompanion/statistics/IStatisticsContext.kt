package ca.josephroque.bowlingcompanion.statistics

import ca.josephroque.bowlingcompanion.statistics.provider.StatisticsProvider

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Provides context for the statistics to be displayed.
 */
interface IStatisticsContext {

    /** A [StatisticsProvider] created by the context. */
    val statisticsProviders: Array<StatisticsProvider>
}
