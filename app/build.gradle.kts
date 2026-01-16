import com.android.build.api.dsl.AndroidSourceSet
import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.devtools.ksp") version "2.3.2"  // Match Kotlin version
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.0"
}

val composeVersion = "1.1.1"

android {
    compileSdk = 36
    namespace = "com.luminaapps.taigamobile"

    defaultConfig {
        applicationId = namespace!!
        minSdk = 23
        targetSdk = 36
        versionCode = 30
        versionName = "2.0"
        project.base.archivesName.set("TaigaMobile-$versionName")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val properties = Properties().also {
                it.load(file("./signing.properties").inputStream())
            }
            storePassword = properties.getProperty("password")
            keyAlias = properties.getProperty("alias")
            keyPassword = properties.getProperty("password")
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
        fun AndroidSourceSet.setupTestSrcDirs() {
            kotlin.srcDir("src/sharedTest/kotlin")
            resources.srcDir("src/sharedTest/resources")
        }

        getByName("test").setupTestSrcDirs()
        getByName("androidTest").setupTestSrcDirs()
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

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
    composeCompiler {
        includeComposeMappingFile.set(false) // Workaround for issue #463961757
    }
}

dependencies {
    // Enforce correct kotlin version for all dependencies
    implementation(enforcedPlatform(kotlin("bom")))
    implementation("androidx.compose.foundation:foundation-layout:1.10.0")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    implementation(kotlin("reflect"))

    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.6.0")

    // ============================================================================================
    // CAREFUL WHEN UPDATING COMPOSE RELATED DEPENDENCIES - THEY CAN USE DIFFERENT COMPOSE VERSION!
    // ============================================================================================

    // Main Compose dependencies
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    // Material You
    implementation("androidx.compose.material3:material3:1.1.1")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.animation:animation:$composeVersion")
    // compose activity
    implementation("androidx.activity:activity-compose:1.4.0")
    // view model support
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    // compose constraint layout
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")

    // Accompanist
    val accompanistVersion = "0.30.1"
    implementation("com.google.accompanist:accompanist-pager:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-pager-indicators:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-insets:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-flowlayout:$accompanistVersion")

    // Coil
    implementation("io.coil-kt:coil-compose:1.3.2")

    // Navigation Component (with Compose)
    implementation("androidx.navigation:navigation-compose:2.5.0")

    // Paging (with Compose)
    implementation("androidx.paging:paging-compose:1.0.0-alpha20")

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
    val daggerVersion = "2.57.2"
    implementation("com.google.dagger:dagger:$daggerVersion")
    ksp("com.google.dagger:dagger-compiler:$daggerVersion")

    // Timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Markdown support (Markwon)
    val markwonVersion = "4.6.2"
    implementation("io.noties.markwon:core:$markwonVersion")
    implementation("io.noties.markwon:image-coil:$markwonVersion")

    // Compose material dialogs (color picker)
    implementation("io.github.vanpra.compose-material-dialogs:color:0.7.0")

    /**
     * Test frameworks & dependencies
     */
    allTestsImplementation(kotlin("test-junit"))

    // Robolectric (run android tests on local host)
    testRuntimeOnly("org.robolectric:robolectric:4.16")

    allTestsImplementation("androidx.test:core-ktx:1.7.0")
    allTestsImplementation("androidx.test:runner:1.7.0")
    allTestsImplementation("androidx.test.ext:junit-ktx:1.3.0")

    // since we need to connect to test db instance
    val postgresDriverVersion = "42.7.8"
    testRuntimeOnly("org.postgresql:postgresql:$postgresDriverVersion")
    androidTestRuntimeOnly("org.postgresql:postgresql:$postgresDriverVersion")

    // manual json parsing when filling test instance
    implementation("com.google.code.gson:gson:2.13.2")

    // MockK
    testImplementation("io.mockk:mockk:1.14.7")
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

