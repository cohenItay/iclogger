import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.itayc.loggers_android"
    compileSdk = 34

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {
    implementation(project(":iclogger"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.work)
}

val localProperties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.itayc"
            artifactId = "iclogger-android-loggers"
            version = "1.3"
            afterEvaluate {
                from(components["release"])
            }
        }
    }
    repositories {
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/cohenItay/iclogger")
            credentials {
                username = localProperties.getProperty("mavenUser")
                password = localProperties.getProperty("mavenPassword")
            }
        }
    }
}