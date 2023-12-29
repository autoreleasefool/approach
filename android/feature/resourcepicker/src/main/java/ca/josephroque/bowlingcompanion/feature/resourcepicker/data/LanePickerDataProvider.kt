package ca.josephroque.bowlingcompanion.feature.resourcepicker.data

import ca.josephroque.bowlingcompanion.core.data.repository.LanesRepository
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourceItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class LanePickerDataProvider @Inject constructor(
	private val lanesRepository: LanesRepository,
	private val alleyId: UUID?,
): ResourcePickerDataProvider {
	override suspend fun loadResources(): List<ResourceItem> = if (alleyId == null) {
		emptyList()
	} else {
		lanesRepository.getAlleyLanes(alleyId)
			.map { lanes -> lanes.map { ResourceItem.Lane(it.id, it.label, it.position) } }
			.first()
	}
}