plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.dicedrop"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.dicedrop"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Блоки рекламы с вознаграждением в кабинете Яндекса
        buildConfigField("String", "AD_UNIT_REVIVE", "\"R-M-20287637-1\"")
        buildConfigField("String", "AD_UNIT_DOUBLE_COINS", "\"R-M-20287637-2\"")
        buildConfigField("String", "AD_UNIT_RANDOM_BONUS", "\"R-M-20287637-3\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        // В отладке только тестовая реклама: нажатия на настоящую Яндекс считает накруткой
        debug {
            buildConfigField("String", "AD_UNIT_REVIVE", "\"demo-rewarded-yandex\"")
            buildConfigField("String", "AD_UNIT_DOUBLE_COINS", "\"demo-rewarded-yandex\"")
            buildConfigField("String", "AD_UNIT_RANDOM_BONUS", "\"demo-rewarded-yandex\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "src/main/res"
        }
    }
}

apply(plugin = "kotlin-parcelize")

kotlin {
    sourceSets.all {
        languageSettings.optIn("kotlinx.parcelize.Parcelize")
    }
}

dependencies {
    implementation(libs.androidx.datastore)
    implementation(libs.gson)

    implementation(libs.decompose)
    implementation(libs.decompose.jetpack)

    implementation(libs.mvikotlin)
    implementation(libs.mvikotlin.main)
    implementation(libs.mvikotlin.coroutines)

    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    implementation(libs.yandex.mobileads)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}