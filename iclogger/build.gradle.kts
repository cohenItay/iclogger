import java.io.FileInputStream
import java.util.Properties

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

val localProperties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.itayc"
            artifactId = "iclogger"
            version = "1.0"
            //artifact(layout.buildDirectory.file("libs/iclogger.jar"))
            afterEvaluate {
                from(components["java"])
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