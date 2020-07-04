import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.72"
}

group = "MinterKotlinSDK"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://jitpack.io") {
        //        name = "jitpack"
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.1")

//    implementation(group = "com.github.kittinunf.fuel", name = "fuel", version = "-SNAPSHOT")
//    implementation(group = "com.github.kittinunf.fuel", name = "fuel-coroutines", version = "-SNAPSHOT")
//    implementation("mysql:mysql-connector-java:5.1.46")
    implementation("org.jetbrains.exposed:exposed:0.13.7")
    implementation("io.github.microutils:kotlin-logging:1.6.24")
    implementation("khttp:khttp:1.0.0")

    implementation("com.google.code.gson:gson:2.8.2")

    implementation("com.github.uchuhimo:konf:master-SNAPSHOT")

    testCompile("org.slf4j", "slf4j-simple", "1.7.26")

//    implementation ("com.xenomachina:kotlin-argparser:2.0.7")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}