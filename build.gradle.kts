import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.0"
    application
}

group = "MinterKotlinSDK"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://jitpack.io") {}
}

dependencies {
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")

    implementation("org.jetbrains.exposed:exposed:0.13.7")
    implementation("io.github.microutils:kotlin-logging:1.6.24")
    implementation("khttp:khttp:1.0.0")
//    implementation("com.google.code.gson:gson:2.8.2")
    implementation( group="org.json", name="json", version= "20200518")
    implementation("com.github.uchuhimo:konf:master-SNAPSHOT")
    testCompile("org.slf4j", "slf4j-simple", "1.7.26")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClassName = "MainKt"
}