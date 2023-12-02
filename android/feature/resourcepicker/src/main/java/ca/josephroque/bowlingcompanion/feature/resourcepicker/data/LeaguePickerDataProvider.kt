package ca.josephroque.bowlingcompanion.feature.resourcepicker.data

import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourceItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class LeaguePickerDataProvider @Inject constructor(
	private val leaguesRepository: LeaguesRepository,
	private val bowlerId: UUID,
): ResourcePickerDataProvider {
	override suspend fun loadResources(): List<ResourceItem> =
		leaguesRepository.getLeaguesList(bowlerId)
			.map { leagues -> leagues.map { ResourceItem.League(it.id, it.name) } }
			.first()
}