package ca.josephroque.bowlingcompanion.feature.archives.ui

import ca.josephroque.bowlingcompanion.core.model.BowlerID
import ca.josephroque.bowlingcompanion.core.model.GameID
import ca.josephroque.bowlingcompanion.core.model.GameScoringMethod
import ca.josephroque.bowlingcompanion.core.model.LeagueID
import ca.josephroque.bowlingcompanion.core.model.SeriesID
import ca.josephroque.bowlingcompanion.core.model.TeamSeriesID
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
		val leagueId: LeagueID,
		val name: String,
		val bowlerName: String,
		val numberOfSeries: Int,
		val numberOfGames: Int,
		override val archivedOn: Instant,
	) : ArchiveListItem {
		override val id: UUID
			get() = leagueId.value
	}

	data class Series(
		val seriesId: SeriesID,
		val date: LocalDate,
		val bowlerName: String,
		val leagueName: String,
		val numberOfGames: Int,
		override val archivedOn: Instant,
	) : ArchiveListItem {
		override val id: UUID
			get() = seriesId.value
	}

	data class Game(
		val gameId: GameID,
		val scoringMethod: GameScoringMethod,
		val score: Int,
		val bowlerName: String,
		val leagueName: String,
		val seriesDate: LocalDate,
		override val archivedOn: Instant,
	) : ArchiveListItem {
		override val id: UUID
			get() = gameId.value
	}

	data class TeamSeries(
		val teamSeriesId: TeamSeriesID,
		val date: LocalDate,
		val teamName: String,
		override val archivedOn: Instant,
	) : ArchiveListItem {
		override val id: UUID
			get() = teamSeriesId.value
	}
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
