package ca.josephroque.bowlingcompanion.statistics.graph

import com.github.mikephil.charting.data.Entry

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Wrapper which provides the data to render a line on a statistics graph.
 */
data class StatisticsGraphLine(val label: String, val entries: List<Entry>)
