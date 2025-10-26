import java.io.FileInputStream
import java.util.Properties

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

val localProperties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

tasks.register(
    "sourcesJar",
    Jar::class.java,
    object : Action<Jar> {
        override fun execute(jar: Jar) {
            jar.archiveClassifier.set("sources") // The set("sources") part tells Gradle that this JAR is for the source code, and it will typically result in a filename like mylibrary-sources.jar.
            jar.from(sourceSets["main"].allSource) // Include the compiled classes from the main source set
        }
    }
)

publishing {
    publications {
        register<MavenPublication>("release") {
            from(components["kotlin"]) // in regular kotlin lib it doesn't need to be inside afterEvaluate
            groupId = "com.itayc"
            artifactId = "iclogger"
            version = "1.6"
            artifact(tasks.named("sourcesJar").get())
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