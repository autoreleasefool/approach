package ca.josephroque.bowlingcompanion.statistics.provider

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Provides context for the statistics to be displayed.
 */
interface IStatisticsContext {

    /** [StatisticsProvider]s created by the context. */
    val statisticsProviders: List<StatisticsProvider>
}
