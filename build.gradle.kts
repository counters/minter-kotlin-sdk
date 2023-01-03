import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
    `maven-publish`
}

group = "counters"
version = "2.5.0"

val grpcKotlinVersion = "1.3.0"

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.github.com/counters/jvm-minter-grpc-class")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USER")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")
    implementation("org.slf4j:slf4j-simple:2.0.5") // http://saltnlight5.blogspot.com/2013/08/how-to-configure-slf4j-with-different.html

    implementation("joda-time:joda-time:2.12.2")

    implementation("com.google.code.gson:gson:2.10")
    implementation(group = "org.json", name = "json", version = "20220320")

    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("com.github.kittinunf.fuel:fuel-coroutines:2.3.1")

    implementation("counters:jvm-minter-grpc-class:1.3.2")

    implementation("io.grpc:grpc-netty-shaded:1.50.2")
    implementation("io.grpc:grpc-kotlin-stub:1.3.0")
    implementation("io.grpc:grpc-protobuf:1.50.2")

//    implementation("io.grpc:grpc-stub:1.50.2")
//    implementation("com.google.protobuf:protobuf-java:3.21.9")
//    implementation("com.google.protobuf:protobuf-kotlin:3.20.3")


//    implementation(kotlin("stdlib-jdk8"))

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
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

/*plugins {
    `maven-publish`
}*/
publishing {
    repositories {
        maven {
            name = "MinterKotlinSDK"
            url = uri("https://maven.pkg.github.com/counters/minter-kotlin-sdk")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}
