plugins {
    kotlin("jvm") version "1.8.21"
    application
}

group = "de.pollpeter.jonas.chess"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    val ktorVersion: String by project

    dependencies {
        implementation("io.ktor:ktor-client-core:$ktorVersion")
        implementation("io.ktor:ktor-client-cio:$ktorVersion")
        implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
        implementation("io.ktor:ktor-serialization-gson:$ktorVersion")
        implementation("io.ktor:ktor-client-auth:$ktorVersion")
        implementation("io.socket:socket.io-client:2.1.0")
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}