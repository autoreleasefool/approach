package ca.josephroque.bowlingcompanion.feature.resourcepicker.data

import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourceItem
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class BowlerPickerDataProvider @Inject constructor(
	private val bowlersRepository: BowlersRepository,
) : ResourcePickerDataProvider {
	override suspend fun loadResources(): List<ResourceItem> = bowlersRepository.getBowlersList()
		.map { bowlers -> bowlers.map { ResourceItem.Bowler(it.id, it.name) } }
		.first()
}
