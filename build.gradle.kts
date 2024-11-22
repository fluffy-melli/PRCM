plugins {
    application
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "1.8.0"
    id("org.springframework.boot") version "3.1.0"
    id("com.github.johnrengelman.shadow") version "8.0.0"
}

group = "prcm.kotlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/release") }
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.springframework.boot:spring-boot-starter-web:3.1.0")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf:3.1.0")
    implementation(kotlin("stdlib"))
}

application {
    mainClass.set("prcm.kotlin.MainKt")
}

tasks.jar {
    archiveBaseName.set("prcm")
    manifest {
        attributes["Main-Class"] = "prcm.kotlin.MainKt"
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}