plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.kboard.thekami.ime"
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
    implementation(project(":engine"))
    implementation(project(":data"))
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    testImplementation(libs.junit)
}
