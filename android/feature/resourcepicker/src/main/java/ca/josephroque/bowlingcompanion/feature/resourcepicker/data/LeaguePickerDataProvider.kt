package ca.josephroque.bowlingcompanion.feature.resourcepicker.data

import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourceItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class LeaguePickerDataProvider @Inject constructor(
	private val leaguesRepository: LeaguesRepository,
	filter: String,
): ResourcePickerDataProvider {
	private val _bowlerId: UUID?
	private val _recurrence: LeagueRecurrence?

	init {
		val parts = filter.split(":")
		if (parts.isEmpty()) {
			_bowlerId = null
			_recurrence = null
		} else {
			_bowlerId = if (parts[0].isNotBlank()) UUID.fromString(parts[0]) else null
			_recurrence = if (parts.size > 1) LeagueRecurrence.valueOf(parts[1]) else null
		}
	}

	override suspend fun loadResources(): List<ResourceItem> = if (_bowlerId == null) {
		emptyList()
	} else {
		leaguesRepository.getLeaguesList(_bowlerId, _recurrence)
			.map { leagues -> leagues.map { ResourceItem.League(it.id, it.name) } }
			.first()
	}
}