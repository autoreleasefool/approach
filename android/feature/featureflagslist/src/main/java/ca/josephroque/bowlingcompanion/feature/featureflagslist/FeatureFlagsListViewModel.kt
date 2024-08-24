package ca.josephroque.bowlingcompanion.feature.featureflagslist

import androidx.lifecycle.viewModelScope
import ca.josephroque.bowlingcompanion.core.common.viewmodel.ApproachViewModel
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlag
import ca.josephroque.bowlingcompanion.core.featureflags.FeatureFlagsClient
import ca.josephroque.bowlingcompanion.feature.featureflagslist.ui.FeatureFlagState
import ca.josephroque.bowlingcompanion.feature.featureflagslist.ui.FeatureFlagsListUiAction
import ca.josephroque.bowlingcompanion.feature.featureflagslist.ui.FeatureFlagsListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel
class FeatureFlagsListViewModel @Inject constructor(
	private val featureFlagsClient: FeatureFlagsClient,
) : ApproachViewModel<FeatureFlagsListScreenEvent>() {
	private val featureFlags = MutableStateFlow(
		FeatureFlag.entries.map {
			FeatureFlagState(it, featureFlagsClient.isEnabled(it))
		},
	)

	val uiState = featureFlags.map {
		FeatureFlagsListScreenUiState.Loaded(
			featureFlagsList = FeatureFlagsListUiState(
				featureFlags = it,
			),
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = FeatureFlagsListScreenUiState.Loading,
	)

	fun handleAction(action: FeatureFlagsListScreenUiAction) {
		when (action) {
			is FeatureFlagsListScreenUiAction.List -> handleListAction(action.action)
		}
	}

	private fun handleListAction(action: FeatureFlagsListUiAction) {
		when (action) {
			FeatureFlagsListUiAction.BackClicked -> sendEvent(FeatureFlagsListScreenEvent.Dismissed)
			FeatureFlagsListUiAction.ResetOverridesClicked -> resetAllOverrides()
			is FeatureFlagsListUiAction.FeatureFlagToggled ->
				toggleFeatureFlag(action.flag, action.isEnabled)
		}
	}

	private fun resetAllOverrides() {
		featureFlags.update {
			FeatureFlag.entries.forEach { flag ->
				featureFlagsClient.setEnabled(flag, null)
			}

			it.map { state ->
				state.copy(isEnabled = featureFlagsClient.isEnabled(state.flag))
			}
		}
	}

	private fun toggleFeatureFlag(flag: FeatureFlag, isEnabled: Boolean) {
		featureFlags.update {
			featureFlagsClient.setEnabled(flag, isEnabled)

			it.map { state ->
				if (state.flag == flag) {
					state.copy(isEnabled = isEnabled)
				} else {
					state
				}
			}
		}
	}
}
