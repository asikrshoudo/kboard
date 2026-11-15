plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.kboard.thekami.data"
    compileSdk = libs.versions.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.min.sdk.get().toInt()
    }

    compileOptions {
        val javaVersion = JavaVersion.toVersion(libs.versions.jvm.get())
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}

dependencies {
    // Flow is part of this module's public API.
    api(libs.coroutines.core)
    implementation(libs.datastore.preferences)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
}
