package ca.josephroque.bowlingcompanion.feature.resourcepicker.data

import ca.josephroque.bowlingcompanion.core.data.repository.SeriesRepository
import ca.josephroque.bowlingcompanion.core.model.SeriesSortOrder
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourceItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class SeriesPickerDataProvider @Inject constructor(
	private val seriesRepository: SeriesRepository,
	private val leagueId: UUID?,
): ResourcePickerDataProvider {
	override suspend fun loadResources(): List<ResourceItem> = if (leagueId == null) {
		emptyList()
	} else {
		seriesRepository.getSeriesList(leagueId, SeriesSortOrder.NEWEST_TO_OLDEST)
			.map { series -> series.map { ResourceItem.Series(it.properties.id, it.properties.date, it.properties.total) } }
			.first()
	}
}