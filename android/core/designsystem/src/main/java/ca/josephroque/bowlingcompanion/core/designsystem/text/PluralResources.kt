package ca.josephroque.bowlingcompanion.core.designsystem.text

import androidx.annotation.PluralsRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun quantityStringResource(@PluralsRes id: Int, quantity: Int): String {
	val context = LocalContext.current
	return context.resources.getQuantityString(id, quantity)
}

@Composable
fun quantityStringResource(@PluralsRes id: Int, quantity: Int, vararg formatArgs: Any): String {
	val context = LocalContext.current
	return context.resources.getQuantityString(id, quantity, *formatArgs)
}