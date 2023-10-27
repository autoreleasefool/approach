import ca.josephroque.bowlingcompanion.configureKotlinAndroid
import ca.josephroque.bowlingcompanion.disableUnnecessaryAndroidTests
import ca.josephroque.bowlingcompanion.libs
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidLibraryConventionPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		with(target) {
			with(pluginManager) {
				apply("com.android.library")
				apply("org.jetbrains.kotlin.android")
			}

			extensions.configure<LibraryExtension> {
				configureKotlinAndroid(this)
				defaultConfig.targetSdk = 34
			}
			extensions.configure<LibraryAndroidComponentsExtension> {
				disableUnnecessaryAndroidTests(target)
			}
			configurations.configureEach {
				resolutionStrategy {
					force(libs.findLibrary("junit4").get())
					// Temporary workaround for https://issuetracker.google.com/174733673
					force("org.objenesis:objenesis:2.6")
				}
			}
			dependencies {
				add("androidTestImplementation", kotlin("test"))
				add("testImplementation", kotlin("test"))
			}
		}
	}
}