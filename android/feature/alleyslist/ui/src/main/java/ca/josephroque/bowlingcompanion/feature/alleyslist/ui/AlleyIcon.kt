package ca.josephroque.bowlingcompanion.feature.alleyslist.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import ca.josephroque.bowlingcompanion.core.model.AlleyMaterial
import ca.josephroque.bowlingcompanion.core.model.AlleyMechanism
import ca.josephroque.bowlingcompanion.core.model.AlleyPinBase
import ca.josephroque.bowlingcompanion.core.model.AlleyPinFall

@Composable
fun AlleyMaterial.icon(): Painter = when (this) {
	AlleyMaterial.SYNTHETIC -> painterResource(R.drawable.ic_material_synthetic)
	AlleyMaterial.WOOD -> painterResource(R.drawable.ic_material_wood)
}

@Composable
fun AlleyMechanism.icon(): Painter = when (this) {
	AlleyMechanism.INTERCHANGEABLE -> painterResource(R.drawable.ic_mechanism_interchangeable)
	AlleyMechanism.DEDICATED -> painterResource(R.drawable.ic_mechanism_dedicated)
}

@Composable
fun AlleyPinBase.icon(): Painter = when (this) {
	AlleyPinBase.BLACK -> painterResource(R.drawable.ic_pin_base_black)
	AlleyPinBase.WHITE -> painterResource(R.drawable.ic_pin_base_white)
	AlleyPinBase.OTHER -> painterResource(R.drawable.ic_pin_base_other)
}

@Composable
fun AlleyPinFall.icon(): Painter = when (this) {
	AlleyPinFall.STRINGS -> painterResource(R.drawable.ic_pin_fall_strings)
	AlleyPinFall.FREE_FALL -> painterResource(R.drawable.ic_pin_fall_freefall)
}