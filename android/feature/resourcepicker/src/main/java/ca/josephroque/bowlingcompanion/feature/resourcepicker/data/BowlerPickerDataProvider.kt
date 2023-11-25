package ca.josephroque.bowlingcompanion.feature.resourcepicker.data

import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourceItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BowlerPickerDataProvider @Inject constructor(
	private val bowlersRepository: BowlersRepository,
): ResourcePickerDataProvider {
	override suspend fun loadResources(): List<ResourceItem> =
		bowlersRepository.getBowlersList()
			.map { bowlers -> bowlers.map { ResourceItem(it.id, it.name) } }
			.first()
}