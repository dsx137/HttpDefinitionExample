plugins {
    java
    idea
    `java-library`
    id("com.gradleup.shadow") version "9.0.0+"
    kotlin("jvm") version "1.9.23"
    kotlin("kapt") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    kotlin("plugin.lombok") version "1.9.23"
}

group = "com.github.dsx137"
base.archivesName.set(rootProject.name)
version = "1.0-SNAPSHOT"

repositories {
    maven("https://maven.aliyun.com/repository/public/")
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx/toml")
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core:2.3.13")
    implementation("io.ktor:ktor-client-cio:2.3.13")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.13")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.13")
    implementation("com.akuleshov7:ktoml-core:0.5.1")
    implementation("com.akuleshov7:ktoml-file:0.5.1")
    implementation("org.slf4j:slf4j-simple:2.0.13")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("org.msgpack:msgpack-core:0.9.9")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "com.github.dsx137.http_definition_example.HttpDefinitionExample"
        )
    }
}

tasks.shadowJar { minimize() }

tasks.build { dependsOn(tasks.shadowJar) }