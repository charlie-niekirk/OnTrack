plugins {
    alias(libs.plugins.ontrack.android.library)
}

android {
    namespace = "me.cniekirk.ontrack.core.data"
}

dependencies {
    implementation(project(":core:database"))
    implementation(project(":core:domain"))
    implementation(project(":core:network"))

    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.com.michael.bull.kotlin.result)
}