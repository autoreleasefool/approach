package ca.josephroque.bowlingcompanion.core.designsystem.components

import android.content.Context
import android.content.ContextWrapper
import android.graphics.PixelFormat
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.getSystemService
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.findViewTreeSavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

@Composable
fun InvisibleContent(content: @Composable () -> Unit) {
	val context = LocalContext.current
	val windowManager = context.getSystemService<WindowManager>()!!

	DisposableEffect(key1 = content) {
		val composeView = ComposeView(context).apply {
			setParentCompositionContext(null)
			setContent {
				content()
			}
			setOwners(context.findActivity())
		}

		windowManager.addView(
			/* view = */ composeView,
			/* params = */ WindowManager.LayoutParams(
				/* w = */ WindowManager.LayoutParams.WRAP_CONTENT,
				/* h = */ WindowManager.LayoutParams.WRAP_CONTENT,
				/* _type = */ WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
				/* _flags = */ WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				/* _format = */ PixelFormat.TRANSLUCENT
			)
		)

		onDispose { windowManager.removeView(composeView) }
	}
}

private fun View.setOwners(fromActivity: ComponentActivity) {
	if (findViewTreeLifecycleOwner() == null) {
		setViewTreeLifecycleOwner(fromActivity)
	}
	if (findViewTreeViewModelStoreOwner() == null) {
		setViewTreeViewModelStoreOwner(fromActivity)
	}
	if (findViewTreeSavedStateRegistryOwner() == null) {
		setViewTreeSavedStateRegistryOwner(fromActivity)
	}
}

/**
 * Traverses through this [Context] and finds [android.app.Activity] wrapped inside it.
 */
private fun Context.findActivity(): ComponentActivity {
	var context = this
	while (context is ContextWrapper) {
		if (context is ComponentActivity) return context
		context = context.baseContext
	}
	throw IllegalStateException("Unable to retrieve Activity from the current context")
}