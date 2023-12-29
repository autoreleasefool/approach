package ca.josephroque.bowlingcompanion.feature.resourcepicker.data

import ca.josephroque.bowlingcompanion.core.data.repository.GearRepository
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourceItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GearPickerDataProvider @Inject constructor(
	private val gearRepository: GearRepository,
	private val kind: GearKind?,
): ResourcePickerDataProvider {
	override suspend fun loadResources(): List<ResourceItem> =
		gearRepository.getGearList(kind = kind)
			.map { gear -> gear.map { ResourceItem.Gear(it.id, it.name, it.kind, it.ownerName, it.avatar) } }
			.first()
}