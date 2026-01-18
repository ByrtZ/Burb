import java.io.BufferedReader

val patch = "INDEV"

val commitHash = Runtime
    .getRuntime()
    .exec(arrayOf("git", "rev-parse", "--short", "HEAD"))
    .let { process ->
        process.waitFor()
        val output = process.inputStream.use {
            it.bufferedReader().use(BufferedReader::readText)
        }
        process.destroy()
        output.trim()
    }

plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("kapt") version "2.3.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("com.gradleup.shadow") version "9.3.1"
    id("xyz.jpenilla.run-paper") version "2.3.0"
}

group = "dev.byrt"
version = "$patch-build-$commitHash"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.noxcrew.com/public")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    implementation("org.incendo:cloud-core:2.0.0")
    implementation("org.incendo:cloud-paper:2.0.0-beta.10")
    implementation("org.incendo:cloud-annotations:2.0.0")
    implementation("org.incendo:cloud-kotlin-coroutines-annotations:2.0.0")
    kapt("org.incendo:cloud-kotlin-coroutines-annotations:2.0.0")
    implementation("org.incendo:cloud-kotlin-extensions:2.0.0")
    implementation("org.incendo:cloud-processors-confirmation:1.0.0-rc.1")
    implementation("org.reflections:reflections:0.10.2")
    implementation("fr.skytasul:glowingentities:1.4.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("io.ktor:ktor-client-core:2.3.13")
    implementation("io.ktor:ktor-client-cio:2.3.5")
    implementation("io.ktor:ktor-client-logging:2.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("org.json:json:20231013")
    implementation("com.noxcrew.noxesium:api:2.3.3")
    implementation("com.noxcrew.interfaces:interfaces:2.0.1-SNAPSHOT")
    implementation("me.lucyydotp.tinsel:tinsel-api:0.1.0") {
        // Everything tinsel needs is already pulled in by paper
        isTransitive = false
    }
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        javaParameters = true
    }
}

val shaded by configurations.creating {
    extendsFrom(configurations.runtimeClasspath.get())
    isCanBeResolved = true

    // These are maven group IDs, not java packages!
    exclude("com.google.code.gson")
    exclude("com.google.errorprone")
    exclude("org.checkerframework")
    exclude("org.intellij")
    exclude("org.jetbrains")
    exclude("org.slf4j")
}

tasks {
    shadowJar {
        configurations = listOf(shaded)
        exclude(
            "javax/annotation/**",
        )
    }
    runServer {
        minecraftVersion("1.21.11")
    }
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}