// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}
buildscript {
    dependencies {
        repositories {
            google()
            mavenCentral()
        }
        dependencies {
            classpath (libs.gradle)
            classpath (libs.google.services)
            classpath (libs.google.services.v4315)
        }
    }
}
allprojects {
    repositories {
        google()
        mavenCentral()
    }
}