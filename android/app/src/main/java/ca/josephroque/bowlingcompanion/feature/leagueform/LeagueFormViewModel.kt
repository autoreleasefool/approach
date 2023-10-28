package ca.josephroque.bowlingcompanion.feature.leagueform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.core.data.repository.LeaguesRepository
import ca.josephroque.bowlingcompanion.core.database.model.LeagueCreate
import ca.josephroque.bowlingcompanion.core.database.model.LeagueUpdate
import ca.josephroque.bowlingcompanion.core.common.dispatcher.ApproachDispatchers
import ca.josephroque.bowlingcompanion.core.common.dispatcher.Dispatcher
import ca.josephroque.bowlingcompanion.core.model.ExcludeFromStatistics
import ca.josephroque.bowlingcompanion.core.model.League
import ca.josephroque.bowlingcompanion.core.model.LeagueRecurrence
import ca.josephroque.bowlingcompanion.feature.leagueform.navigation.BOWLER_ID
import ca.josephroque.bowlingcompanion.feature.leagueform.navigation.LEAGUE_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LeagueFormViewModel @Inject constructor(
	private val savedStateHandle: SavedStateHandle,
	private val leaguesRepository: LeaguesRepository,
	@Dispatcher(ApproachDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
): ViewModel() {

	private val _uiState: MutableStateFlow<LeagueFormUiState> = MutableStateFlow(LeagueFormUiState.Loading)
	val uiState: StateFlow<LeagueFormUiState> = _uiState.asStateFlow()

	private val bowlerId = savedStateHandle.get<String>(BOWLER_ID)?.let {
		UUID.fromString(it)
	} ?: UUID.randomUUID()

	fun loadLeague() {
		viewModelScope.launch {
			val leagueId = savedStateHandle.get<UUID?>(LEAGUE_ID)
			if (leagueId == null) {
				_uiState.value = LeagueFormUiState.Create(
					properties = LeagueCreate(
						bowlerId = bowlerId,
						id = UUID.randomUUID(),
						name = "",
						recurrence = LeagueRecurrence.REPEATING,
						excludeFromStatistics = ExcludeFromStatistics.INCLUDE,
						numberOfGames = 4,
						additionalPinFall = 0,
						additionalGames = 0,
					),
					gamesPerSeries = GamesPerSeries.DYNAMIC,
					includeAdditionalPinFall = IncludeAdditionalPinFall.NONE,
					fieldErrors = LeagueFormFieldErrors(),
				)
			} else {
				val league = leaguesRepository.getLeagueDetails(leagueId)
					.first()
				val update = LeagueUpdate(
					id = league.id,
					name = league.name,
					additionalGames = league.additionalGames,
					additionalPinFall = league.additionalPinFall,
					excludeFromStatistics = league.excludeFromStatistics,
				)

				val additionalGames = update.additionalGames

				_uiState.value = LeagueFormUiState.Edit(
					initialValue = update,
					properties = update,
					includeAdditionalPinFall = if ((additionalGames ?: 0) > 0) IncludeAdditionalPinFall.INCLUDE else IncludeAdditionalPinFall.NONE,
					fieldErrors = LeagueFormFieldErrors(),
				)
			}
		}
	}

	fun saveLeague() {
		viewModelScope.launch {
			withContext(ioDispatcher) {
				when (val state = _uiState.value) {
					LeagueFormUiState.Loading, LeagueFormUiState.Dismissed -> Unit
					is LeagueFormUiState.Create ->
						if (state.isSavable()) {
							val stateAdditionalGames = state.properties.additionalGames
							val stateAdditionalPinFall = state.properties.additionalPinFall

							val additionalGames = if (stateAdditionalPinFall != null && (stateAdditionalGames ?: 0) > 0) stateAdditionalGames else null
							val additionalPinFall = if ((additionalGames ?: 0) > 0) stateAdditionalPinFall else null

							leaguesRepository.insertLeague(
								LeagueCreate(
									bowlerId = state.properties.bowlerId,
									id = state.properties.id,
									name = state.properties.name,
									recurrence = state.properties.recurrence,
									numberOfGames = when (state.gamesPerSeries) {
										GamesPerSeries.DYNAMIC -> null
										GamesPerSeries.STATIC -> state.properties.numberOfGames
									},
									additionalPinFall = when (state.includeAdditionalPinFall) {
										IncludeAdditionalPinFall.INCLUDE -> additionalPinFall
										IncludeAdditionalPinFall.NONE -> null
									},
									additionalGames = when (state.includeAdditionalPinFall) {
										IncludeAdditionalPinFall.INCLUDE -> additionalGames
									IncludeAdditionalPinFall.NONE -> null
									},
									excludeFromStatistics = state.properties.excludeFromStatistics,
								)
							)
							_uiState.value = LeagueFormUiState.Dismissed
						} else {
							_uiState.value = state.copy(fieldErrors = state.fieldErrors())
						}
					is LeagueFormUiState.Edit ->
						if (state.isSavable()) {
							val stateAdditionalGames = state.properties.additionalGames
							val stateAdditionalPinFall = state.properties.additionalPinFall

							val additionalGames = if (stateAdditionalPinFall != null && (stateAdditionalGames ?: 0) > 0) stateAdditionalGames else null
							val additionalPinFall = if ((additionalGames ?: 0) > 0) stateAdditionalPinFall else null

							leaguesRepository.updateLeague(
								LeagueUpdate(
									id = state.properties.id,
									name = state.properties.name,
									excludeFromStatistics = state.properties.excludeFromStatistics,
									additionalGames = when (state.includeAdditionalPinFall) {
										IncludeAdditionalPinFall.INCLUDE -> additionalGames
										IncludeAdditionalPinFall.NONE -> null
									},
									additionalPinFall = when (state.includeAdditionalPinFall) {
										IncludeAdditionalPinFall.INCLUDE -> additionalPinFall
										IncludeAdditionalPinFall.NONE -> null
									},
								)
							)
							_uiState.value = LeagueFormUiState.Dismissed
						} else {
							_uiState.value = state.copy(fieldErrors = state.fieldErrors())
						}
				}
			}
		}
	}

	fun deleteLeague() {
		viewModelScope.launch {
			when (val state = _uiState.value) {
				LeagueFormUiState.Loading, LeagueFormUiState.Dismissed, is LeagueFormUiState.Create -> Unit
				is LeagueFormUiState.Edit ->
					leaguesRepository.deleteLeague(state.properties.id)
			}

			_uiState.value = LeagueFormUiState.Dismissed
		}
	}

	fun updateName(name: String) {
		when (val state = _uiState.value) {
			LeagueFormUiState.Loading, LeagueFormUiState.Dismissed -> Unit
			is LeagueFormUiState.Edit -> _uiState.value = state.copy(
				properties = state.properties.copy(name = name),
				fieldErrors = state.fieldErrors.copy(nameErrorId = null),
			)
			is LeagueFormUiState.Create -> _uiState.value = state.copy(
				properties = state.properties.copy(name = name),
				fieldErrors = state.fieldErrors.copy(nameErrorId = null),
			)
		}
	}

	fun updateRecurrence(recurrence: LeagueRecurrence) {
		when (val state = _uiState.value) {
			LeagueFormUiState.Loading, LeagueFormUiState.Dismissed, is LeagueFormUiState.Edit -> Unit
			is LeagueFormUiState.Create -> _uiState.value = state.copy(
				properties = state.properties.copy(recurrence = recurrence),
				gamesPerSeries = when (recurrence) {
					LeagueRecurrence.REPEATING -> state.gamesPerSeries
					LeagueRecurrence.ONCE -> GamesPerSeries.STATIC
				}
			)
		}
	}

	fun updateNumberOfGames(numberOfGames: Int) {
		when (val state = _uiState.value) {
			LeagueFormUiState.Loading, LeagueFormUiState.Dismissed, is LeagueFormUiState.Edit -> Unit
			is LeagueFormUiState.Create -> _uiState.value = state.copy(
				properties = state.properties.copy(
					numberOfGames = max(min(numberOfGames, League.NUMBER_OF_GAMES_RANGE.last), League.NUMBER_OF_GAMES_RANGE.first),
				)
			)
		}
	}

	fun updateGamesPerSeries(gamesPerSeries: GamesPerSeries) {
		when (val state = _uiState.value) {
			LeagueFormUiState.Loading, LeagueFormUiState.Dismissed, is LeagueFormUiState.Edit -> Unit
			is LeagueFormUiState.Create -> _uiState.value = state.copy(
				gamesPerSeries = gamesPerSeries,
			)
		}
	}

	fun updateExcludeFromStatistics(excludeFromStatistics: ExcludeFromStatistics) {
		when (val state = _uiState.value) {
			LeagueFormUiState.Loading, LeagueFormUiState.Dismissed -> Unit
			is LeagueFormUiState.Edit -> _uiState.value = state.copy(
				properties = state.properties.copy(excludeFromStatistics = excludeFromStatistics),
			)
			is LeagueFormUiState.Create -> _uiState.value = state.copy(
				properties = state.properties.copy(excludeFromStatistics = excludeFromStatistics),
			)
		}
	}

	fun updateIncludeAdditionalPinFall(includeAdditionalPinFall: IncludeAdditionalPinFall) {
		when (val state = _uiState.value) {
			LeagueFormUiState.Loading, LeagueFormUiState.Dismissed -> Unit
			is LeagueFormUiState.Edit -> _uiState.value = state.copy(
				includeAdditionalPinFall = includeAdditionalPinFall,
			)
			is LeagueFormUiState.Create -> _uiState.value = state.copy(
				includeAdditionalPinFall = includeAdditionalPinFall,
			)
		}
	}

	fun updateAdditionalPinFall(additionalPinFall: Int) {
		when (val state = _uiState.value) {
			LeagueFormUiState.Loading, LeagueFormUiState.Dismissed -> Unit
			is LeagueFormUiState.Edit -> _uiState.value = state.copy(
				state.properties.copy(additionalPinFall = additionalPinFall),
			)
			is LeagueFormUiState.Create -> _uiState.value = state.copy(
				state.properties.copy(additionalPinFall = additionalPinFall),
			)
		}
	}

	fun updateAdditionalGames(additionalGames: Int) {
		when (val state = _uiState.value) {
			LeagueFormUiState.Loading, LeagueFormUiState.Dismissed -> Unit
			is LeagueFormUiState.Edit -> _uiState.value = state.copy(
				state.properties.copy(additionalGames = additionalGames)
			)
			is LeagueFormUiState.Create -> _uiState.value = state.copy(
				state.properties.copy(additionalGames = additionalGames)
			)
		}
	}
}

sealed interface LeagueFormUiState {
	data object Loading: LeagueFormUiState
	data object Dismissed: LeagueFormUiState

	data class Create(
		val properties: LeagueCreate,
		val gamesPerSeries: GamesPerSeries,
		val includeAdditionalPinFall: IncludeAdditionalPinFall,
		val fieldErrors: LeagueFormFieldErrors,
	): LeagueFormUiState {
		fun isSavable(): Boolean =
			properties.name.isNotBlank()

		fun fieldErrors(): LeagueFormFieldErrors =
			LeagueFormFieldErrors(
				nameErrorId = if (properties.name.isBlank()) R.string.league_form_property_name_missing else null
			)
	}

	data class Edit(
		val initialValue: LeagueUpdate,
		val properties: LeagueUpdate,
		val includeAdditionalPinFall: IncludeAdditionalPinFall,
		val fieldErrors: LeagueFormFieldErrors,
	): LeagueFormUiState {
		fun isSavable(): Boolean =
			properties.name.isNotBlank() && properties != initialValue

		fun fieldErrors(): LeagueFormFieldErrors =
			LeagueFormFieldErrors(
				nameErrorId = if (properties.name.isBlank()) R.string.league_form_property_name_missing else null
			)
	}
}

enum class GamesPerSeries {
	STATIC,
	DYNAMIC,
}

enum class IncludeAdditionalPinFall {
	INCLUDE,
	NONE,
}

data class LeagueFormFieldErrors(
	val nameErrorId: Int? = null
)