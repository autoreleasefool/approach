package ca.josephroque.bowlingcompanion.feature.archives.ui

import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import java.util.UUID
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

sealed interface ArchiveListItem {
	val id: UUID
	val archivedOn: Instant

	data class Bowler(
		val bowlerId: BowlerID,
		val name: String,
		val numberOfLeagues: Int,
		val numberOfSeries: Int,
		val numberOfGames: Int,
		override val archivedOn: Instant,
	) : ArchiveListItem {
		override val id: UUID
			get() = bowlerId.value
	}

	data class League(
		override val id: UUID,
		val name: String,
		val bowlerName: String,
		val numberOfSeries: Int,
		val numberOfGames: Int,
		override val archivedOn: Instant,
	) : ArchiveListItem

	data class Series(
		override val id: UUID,
		val date: LocalDate,
		val bowlerName: String,
		val leagueName: String,
		val numberOfGames: Int,
		override val archivedOn: Instant,
	) : ArchiveListItem

	data class Game(
		override val id: UUID,
		val scoringMethod: GameScoringMethod,
		val score: Int,
		val bowlerName: String,
		val leagueName: String,
		val seriesDate: LocalDate,
		override val archivedOn: Instant,
	) : ArchiveListItem
}

data class ArchivesListUiState(
	val list: List<ArchiveListItem>,
	val itemToUnarchive: ArchiveListItem?,
)

sealed interface ArchivesListUiAction {
	data object BackClicked : ArchivesListUiAction

	data class UnarchiveClicked(val item: ArchiveListItem) : ArchivesListUiAction
	data object ConfirmUnarchiveClicked : ArchivesListUiAction
}
