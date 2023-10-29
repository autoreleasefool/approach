import ca.josephroque.bowlingcompanion.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidFeatureUiConventionPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		with(target) {
			pluginManager.apply {
				apply("approach.android.library")
			}

			dependencies {
				add("implementation", project(":core:designsystem"))
				add("implementation", project(":core:model"))
			}
		}
	}
}
