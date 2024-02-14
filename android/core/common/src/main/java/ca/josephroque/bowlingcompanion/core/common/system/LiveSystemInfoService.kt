package ca.josephroque.bowlingcompanion.core.common.system

import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LiveSystemInfoService @Inject constructor(
	@ApplicationContext private val context: Context,
) : SystemInfoService {
	override val versionName: String
		get() = context.packageManager.getPackageInfo(context.packageName, 0).versionName

	override val versionCode: String
		get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode.toString()
		} else {
			@Suppress("DEPRECATION")
			context.packageManager.getPackageInfo(context.packageName, 0).versionCode.toString()
		}
}
