plugins { id("com.android.application") }

android {
    namespace = "dev.liquidglass.xposed"
    compileSdk = 35

    defaultConfig {
        applicationId = "dev.liquidglass.xposed"
        minSdk = 31            // window blur APIs need Android 12+
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")   // fine for a personal module
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    compileOnly("de.robv.android.xposed:api:82")
    // Fallback if api.xposed.info is unreachable: put api-82.jar in app/libs and use this instead:
    // compileOnly(files("libs/api-82.jar"))
}
