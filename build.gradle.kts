import java.util.Properties
import java.io.FileInputStream
import org.jetbrains.dokka.gradle.DokkaTask
import java.util.Date

plugins {
    kotlin("jvm") version "1.3.72"
    id("com.jfrog.bintray") version "1.8.4"
    id("org.jetbrains.dokka") version "0.10.0"
    `maven-publish`
    `java-library`
}

group = "com.ale.lib"
version = "1.0.0"

repositories {
    mavenCentral()

    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")

}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }


    register<Jar>("javadocJar") {
        val dokkaTask = getByName<DokkaTask>("dokka")
        from(dokkaTask.outputDirectory)
        dependsOn(dokkaTask)
        archiveClassifier.set("javadoc")
    }

    dokka {
        outputFormat = "html"
        outputDirectory = "$buildDir/dokka"

    }
}

val properties = Properties()
properties.load(FileInputStream("local.properties"))

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
            artifact(tasks["javadocJar"])
            groupId = project.group.toString()
            version = project.version.toString()
        }
    }
}

bintray {
    user = properties.getProperty("bintray.user")
    key = properties.getProperty("bintray.apikey")
    setPublications("default")
    publish = true //[Default: false] Whether version should be auto published after an upload
    pkg.apply {
        repo = "toninelli-library"
        name = "klog"
        setLicenses("Apache-2.0")
        version.apply {
            name = "${project.version}"
            desc = "klog logging library"
            released = "${Date()}"
            vcsTag = project.version.toString()
        }
    }
}




