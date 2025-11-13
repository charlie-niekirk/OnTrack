import me.cniekirk.ontrack.testImplementation

plugins {
    alias(libs.plugins.ontrack.android.library)
}

android {
    namespace = "me.cniekirk.ontrack.core.data"
}

dependencies {
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:domain"))
    implementation(project(":core:network"))
    implementation(project(":core:platform"))

    implementation(libs.com.squareup.okhttp3)
    implementation(libs.com.squareup.retrofit2)
    implementation(libs.org.jetbrains.kotlinx.serialization.json)

    implementation(libs.com.google.protobuf.javalite)

    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.com.michael.bull.kotlin.result)

    testImplementation(libs.org.jetbrains.kotlinx.coroutines.test)
    testImplementation(libs.junit)
    testImplementation(libs.io.mockk)
}