package ca.josephroque.bowlingcompanion.feature.resourcepicker.data

import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourceItem
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class GamePickerDataProvider @Inject constructor(
	private val gamesRepository: GamesRepository,
	private val seriesId: SeriesID?,
) : ResourcePickerDataProvider {
	override suspend fun loadResources(): List<ResourceItem> = if (seriesId == null) {
		emptyList()
	} else {
		gamesRepository.getGamesList(seriesId)
			.map { leagues -> leagues.map { ResourceItem.Game(it.id, it.index, it.score) } }
			.first()
	}
}
