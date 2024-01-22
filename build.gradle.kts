plugins {
    kotlin("jvm") version "1.8.22"
    id("fabric-loom") version "1.4-SNAPSHOT"
    kotlin("plugin.serialization") version "1.8.22"
    `maven-publish`
    java
}

val mcVersion = "1.20.1"
val silkVersion = "1.10.0"

group = "gg.norisk"
version = "${mcVersion}-1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.kosmx.dev/")
    maven("https://maven.wispforest.io")
    maven {
        name = "GeckoLib"
        url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    }
    exclusiveContent {
        forRepository {
            maven("https://api.modrinth.com/maven")
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    mappings("net.fabricmc:yarn:$mcVersion+build.2")
    modImplementation("net.fabricmc:fabric-loader:0.15.0")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.83.1+$mcVersion")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.9.5+kotlin.1.8.22")

    include(modImplementation("net.silkmc:silk-commands:$silkVersion")!!)
    include(modImplementation("net.silkmc:silk-core:$silkVersion")!!)
    include(modImplementation("net.silkmc:silk-network:$silkVersion")!!)

    modImplementation("software.bernie.geckolib:geckolib-fabric-$mcVersion:4.3.1")
    modImplementation("dev.kosmx.player-anim:player-animation-lib-fabric:1.0.2-rc1+1.20")
    modImplementation("io.wispforest:owo-lib:0.11.2+1.20")
    modImplementation("maven.modrinth:iris:1.6.11+1.20.2")
}

loom {
    accessWidenerPath.set(file("src/main/resources/subwaysurfers.accesswidener"))
}

tasks {

    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(mutableMapOf("version" to project.version))
        }
    }

    jar {
        from("LICENSE")
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                artifact(remapJar) {
                    builtBy(remapJar)
                }
                artifact(kotlinSourcesJar) {
                    builtBy(remapSourcesJar)
                }
            }
        }

        // select the repositories you want to publish to
        repositories {
            // uncomment to publish to the local maven
            // mavenLocal()
        }
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }

}

java {
    withSourcesJar()
}
