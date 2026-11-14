import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Pure Kotlin/JVM module: no Android dependencies, so its tests run in
// seconds on CI. Holds language rules, prediction and autocorrect.
plugins {
    alias(libs.plugins.kotlin.jvm)
}

val jvmLevel = libs.versions.jvm.get()

java {
    sourceCompatibility = JavaVersion.toVersion(jvmLevel)
    targetCompatibility = JavaVersion.toVersion(jvmLevel)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(jvmLevel)
    }
}
