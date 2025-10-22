//import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.ontrack.android.application.compose)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.serialization)
}

android {
    namespace = "me.cniekirk.ontrack"

    defaultConfig {
        applicationId = "me.cniekirk.ontrack"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }
}

//protobuf {
//    protoc {
//        artifact = "com.google.protobuf:protoc:4.32.0"
//    }
//    // Generates the java Protobuf-lite code for the Protobufs in this project. See
//    // https://github.com/google/protobuf-gradle-plugin#customizing-protobuf-compilation
//    // for more information.
//    generateProtoTasks {
//        // see https://github.com/google/protobuf-gradle-plugin/issues/518
//        // see https://github.com/google/protobuf-gradle-plugin/issues/491
//        // all() here because of android multi-variant
//        all().forEach { task ->
//            // this only works on version 3.8+ that has buildins for javalite / kotlin lite
//            // with previous version the java build in is to be removed and a new plugin
//            // need to be declared
//            task.builtins {
//                id("java") { // id is imported above
//                    option("lite")
//                }
//            }
//        }
//    }
//}

dependencies {
    implementation(project(":core:compose"))
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:di"))
    implementation(project(":core:domain"))
    implementation(project(":core:navigation"))
    implementation(project(":core:network"))
    implementation(project(":core:platform"))

    implementation(project(":feature:home"))
    implementation(project(":feature:servicelist"))
    implementation(project(":feature:stationsearch"))

    implementation(libs.org.orbit.mvi.core)
    implementation(libs.org.orbit.mvi.compose)
    implementation(libs.org.orbit.mvi.viewmodel)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.org.jetbrains.kotlinx.serialization.json)

    implementation(libs.com.jakewharton.timber)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}