import java.util.*

plugins {
    id("org.jetbrains.intellij") version "1.15.0"
    kotlin("jvm")
    id("org.jetbrains.compose")
}
val GIRAFFE_AS = "223.8836.35.2231.10406996"

val HEDGEHOG = "231.9392.1"
val GIRAFFE = "223.8836.41"
val FLAMINGO = "222.3739.54"
val EEL = "221.6008.13"
val DOLPHIN = "213.7172.25"
val ic_version = GIRAFFE

group = "io.github.appspirimentlabs"
version = "1.0.2-$ic_version"

repositories {
    mavenCentral()
    google()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    implementation(compose.desktop.macos_arm64)
    implementation(compose.desktop.macos_x64)
    implementation(compose.desktop.windows_x64)
//    implementation(compose.desktop.linux_arm64)
//    implementation(compose.desktop.linux_x64)
}

intellij {
    version.set(ic_version)
    type.set("IC")
    plugins.set(listOf("android"))
}

val properties = Properties().apply {
    load(rootProject.file("local.properties").reader())
}
val certChainFile = properties["CERT_CHAIN_FILE"] ?: ""
val certPrivatKeyFile = properties["CERT_PRIVATE_KEY_FILE"]  ?: ""
val certPass = properties["CERT_PASS"]  as String?

tasks {
//    runIde {
//        // Absolute path to installed target 3.5 Android Studio to use as
//        // IDE Development Instance (the "Contents" directory is macOS specific):
//        ideDir.set(file("/Applications/Android Studio.app/Contents"))
//    }
    signPlugin {
        certificateChainFile.set(file(certChainFile))
        privateKeyFile.set(file(certPrivatKeyFile))
        password.set(certPass)
    }

    publishPlugin {
        token.set(System.getenv("ORG_GRADLE_PROJECT_vectorViewerPublishToken"))
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

