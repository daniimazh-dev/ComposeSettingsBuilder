import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
    signing
}

android {
    namespace = "com.daniil.csb"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures { compose = true }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

fun getProp(name: String): String? = project.findProperty(name)?.toString() ?: localProperties.getProperty(name)

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "io.github.daniimazh-dev"
                artifactId = "csb"
                version = "1.0.3"

                pom {
                    name.set("Compose Settings Builder")
                    description.set("Compose Settings Builder is a powerful and flexible library for creating settings using kotlin DSL for your Jetpack Compose project")
                    url.set("https://github.com/daniimazh-dev/ComposeSettingsBuilder")
                    licenses {
                        license { name.set("MIT License") }
                    }
                    developers {
                        developer {
                            id.set("daniimazh8")
                            name.set("Daniil")
                            email.set("daniimazh8@gmail.com")
                        }
                    }
                    scm {
                        val gitUrl = "https://github.com/daniimazh-dev/ComposeSettingsBuilder"
                        connection.set("scm:git:git://github.com/daniimazh-dev/ComposeSettingsBuilder.git")
                        developerConnection.set("scm:git:git://github.com/daniimazh-dev/ComposeSettingsBuilder.git")
                        url.set(gitUrl)
                    }
                }
            }
        }
        repositories {
            maven {
                name = "Local"
                url = uri(layout.buildDirectory.dir("publish-repo"))
            }
        }
    }

    val keyId = getProp("signing.keyId")
    val key = getProp("signing.key")
    val password = getProp("signing.password")

    if (keyId != null && key != null && password != null) {
        signing {
            useInMemoryPgpKeys(keyId, key, password)
            sign(publishing.publications["release"])
        }
    }
}

tasks.register<Zip>("generateBundle") {
    description = "Generate zip bundle file"
    group = "publishing"
    dependsOn("publishReleasePublicationToLocalRepository")
    from(layout.buildDirectory.dir("publish-repo"))
    archiveFileName.set("bundle.zip")
    destinationDirectory.set(layout.buildDirectory.dir("outputs/bundle"))
}

kotlin {
    compilerOptions {
//        freeCompilerArgs.add("-Xcontext-parameters")
        jvmTarget = JvmTarget.JVM_11
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.datastore)
}
