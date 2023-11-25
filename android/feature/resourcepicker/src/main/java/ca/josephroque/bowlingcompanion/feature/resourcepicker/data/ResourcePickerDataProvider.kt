package ca.josephroque.bowlingcompanion.feature.resourcepicker.data

import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourceItem

interface ResourcePickerDataProvider {
	suspend fun loadResources(): List<ResourceItem>
}
