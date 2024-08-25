package ca.josephroque.bowlingcompanion.feature.resourcepicker.ui

import androidx.annotation.PluralsRes
import ca.josephroque.bowlingcompanion.core.common.utils.simpleFormat
import ca.josephroque.bowlingcompanion.core.model.AlleyID
import ca.josephroque.bowlingcompanion.core.model.Avatar
import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.GearKind
import ca.josephroque.bowlingcompanion.core.model.LaneID
import ca.josephroque.bowlingcompanion.core.model.LanePosition
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.ResourcePickerType
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import java.util.UUID
import kotlinx.datetime.LocalDate

sealed interface ResourcePickerFilter {
	data class Str(val value: String) : ResourcePickerFilter
	data class League(val id: LeagueID) : ResourcePickerFilter
	data class Series(val id: SeriesID) : ResourcePickerFilter
	data class Gear(val kind: GearKind) : ResourcePickerFilter
	data class Alley(val id: AlleyID) : ResourcePickerFilter
	data class BowlerKind(val kind: ca.josephroque.bowlingcompanion.core.model.BowlerKind) :
		ResourcePickerFilter
}

sealed interface ResourceItem {
	val id: UUID
	val name: String

	data class Bowler(val bowlerId: BowlerID, override val name: String) : ResourceItem {
		override val id: UUID
			get() = bowlerId.value
	}

	data class League(val leagueId: LeagueID, override val name: String) : ResourceItem {
		override val id: UUID
			get() = leagueId.value
	}

	data class Series(val seriesId: SeriesID, val date: LocalDate, val total: Int) : ResourceItem {
		override val id: UUID
			get() = seriesId.value
		override val name: String
			get() = date.simpleFormat()
	}

	data class Game(val gameId: GameID, val index: Int, val score: Int) : ResourceItem {
		override val id: UUID
			get() = gameId.value
		override val name: String
			get() = "Game ${index + 1}"
	}

	data class Gear(
		override val id: UUID,
		override val name: String,
		val kind: GearKind,
		val ownerName: String?,
		val avatar: Avatar,
	) : ResourceItem

	data class Alley(val alleyId: AlleyID, override val name: String) : ResourceItem {
		override val id: UUID
			get() = alleyId.value
	}

	data class Lane(val laneId: LaneID, override val name: String, val position: LanePosition) :
		ResourceItem {
		override val id: UUID
			get() = laneId.value
	}

	data class Team(
		val teamId: TeamID,
		override val name: String,
		val members: List<String>,
	) : ResourceItem {
		override val id: UUID
			get() = teamId.value
	}
}

data class ResourcePickerUiState(
	val resourceType: ResourcePickerType,
	val items: List<ResourceItem>,
	val selectedItems: Set<UUID>,
)

sealed interface ResourcePickerUiAction {
	data object BackClicked : ResourcePickerUiAction
	data object DoneClicked : ResourcePickerUiAction

	data class ItemClicked(val itemId: UUID) : ResourcePickerUiAction
}

data class ResourcePickerTopBarUiState(
	@PluralsRes val titleResourceId: Int = R.plurals.base_picker_title,
	val titleOverride: String? = null,
	val limit: Int = 0,
)
