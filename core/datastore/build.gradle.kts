plugins {
    alias(libs.plugins.ontrack.android.library)
    alias(libs.plugins.com.google.protobuf)
}

android {
    namespace = "me.cniekirk.ontrack.core.datastore"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(project(":core:di"))
    implementation(project(":core:domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore)
    implementation(libs.com.google.protobuf.javalite)

    testImplementation(libs.org.jetbrains.kotlinx.coroutines.test)
}

protobuf {
    protoc {
        artifact = libs.com.google.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
            }
        }
    }
}