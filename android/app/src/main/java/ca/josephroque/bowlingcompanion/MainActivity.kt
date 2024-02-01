package ca.josephroque.bowlingcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import ca.josephroque.bowlingcompanion.ui.ApproachApp
import ca.josephroque.bowlingcompanion.core.designsystem.theme.ApproachTheme
import ca.josephroque.bowlingcompanion.ui.rememberApproachAppState
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialNavigationApi::class)
@AndroidEntryPoint
class MainActivity: ComponentActivity() {

	private val viewModel: MainActivityViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		var uiState: MainActivityUiState by mutableStateOf(MainActivityUiState.Loading)

		lifecycleScope.launch {
			lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
				viewModel.mainActivityUiState
					.onEach { uiState = it }
					.collect()
			}
		}

		setContent {
			ApproachTheme {
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					LaunchedEffect(uiState.isLaunchComplete()) {
						viewModel.didFirstLaunch()
					}

					val bottomSheetNavigator = rememberBottomSheetNavigator()
					val navController = rememberNavController(bottomSheetNavigator)

					val appState = rememberApproachAppState(
						bottomSheetNavigator = bottomSheetNavigator,
						navController = navController,
					)

					when (val state = uiState) {
						MainActivityUiState.Loading -> Unit
						is MainActivityUiState.Success -> ApproachApp(
							state = state.appState,
							finishActivity = { finish() },
							onTabChanged = viewModel::didChangeTab,
							appState = appState,
						)
					}
				}
			}
		}
	}
}