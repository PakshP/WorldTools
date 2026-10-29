import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    `maven-publish`
    kotlin("jvm")
    id("net.fabricmc.fabric-loom")
}

val modVersion = project.properties["mod_version"]!! as String
val minecraftVersion = project.properties["minecraft_version"]!! as String

base.archivesName.set("${project.properties["archives_base_name"]!!}-fabric")
group = project.properties["maven_group"]!!
version = "$modVersion+$minecraftVersion"

// Loader-independent code lives in the common directory and is compiled directly into the Fabric jar.
val commonDir = rootProject.file("common/src/main")
sourceSets.main {
    java.srcDir(commonDir.resolve("java"))
    kotlin.srcDir(commonDir.resolve("kotlin"))
    resources.srcDir(commonDir.resolve("resources"))
}

repositories {
    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com/releases/")
}

loom {
    accessWidenerPath.set(commonDir.resolve("resources/worldtools.accesswidener"))
    runs {
        getByName("client") {
            ideConfigGenerated(true)
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    implementation("net.fabricmc:fabric-loader:${project.properties["fabric_loader_version"]!!}")
    implementation("net.fabricmc.fabric-api:fabric-api:${project.properties["fabric_api_version"]!!}")
    implementation("net.fabricmc:fabric-language-kotlin:${project.properties["fabric_kotlin_version"]!!}")
    api("me.shedaniel.cloth:cloth-config-fabric:${project.properties["cloth_config_version"]!!}") {
        exclude(group = "net.fabricmc.fabric-api", module = "fabric-api")
    }
    api("com.terraformersmc:modmenu:${project.properties["mod_menu_version"]!!}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_25)
    }
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release = 25
    }

    processResources {
        val props = mapOf(
            "version" to project.version,
            "minecraft_version" to minecraftVersion,
            "fabric_loader_version" to project.properties["fabric_loader_version"]!!,
            "fabric_kotlin_version" to project.properties["fabric_kotlin_version"]!!,
        )
        inputs.properties(props)
        filesMatching("fabric.mod.json") {
            expand(props)
        }
    }
}
