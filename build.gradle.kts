// Top-level build file where you can add configuration options common to all sub-projects/modules.
// AGP 9's built-in Kotlin support means the org.jetbrains.kotlin.android / kotlin.plugin.compose
// plugins are no longer applied here — AGP compiles Kotlin (and Compose) itself.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}
