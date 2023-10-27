// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
	repositories {
		google()
		mavenCentral()
	}
}

plugins {
	alias(libs.plugins.android.application) apply false
	alias(libs.plugins.kotlin.jvm) apply false
	alias(libs.plugins.hilt) apply false
	alias(libs.plugins.ksp) apply false
	alias(libs.plugins.secrets) apply false
	id("com.google.protobuf") version "0.9.4" apply false
}