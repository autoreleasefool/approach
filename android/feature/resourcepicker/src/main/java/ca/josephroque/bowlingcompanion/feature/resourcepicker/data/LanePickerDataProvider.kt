package ca.josephroque.bowlingcompanion.feature.resourcepicker.data

import ca.josephroque.bowlingcompanion.core.data.repository.LanesRepository
import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourceItem
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LanePickerDataProvider @Inject constructor(
	private val lanesRepository: LanesRepository,
	private val alleyId: AlleyID?,
) : ResourcePickerDataProvider {
	override suspend fun loadResources(): List<ResourceItem> = if (alleyId == null) {
		emptyList()
	} else {
		lanesRepository.getAlleyLanes(alleyId)
			.map { lanes -> lanes.map { ResourceItem.Lane(it.id, it.label, it.position) } }
			.first()
	}
}
