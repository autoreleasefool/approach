package ca.josephroque.bowlingcompanion.core.model.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
fun AlleyMaterial.title(): String = when (this) {
	AlleyMaterial.SYNTHETIC -> stringResource(R.string.alley_property_material_synthetic)
	AlleyMaterial.WOOD -> stringResource(R.string.alley_property_material_wood)
}

@Composable
fun AlleyMechanism.icon(): Painter = when (this) {
	AlleyMechanism.INTERCHANGEABLE -> painterResource(R.drawable.ic_mechanism_interchangeable)
	AlleyMechanism.DEDICATED -> painterResource(R.drawable.ic_mechanism_dedicated)
}

@Composable
fun AlleyMechanism.title(): String = when (this) {
	AlleyMechanism.INTERCHANGEABLE -> stringResource(R.string.alley_property_mechanism_interchangeable)
	AlleyMechanism.DEDICATED -> stringResource(R.string.alley_property_mechanism_dedicated)
}

@Composable
fun AlleyPinBase.icon(): Painter = when (this) {
	AlleyPinBase.BLACK -> painterResource(R.drawable.ic_pin_base_black)
	AlleyPinBase.WHITE -> painterResource(R.drawable.ic_pin_base_white)
	AlleyPinBase.OTHER -> painterResource(R.drawable.ic_pin_base_other)
}

@Composable
fun AlleyPinBase.title(): String = when (this) {
	AlleyPinBase.BLACK -> stringResource(R.string.alley_property_pin_base_black)
	AlleyPinBase.WHITE -> stringResource(R.string.alley_property_pin_base_white)
	AlleyPinBase.OTHER -> stringResource(R.string.alley_property_pin_base_other)
}

@Composable
fun AlleyPinFall.icon(): Painter = when (this) {
	AlleyPinFall.STRINGS -> painterResource(R.drawable.ic_pin_fall_strings)
	AlleyPinFall.FREE_FALL -> painterResource(R.drawable.ic_pin_fall_freefall)
}

@Composable
fun AlleyPinFall.title(): String = when (this) {
	AlleyPinFall.STRINGS -> stringResource(R.string.alley_property_pin_fall_strings)
	AlleyPinFall.FREE_FALL -> stringResource(R.string.alley_property_pin_fall_freefall)
}
