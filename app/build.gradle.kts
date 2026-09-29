plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }

android {
    namespace = "id.alur.launcher"
    compileSdk = 35
    defaultConfig {
        applicationId = "id.alur.launcher"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.5"
    }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
}
