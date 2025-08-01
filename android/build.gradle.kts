import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    alias(libs.plugins.googleServices)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
}
val releaseEnabled = file("./release.jks").exists()

val properties = Properties()
try {
    properties.load(project.rootProject.file("local.properties").bufferedReader())
} catch (e: Exception) {
}

val releasePassword = properties.getProperty("releasePassword", "")

android {
    namespace = "com.droidcon.app"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.droidcon.app"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 10003
        versionName = "1.0.3"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packaging {
        resources.excludes.add("META-INF/*.kotlin_module")
    }
    if (releaseEnabled) {
        signingConfigs {
            create("release") {
                keyAlias = "key0"
                keyPassword = releasePassword
                storeFile = file("./release.jks")
                storePassword = releasePassword
            }
        }
    }
    buildTypes {
        if (releaseEnabled) {
            getByName("release") {
                isMinifyEnabled = false
                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        // Flag to enable support for the new language APIs
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    lint {
        warningsAsErrors = false
        abortOnError = false
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

dependencies {
    implementation(projects.shared)
    implementation(projects.sharedUi)

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.coil.compose)
    implementation(libs.accompanist.navigationAnimation)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)

    implementation(libs.hyperdrive.multiplatformx.api)

    implementation(libs.bundles.androidx.compose)

    coreLibraryDesugaring(libs.android.desugar)
}

// Function that copies `mock` JSON config file for google-services if there isn't one available
// Google-services plugin requires this config file to build
val googleServices = file("google-services.json")
if (!googleServices.exists()) {
    file("mock-google-services.json").copyTo(googleServices, overwrite = false)
}
