package ca.josephroque.bowlingcompanion.feature.resourcepicker.data

import ca.josephroque.bowlingcompanion.core.data.repository.TeamsRepository
import ca.josephroque.bowlingcompanion.core.model.TeamSortOrder
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourceItem
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TeamPickerDataProvider @Inject constructor(
	private val teamsRepository: TeamsRepository,
) : ResourcePickerDataProvider {
	override suspend fun loadResources(): List<ResourceItem> =
		teamsRepository.getTeamList(sortOrder = TeamSortOrder.ALPHABETICAL)
			.map { teams -> teams.map { ResourceItem.Team(it.id, it.name, it.membersList()) } }
			.first()
}
