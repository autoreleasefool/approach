package ca.josephroque.bowlingcompanion.feature.leagueform

import ca.josephroque.bowlingcompanion.core.model.LeagueUpdate
import ca.josephroque.bowlingcompanion.feature.leagueform.ui.IncludeAdditionalPinFall
import ca.josephroque.bowlingcompanion.feature.leagueform.ui.LeagueFormTopBarUiState
import ca.josephroque.bowlingcompanion.feature.leagueform.ui.LeagueFormUiAction
import ca.josephroque.bowlingcompanion.feature.leagueform.ui.LeagueFormUiState
import java.util.UUID

sealed interface LeagueFormScreenUiState {
	data object Loading: LeagueFormScreenUiState

	data class Create(
		val form: LeagueFormUiState,
		val topBar: LeagueFormTopBarUiState,
	): LeagueFormScreenUiState {
		fun isSavable(): Boolean =
			form.name.isNotBlank()
	}

	data class Edit(
		val initialValue: LeagueUpdate,
		val form: LeagueFormUiState,
		val topBar: LeagueFormTopBarUiState,
	): LeagueFormScreenUiState {
		fun isSavable(): Boolean =
			form.name.isNotBlank() && form.update(id = initialValue.id) != initialValue
	}
}

fun LeagueFormUiState.update(id: UUID): LeagueUpdate = LeagueUpdate(
	id = id,
	name = name,
	additionalGames = when (includeAdditionalPinFall) {
		IncludeAdditionalPinFall.INCLUDE -> if (additionalGames > 0) additionalGames else null
		IncludeAdditionalPinFall.NONE -> null
	},
	additionalPinFall = when (includeAdditionalPinFall) {
		IncludeAdditionalPinFall.INCLUDE -> if (additionalGames > 0) additionalPinFall else null
		IncludeAdditionalPinFall.NONE -> null
	},
	excludeFromStatistics = excludeFromStatistics,
)

sealed interface LeagueFormScreenUiAction {
	data object LoadLeague: LeagueFormScreenUiAction
	data class LeagueForm(
		val action: LeagueFormUiAction,
	): LeagueFormScreenUiAction
}

sealed interface LeagueFormScreenEvent {
	data object Dismissed: LeagueFormScreenEvent
}