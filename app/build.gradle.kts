plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
}

// قراءة رقم البناء من GitHub Actions وتجاوز آخر إصدار تم رفعه (58)
val passedRunNumber = project.findProperty("appVersionCode")?.toString()?.toIntOrNull()
val baseVersionCode = 58
val calculatedVersionCode = if (passedRunNumber != null) baseVersionCode + passedRunNumber else baseVersionCode
val baseVersionName = "3.4"

android {
    namespace = "com.playtesterhub.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.playtesterhub.app"
        minSdk = 24
        targetSdk = 36

        // الزيادة التلقائية
        versionCode = calculatedVersionCode
        versionName = if (passedRunNumber != null) "$baseVersionName.$passedRunNumber" else baseVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val uploadKeystoreFile = listOf(
        file("../upload-keystore.jks"),
        file("upload-keystore.jks")
    ).firstOrNull { it.exists() }

    val defaultKeystore = file("../debug.keystore")
    val isUploadKeystoreActive = uploadKeystoreFile != null || System.getenv("KEYSTORE_PASSWORD") != null

    val signingFile = uploadKeystoreFile ?: defaultKeystore
    val storePass = System.getenv("KEYSTORE_PASSWORD") ?: if (uploadKeystoreFile != null) "NnBkgyAhj1sG0FZkLD9x" else "android"
    val aliasName = System.getenv("KEY_ALIAS") ?: if (uploadKeystoreFile != null) "upload" else "androiddebugkey"
    val keyPass = System.getenv("KEY_PASSWORD") ?: storePass

    signingConfigs {
        create("release") {
            storeFile = signingFile
            storePassword = storePass
            keyAlias = aliasName
            keyPassword = keyPass
        }
        getByName("debug") {
            storeFile = if (isUploadKeystoreActive) signingFile else defaultKeystore
            storePassword = if (isUploadKeystoreActive) storePass else "android"
            keyAlias = if (isUploadKeystoreActive) aliasName else "androiddebugkey"
            keyPassword = if (isUploadKeystoreActive) keyPass else "android"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.coil.compose)
    implementation(libs.play.services.ads)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.auth.ktx)
    // implementation(libs.firebase.messaging.ktx)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// Auto-provision fallback google-services.json for CI/CD environments (e.g. GitHub Actions) if not committed
tasks.matching { it.name.startsWith("process") && it.name.contains("GoogleServices") }.configureEach {
    doFirst {
        val gServices = file("google-services.json")
        if (!gServices.exists() || gServices.length() == 0L) {
            gServices.writeText(
                """
                {
                  "project_info": {
                    "project_number": "170455936881",
                    "project_id": "playtester-hub-61bd6",
                    "storage_bucket": "playtester-hub-61bd6.appspot.com"
                  },
                  "client": [
                    {
                      "client_info": {
                        "mobilesdk_app_id": "1:170455936881:android:29bcec6a7e7d0f03c410d0",
                        "android_client_info": {
                          "package_name": "com.playtesterhub.app"
                        }
                      },
                      "oauth_client": [],
                      "api_key": [
                        {
                          "current_key": "AIzaSyDummyKeyForCIBuildConfigOnly12345678"
                        }
                      ],
                      "services": {
                        "appinvite_service": {
                          "other_platform_oauth_client": []
                        }
                      }
                    }
                  ],
                  "configuration_version": "1"
                }
                """.trimIndent()
            )
        }
    }
}
