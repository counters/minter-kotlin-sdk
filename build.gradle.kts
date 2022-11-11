import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
}

group = "counters"
version = "2.4.7"

val grpcKotlinVersion = "1.3.0"
val coroutinesVersion = "1.6.1"


repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven(url = "https://jitpack.io") {}
}

dependencies {

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.21")
    implementation("org.slf4j", "slf4j-simple", "1.7.30")

    implementation(group = "org.json", name = "json", version = "20200518")
    testImplementation("org.slf4j", "slf4j-simple", "1.7.26")
    implementation("joda-time:joda-time:2.12.1")

    implementation(fileTree("libs"))

    implementation("com.google.code.gson:gson:2.10")

    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("com.github.kittinunf.fuel:fuel-coroutines:2.3.1")

    implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")

    implementation("io.grpc:grpc-netty-shaded:1.50.2")
    implementation("io.grpc:grpc-protobuf:1.50.2")
    implementation("io.grpc:grpc-stub:1.50.2")

    implementation("com.google.protobuf:protobuf-java:3.21.9")
    implementation("com.google.protobuf:protobuf-kotlin:3.20.3")
    implementation(kotlin("stdlib-jdk8"))

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

/*
application {
    mainClassName = "MainKt"
}
*/
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
