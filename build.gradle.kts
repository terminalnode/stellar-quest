val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val stellarSdkVersion = "0.24.0"

plugins {
  application
  kotlin("jvm") version "1.5.0"
}

group = "terminalnode.xyz"
version = "0.0.1"

application {
  mainClass.set("terminalnode.xyz.ApplicationKt")
}

repositories {
  maven { setUrl("https://jitpack.io") }
  mavenCentral()
}

dependencies {
  implementation("com.github.stellar:java-stellar-sdk:$stellarSdkVersion")
  implementation("io.ktor:ktor-server-core:$ktor_version")
  implementation("io.ktor:ktor-server-netty:$ktor_version")
  implementation("io.ktor:ktor-gson:$ktor_version")

  implementation("ch.qos.logback:logback-classic:$logback_version")
  testImplementation("io.ktor:ktor-server-tests:$ktor_version")
}