package ca.josephroque.bowlingcompanion.feature.resourcepicker.data

import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.model.SeriesPreBowl
import ca.josephroque.bowlingcompanion.core.model.SeriesSortOrder
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourceItem
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SeriesPickerDataProvider @Inject constructor(
	private val seriesRepository: SeriesRepository,
	private val filter: String,
) : ResourcePickerDataProvider {
	private val leagueId: UUID?
	private val preBowl: SeriesPreBowl?

	init {
		val parts = filter.split(":")
		if (parts.isEmpty()) {
			leagueId = null
			preBowl = null
		} else {
			leagueId = if (parts[0].isNotBlank()) UUID.fromString(parts[0]) else null
			preBowl = if (parts.size > 1 && parts[1].isNotBlank()) SeriesPreBowl.valueOf(parts[1]) else null
		}
	}

	override suspend fun loadResources(): List<ResourceItem> = if (leagueId == null) {
		emptyList()
	} else {
		seriesRepository.getSeriesList(leagueId, SeriesSortOrder.NEWEST_TO_OLDEST, preBowl)
			.map { series ->
				series.map {
					ResourceItem.Series(it.properties.id, it.properties.date, it.properties.total)
				}
			}
			.first()
	}
}
