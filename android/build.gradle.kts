// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
	repositories {
		google()
		mavenCentral()
	}
	dependencies {
		classpath(libs.ruler.gradle.plugin)
	}
}

plugins {
	alias(libs.plugins.android.application) apply false
	alias(libs.plugins.compose.compiler) apply false
	alias(libs.plugins.kotlin.jvm) apply false
	alias(libs.plugins.kotlin.parcelize) apply false
	alias(libs.plugins.hilt) apply false
	alias(libs.plugins.ksp) apply false
	alias(libs.plugins.secrets) apply false
	id("com.google.protobuf") version "0.9.4" apply false
	alias(libs.plugins.ktlint) apply false
}

allprojects {
	apply(plugin = "org.jlleitschuh.gradle.ktlint")
}
