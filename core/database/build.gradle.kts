plugins {
    alias(libs.plugins.ontrack.android.library)
}

android {
    namespace = "me.cniekirk.ontrack.core.database"
}

dependencies {
    implementation(project(":core:di"))

    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
}