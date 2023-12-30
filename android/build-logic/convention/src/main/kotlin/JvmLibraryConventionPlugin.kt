import ca.josephroque.bowlingcompanion.configureKotlinJvm
import ca.josephroque.bowlingcompanion.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class JvmLibraryConventionPlugin: Plugin<Project> {
	override fun apply(target: Project) {
		with(target) {
			with(pluginManager) {
				apply("org.jetbrains.kotlin.jvm")
			}

			dependencies {
				add("testImplementation", libs.findLibrary("junit4").get())
				add("testImplementation", kotlin("test"))
			}

			configureKotlinJvm()
		}
	}
}
