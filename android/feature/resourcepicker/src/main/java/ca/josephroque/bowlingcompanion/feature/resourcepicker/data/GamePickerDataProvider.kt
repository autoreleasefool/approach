package ca.josephroque.bowlingcompanion.feature.resourcepicker.data

import ca.josephroque.bowlingcompanion.core.data.repository.GamesRepository
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourceItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class GamePickerDataProvider @Inject constructor(
	private val gamesRepository: GamesRepository,
	private val seriesId: UUID?,
): ResourcePickerDataProvider {
	override suspend fun loadResources(): List<ResourceItem> = if (seriesId == null) {
		emptyList()
	} else {
		gamesRepository.getGamesList(seriesId)
			.map { leagues -> leagues.map { ResourceItem.Game(it.id, it.index, it.score) } }
			.first()
	}
}