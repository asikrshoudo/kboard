plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

// Release signing material comes from the environment (set by CI from
// GitHub Secrets). Nothing secret lives in the repository.
val signingKeystore = providers.environmentVariable("KBOARD_KEYSTORE_PATH")
val signingPassword = providers.environmentVariable("KBOARD_KEYSTORE_PASSWORD")
val signingAlias = providers.environmentVariable("KBOARD_KEY_ALIAS")

android {
    namespace = "com.kboard.thekami"
    compileSdk = libs.versions.compile.sdk.get().toInt()

    defaultConfig {
        applicationId = "com.kboard.thekami"
        minSdk = libs.versions.min.sdk.get().toInt()
        targetSdk = libs.versions.target.sdk.get().toInt()
        // Release CI passes the tag as name and the run number as code
        // (Play Store requires a code that always increases).
        versionCode = providers.gradleProperty("kboard.versionCode").map(String::toInt).getOrElse(1)
        versionName = providers.gradleProperty("kboard.versionName").getOrElse("0.0.0-dev")
    }

    compileOptions {
        val javaVersion = JavaVersion.toVersion(libs.versions.jvm.get())
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    signingConfigs {
        create("release") {
            if (signingKeystore.isPresent) {
                storeFile = file(signingKeystore.get())
                storePassword = signingPassword.get()
                keyAlias = signingAlias.get()
                keyPassword = signingPassword.get()
            }
        }
    }

    buildTypes {
        debug {
            if (signingKeystore.isPresent) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

dependencies {
    implementation(project(":ime"))
    implementation(project(":data"))
    implementation(project(":engine"))
    implementation(libs.coroutines.core)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(rootProject.file("config/detekt/compose.yml"))
}
