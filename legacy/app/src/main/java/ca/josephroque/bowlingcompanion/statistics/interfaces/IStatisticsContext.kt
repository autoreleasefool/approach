package ca.josephroque.bowlingcompanion.statistics.interfaces

import ca.josephroque.bowlingcompanion.statistics.provider.StatisticsProvider

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Provides context for the statistics to be displayed.
 */
interface IStatisticsContext {
    val statisticsProviders: List<StatisticsProvider>
}
