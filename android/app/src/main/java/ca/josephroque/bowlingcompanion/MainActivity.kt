package ca.josephroque.bowlingcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import ca.josephroque.bowlingcompanion.ui.ApproachApp
import ca.josephroque.bowlingcompanion.ui.theme.ApproachTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

	private val viewModel: MainActivityViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		var uiState: MainActivityUiState by mutableStateOf(MainActivityUiState.Success)

		lifecycleScope.launch {
			lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
				viewModel.uiState
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
					ApproachApp()
				}
			}
		}
	}
}
