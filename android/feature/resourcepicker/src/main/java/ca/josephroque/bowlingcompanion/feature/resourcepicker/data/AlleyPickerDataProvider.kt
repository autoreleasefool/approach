package ca.josephroque.bowlingcompanion.feature.resourcepicker.data

import ca.josephroque.bowlingcompanion.core.data.repository.AlleysRepository
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourceItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlleyPickerDataProvider @Inject constructor(
	private val alleysRepository: AlleysRepository,
): ResourcePickerDataProvider {
	override suspend fun loadResources(): List<ResourceItem> =
		alleysRepository.getAlleysList()
			.map { alleys -> alleys.map { ResourceItem.Alley(it.id, it.name) } }
			.first()
}