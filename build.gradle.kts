import java.util.Properties
import java.io.FileInputStream
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.javac.resolve.classId
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

sourceSets.main{
    java.srcDir("src/main/kotlin")
}

tasks {

    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }


    register<Jar>("sourceJar") {
        archiveClassifier.set("sources")
        from(sourceSets.getByName("main").allSource)
    }

    dokka {
        outputFormat = "html"
        outputDirectory = "$buildDir/javadoc"
    }

}


val properties = Properties()
properties.load(FileInputStream("local.properties"))

publishing {
    publications {
        create<MavenPublication>("default") {
            artifactId = project.name
            groupId = project.group.toString()
            version = project.version.toString()
            from(components["java"])
            artifact(tasks["sourceJar"])

            pom {
                name.set(rootProject.name)
                url.set("https://github.com/alessandroToninelli/klog")
                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        name.set("Alessandro Toninelli")
                        id.set("alessandroToninelli")
                    }
                }
                scm {
                    url.set("https://github.com/alessandroToninelli/klog")
                }
            }
        }
    }
}

bintray {
    user = properties.getProperty("bintray.user")
    key = properties.getProperty("bintray.apikey")
    setPublications("default")
    publish = true
    pkg.apply {
        repo = "toninelli-library"
        name = rootProject.name
        setLicenses("Apache-2.0")
        version.apply {
            name = "${project.version}"
            desc = "klog logging library"
            released = "${Date()}"
            vcsTag = project.version.toString()
        }
    }
}




