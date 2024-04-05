package ca.josephroque.bowlingcompanion.feature.onboarding.opponentmigration

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.data.repository.BowlersRepository
import ca.josephroque.bowlingcompanion.core.data.repository.UserDataRepository
import ca.josephroque.bowlingcompanion.core.model.BowlerKind
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.opponentmigration.MergedBowler
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.opponentmigration.OpponentMigrationBottomBarUiAction
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.opponentmigration.OpponentMigrationBottomBarUiState
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.opponentmigration.OpponentMigrationDialogUiState
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.opponentmigration.OpponentMigrationNameDialogUiAction
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.opponentmigration.OpponentMigrationTopBarUiAction
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.opponentmigration.OpponentMigrationUiAction
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.opponentmigration.OpponentMigrationUiState
import ca.josephroque.bowlingcompanion.feature.onboarding.ui.legacyuser.opponentmigration.TooManyBowlersDialogUiAction
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OpponentMigrationViewModel @Inject constructor(
	private val bowlersRepository: BowlersRepository,
	private val userDataRepository: UserDataRepository,
) : ApproachViewModel<OpponentMigrationScreenEvent>() {
	private val _uiState: MutableStateFlow<OpponentMigrationScreenUiState> =
		MutableStateFlow(OpponentMigrationScreenUiState.Loading)

	val uiState = _uiState.asStateFlow()

	private val opponentHistory: MutableList<List<MergedBowler>> = mutableListOf()
	private val mergedOpponentIds: MutableMap<UUID, UUID> = mutableMapOf()

	init {
		viewModelScope.launch {
			userDataRepository.userData.collect {
				if (it.isOpponentMigrationComplete) {
					sendEvent(OpponentMigrationScreenEvent.FinishedMigration)
				}
			}
		}

		viewModelScope.launch {
			val opponents = bowlersRepository
				.getOpponentsList()
				.first()
				.sortedBy { it.name }

			_uiState.update {
				OpponentMigrationScreenUiState.Loaded(
					opponentMigration = OpponentMigrationUiState(
						list = opponents.map {
							MergedBowler(
								id = it.id,
								name = it.name,
								kind = it.kind,
								mergedBowlerNames = emptyList(),
							)
						},
						selected = emptySet(),
					),
					bottomBar = OpponentMigrationBottomBarUiState.StartMigration,
				)
			}
		}
	}
	
	fun handleAction(action: OpponentMigrationScreenUiAction) {
		when (action) {
			is OpponentMigrationScreenUiAction.OpponentMigration -> handleMigrationAction(action.action)
			is OpponentMigrationScreenUiAction.TopBar -> handleTopBarAction(action.action)
			is OpponentMigrationScreenUiAction.BottomBar -> handleBottomBarAction(action.action)
		}
	}
	
	private fun handleMigrationAction(action: OpponentMigrationUiAction) {
		when (action) {
			is OpponentMigrationUiAction.OpponentClicked -> toggleOpponentSelected(action.opponent)
			is OpponentMigrationUiAction.OpponentNameDialog -> handleNameDialogAction(action.action)
			is OpponentMigrationUiAction.TooManyBowlersDialog -> handleTooManyBowlersDialogAction(
				action.action,
			)
		}
	}

	private fun handleNameDialogAction(action: OpponentMigrationNameDialogUiAction) {
		when (action) {
			OpponentMigrationNameDialogUiAction.Dismissed -> dismissOpponentNameDialog()
			OpponentMigrationNameDialogUiAction.Confirmed -> mergeSelectedOpponents()
			is OpponentMigrationNameDialogUiAction.NameChanged -> updateOpponentName(action.name)
		}
	}

	private fun handleTooManyBowlersDialogAction(action: TooManyBowlersDialogUiAction) {
		when (action) {
			TooManyBowlersDialogUiAction.Dismissed -> _uiState.updateLoaded {
				it.copy(opponentMigration = it.opponentMigration.copy(dialog = null))
			}
		}
	}
	
	private fun handleTopBarAction(action: OpponentMigrationTopBarUiAction) {
		when (action) {
			is OpponentMigrationTopBarUiAction.DoneClicked -> migrateOpponents()
		}
	}

	private fun handleBottomBarAction(action: OpponentMigrationBottomBarUiAction) {
		when (action) {
			is OpponentMigrationBottomBarUiAction.MergeClicked -> promptForMergedOpponentName()
			is OpponentMigrationBottomBarUiAction.UndoClicked -> undoMerge()
			OpponentMigrationBottomBarUiAction.StartMigrationClicked -> startMigration()
		}
	}

	private fun startMigration() {
		_uiState.updateLoaded {
			it.copy(
				opponentMigration = it.opponentMigration.copy(isMigrating = true),
				bottomBar = OpponentMigrationBottomBarUiState.Migrating(),
			)
		}
	}

	private fun migrateOpponents() {
		viewModelScope.launch {
			TODO()

			// userData.didCompleteOpponentMigration()
			// analyticsClient.track(AnalyticsEvent.OpponentMigrationCompleted)
			// userData.didCompleteOnboarding()
			// analyticsClient.track(AnalyticsEvent.AppOnboardingCompleted)
		}
	}

	private fun promptForMergedOpponentName() {
		_uiState.updateLoaded { state ->
			if (state.opponentMigration.selected.isEmpty()) {
				return@updateLoaded state
			}

			val selectedOpponents = state.opponentMigration.selected
			val mergedOpponents = state.opponentMigration.list.filter { it.id in selectedOpponents }
			val playableBowlers = mergedOpponents.filter { it.kind == BowlerKind.PLAYABLE }
			if (playableBowlers.size > 1) {
				return@updateLoaded state.copy(
					opponentMigration = state.opponentMigration.copy(
						dialog = OpponentMigrationDialogUiState.TooManyBowlersDialog(
							firstName = playableBowlers.first().name,
							secondName = playableBowlers.last().name,
						),
					),
				)
			}

			state.copy(
				opponentMigration = state.opponentMigration.copy(
					dialog = OpponentMigrationDialogUiState.NameDialog(
						name = state.opponentMigration.list
							.first { state.opponentMigration.selected.contains(it.id) }
							.name,
					),
				),
			)
		}
	}

	private fun dismissOpponentNameDialog() {
		_uiState.updateLoaded {
			it.copy(
				opponentMigration = it.opponentMigration.copy(
					dialog = null,
				),
			)
		}
	}

	private fun updateOpponentName(name: String) {
		_uiState.updateLoaded {
			when (val dialog = it.opponentMigration.dialog) {
				null, is OpponentMigrationDialogUiState.TooManyBowlersDialog -> it
				is OpponentMigrationDialogUiState.NameDialog -> it.copy(
					opponentMigration = it.opponentMigration.copy(
						dialog = dialog.copy(name = name),
					),
				)
			}
		}
	}

	private fun mergeSelectedOpponents() {
		_uiState.updateLoaded { state ->
			val opponentName = (state.opponentMigration.dialog as? OpponentMigrationDialogUiState.NameDialog)
				?.name ?: return@updateLoaded state

			if (state.opponentMigration.selected.isEmpty() || opponentName.isBlank()) {
				return@updateLoaded state
			}

			val selectedOpponents = state.opponentMigration.selected
			val mergedOpponents = state.opponentMigration.list.filter { it.id in selectedOpponents }

			val primaryOpponent = mergedOpponents.firstOrNull { it.kind == BowlerKind.PLAYABLE }
				?: mergedOpponents.first()
			mergedOpponents.forEach {
				mergedOpponentIds[it.id] = primaryOpponent.id
			}

			val mergedBowlerNames = mergedOpponents.flatMap { it.mergedBowlerNames + it.name }
				.toSet()
				.sortedBy { it }

			val mergedOpponent = MergedBowler(
				id = primaryOpponent.id,
				name = opponentName,
				kind = primaryOpponent.kind,
				mergedBowlerNames = mergedBowlerNames,
			)

			val updatedOpponents =
				(state.opponentMigration.list.filter { it.id !in selectedOpponents } + mergedOpponent)
					.sortedBy { it.name }
			opponentHistory.add(state.opponentMigration.list)

			state.copy(
				opponentMigration = state.opponentMigration.copy(
					list = updatedOpponents,
					selected = emptySet(),
					dialog = null,
				),
				bottomBar = OpponentMigrationBottomBarUiState.Migrating(
					isMergeEnabled = false,
					isUndoEnabled = true,
				),
			)
		}

		dismissOpponentNameDialog()
	}

	private fun undoMerge() {
		_uiState.updateLoaded {
			val previousOpponents = opponentHistory.removeLastOrNull()
				?: return@updateLoaded it

			it.copy(
				opponentMigration = it.opponentMigration.copy(
					list = previousOpponents,
					selected = emptySet(),
				),
				bottomBar = (it.bottomBar as? OpponentMigrationBottomBarUiState.Migrating)?.copy(
					isMergeEnabled = false,
					isUndoEnabled = opponentHistory.size > 0,
				) ?: OpponentMigrationBottomBarUiState.Migrating(),
			)
		}
	}

	private fun toggleOpponentSelected(opponent: MergedBowler) {
		_uiState.updateLoaded {
			val selected = if (it.opponentMigration.selected.contains(opponent.id)) {
				it.opponentMigration.selected - opponent.id
			} else {
				it.opponentMigration.selected + opponent.id
			}

			it.copy(
				opponentMigration = it.opponentMigration.copy(
					selected = selected,
				),
				bottomBar = (it.bottomBar as? OpponentMigrationBottomBarUiState.Migrating)?.copy(
					isMergeEnabled = selected.size > 1,
				) ?: OpponentMigrationBottomBarUiState.Migrating(),
			)
		}
	}
}
