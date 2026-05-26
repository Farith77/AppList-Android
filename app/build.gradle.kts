// --- 1. IMPORTACIONES ---
import java.util.Properties

// --- 2. PLUGINS ---
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.0.21-1.0.28"
    kotlin("plugin.serialization") version "2.0.21"
}

// --- 3. VARIABLES CUSTOM Y CONFIGURACIÓN LOCAL ---
// Leemos el archivo local.properties antes de configurar Android
val properties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    properties.load(localPropertiesFile.inputStream())
}
val baseUrl = properties.getProperty("BASE_URL") ?: "http://10.0.2.2:8080/"

// --- 4. BLOQUE PRINCIPAL DE ANDROID ---
android {
    namespace = "com.dreamapps.applist"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.dreamapps.applist"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // VARIABLES INYECTADAS DESDE LOCAL.PROPERTIES
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

// --- 5. DEPENDENCIAS ---
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation("androidx.compose.material:material-icons-extended") //mas iconos

    // --- Versiones (Práctica SQA) ---
    val roomVersion = "2.6.1"
    val retrofitVersion = "2.9.0"
    val lifecycleVersion = "2.7.0"

    // --- 1. ROOM (Base de Datos Local) ---
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion") // Para usar Corrutinas con Room
    ksp("androidx.room:room-compiler:$roomVersion") // El procesador (reemplaza a kapt)

    // --- 2. RETROFIT (Conexión con Spring Boot) ---
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion") // Para convertir JSON a clases Kotlin

    // --- 3. LIFECYCLE & VIEWMODEL (Arquitectura MVVM) ---
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")

    // --- NAVEGACIÓN COMPONENT ---
    implementation("androidx.navigation:navigation-compose:2.8.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}