plugins {
    kotlin("jvm") version "1.9.0"
    application
    antlr
}

group = "org.adaxiik"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
//    testImplementation(kotlin("test"))
    antlr("org.antlr:antlr4:4.13.1")
}
//
//tasks.test {
//    useJUnitPlatform()
//}

kotlin {
    jvmToolchain(11)
}

tasks.generateGrammarSource {
    outputDirectory = file("${project.buildDir}/generated/sources/main/java/antlr")
    arguments = listOf("-no-listener", "-visitor")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

sourceSets {
    main {
        java {
            srcDir(tasks.generateGrammarSource)
        }
    }
}

application {
    mainClass.set("MainKt")
}

