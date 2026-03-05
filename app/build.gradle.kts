import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.devtools.ksp") version "2.3.2"  // Match Kotlin version
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.10"
}

android {
    compileSdk = 36
    namespace = "com.luminaapps.taigamobile"

    defaultConfig {
        applicationId = namespace!!
        minSdk = 23
        targetSdk = 36
        versionCode = 32
        versionName = "2.1"
        project.base.archivesName.set("TaigaMobile-$versionName")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val properties = Properties().also {
                it.load(file("./signing.properties").inputStream())
            }
            storeFile = file("play-store_release-key.keystore")
            storePassword = properties.getProperty("storePassword")
            keyAlias = properties.getProperty("keyAlias")
            keyPassword = properties.getProperty("keyPassword")
        }
    }


    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }

        getByName("release") {
            ndk {
                debugSymbolLevel = "FULL"
            }
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    testOptions.unitTests {
        isIncludeAndroidResources = true
    }

    sourceSets {
        getByName("test") {
            java.directories.add("src/sharedTest/kotlin")
            resources.directories.add("src/sharedTest/resources")
        }
        getByName("androidTest") {
            java.directories.add("src/sharedTest/kotlin")
            resources.directories.add("src/sharedTest/resources")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    // These are false-positive IDE warnings, both kotlin { } and composeCompiler { } resolve correctly
    // via the Project receiver even when  written inside android { }, and they cannot be moved outside
    // without breaking the build because:
    // 1. kotlin { } — Inside android { }, AGP intercepts this to configure the Kotlin Android compilation.
    // Moving it to top level changes which extension is resolved, breaking the Compose compiler flag
    // application.
    // 2. composeCompiler { } — Same issue; it needs the AGP context to properly configure the Compose compiler.

    //noinspection WrongGradleMethod
    kotlin {
        jvmToolchain(25)
        compilerOptions {
            freeCompilerArgs.add("-Xannotation-default-target=first-only")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    lint { 
        abortOnError = false
    }

    //noinspection WrongGradleMethod
    composeCompiler {
        includeComposeMappingFile.set(false) // Workaround for issue #463961757
    }
}

dependencies {
    // Enforce correct kotlin version for all dependencies
    implementation(enforcedPlatform(kotlin("bom")))
    // Compose BOM — manages all androidx.compose.* versions consistently
    // Do not update version.
    // See open issue https://github.com/LuminaAppsDev/TaigaMobile/issues/1
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    implementation(kotlin("reflect"))

    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")

    // Main Compose dependencies (versions managed by BOM)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.animation:animation")
    // compose activity
    implementation("androidx.activity:activity-compose:1.12.4")
    // view model support
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    // compose constraint layout
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")

    // Material2 — still needed for Scaffold/Snackbar/ModalBottomSheet/Divider/etc. (step 3 migration)
    implementation("androidx.compose.material:material")

    // Coil
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Navigation Component (with Compose)
    implementation("androidx.navigation:navigation-compose:2.9.7")

    // Paging (with Compose)
    implementation("androidx.paging:paging-compose:3.4.1")

    // Coroutines
    val coroutinesVersion = "1.10.2"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")

    // Moshi
    val moshiVersion = "1.15.2"
    implementation("com.squareup.moshi:moshi:$moshiVersion")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

    // Retrofit 2
    val retrofitVersion = "3.0.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")

    // OkHttp
    val okHttpVersion = "5.3.2"
    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okHttpVersion")

    // Dagger 2
    val daggerVersion = "2.59.2"
    implementation("com.google.dagger:dagger:$daggerVersion")
    ksp("com.google.dagger:dagger-compiler:$daggerVersion")

    // Timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Markdown support (Markwon)
    val markwonVersion = "4.6.2"
    implementation("io.noties.markwon:core:$markwonVersion")
    implementation("io.noties.markwon:image:$markwonVersion")


    /**
     * Test frameworks & dependencies
     */
    allTestsImplementation(kotlin("test-junit"))

    // Robolectric (run android tests on local host)
    testRuntimeOnly("org.robolectric:robolectric:4.16.1")

    allTestsImplementation("androidx.test:core-ktx:1.7.0")
    allTestsImplementation("androidx.test:runner:1.7.0")
    allTestsImplementation("androidx.test.ext:junit-ktx:1.3.0")

    // since we need to connect to test db instance
    val postgresDriverVersion = "42.7.10"
    testRuntimeOnly("org.postgresql:postgresql:$postgresDriverVersion")
    androidTestRuntimeOnly("org.postgresql:postgresql:$postgresDriverVersion")

    // manual JSON parsing when filling test instance
    implementation("com.google.code.gson:gson:2.13.2")

    // MockK
    testImplementation("io.mockk:mockk:1.14.9")
}

fun DependencyHandler.allTestsImplementation(dependencyNotation: Any) {
    testImplementation(dependencyNotation)
    androidTestImplementation(dependencyNotation)
}

tasks.register<Exec>("launchTestInstance") {
    commandLine("../taiga-test-instance/launch-taiga.sh")
}

tasks.register<Exec>("stopTestInstance") {
    commandLine("../taiga-test-instance/stop-taiga.sh")
}

tasks.withType<Test> {
    dependsOn("launchTestInstance")
    finalizedBy("stopTestInstance")
}
