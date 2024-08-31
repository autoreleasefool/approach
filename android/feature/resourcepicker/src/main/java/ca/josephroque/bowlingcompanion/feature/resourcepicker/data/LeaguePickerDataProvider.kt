package ca.josephroque.bowlingcompanion.feature.resourcepicker.data

import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.feature.resourcepicker.ui.ResourceItem
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LeaguePickerDataProvider @Inject constructor(
	private val leaguesRepository: LeaguesRepository,
	filter: String,
) : ResourcePickerDataProvider {
	private val bowlerId: BowlerID?
	private val recurrence: LeagueRecurrence?

	init {
		val parts = filter.split(":")
		if (parts.isEmpty()) {
			bowlerId = null
			recurrence = null
		} else {
			bowlerId = if (parts[0].isNotBlank()) BowlerID.fromString(parts[0]) else null
			recurrence = if (parts.size > 1) LeagueRecurrence.valueOf(parts[1]) else null
		}
	}

	override suspend fun loadResources(): List<ResourceItem> = if (bowlerId == null) {
		emptyList()
	} else {
		leaguesRepository.getLeaguesList(bowlerId, recurrence)
			.map { leagues -> leagues.map { ResourceItem.League(it.id, it.name) } }
			.first()
	}
}
