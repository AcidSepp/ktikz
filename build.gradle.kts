plugins {
    java
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.jzy3d.org/releases")
    }
}

dependencies {
    testImplementation(libs.assertj)
    testImplementation(libs.mockito)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(25)
}

testing.suites.named<JvmTestSuite>("test") {
    useJUnitJupiter(libs.versions.junit.jupiter)
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
