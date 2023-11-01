import ca.josephroque.bowlingcompanion.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidFeatureConventionPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		with(target) {
			pluginManager.apply {
				apply("approach.android.library")
				apply("approach.android.hilt")
			}

			dependencies {
				add("implementation", project(":core:analytics"))
				add("implementation", project(":core:common"))
				add("implementation", project(":core:data"))
				add("implementation", project(":core:designsystem"))
				add("implementation", project(":core:model"))

				add("testImplementation", kotlin("test"))
				add("testImplementation", project(":core:testing"))
				add("androidTestImplementation", kotlin("test"))
				add("androidTestImplementation", project(":core:testing"))

				add("implementation", libs.findLibrary("androidx.hilt.navigation.compose").get())
				add("implementation", libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
			}
		}
	}
}
