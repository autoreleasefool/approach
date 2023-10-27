import ca.josephroque.bowlingcompanion.libs
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
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
				add("implementation", project(":core:model"))

				add("testImplementation", kotlin("test"))
				add("androidTestImplementation", kotlin("test"))

				add("implementation", libs.findLibrary("androidx.hilt.navigation.compose").get())
				add("implementation", libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
				add("implementation", libs.findLibrary("androidx.lifecycle.viewModelCompose").get())

				add("implementation", libs.findLibrary("kotlinx.coroutines.android").get())
			}
		}
	}
}
