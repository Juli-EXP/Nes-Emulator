val kotlinVersion: String by project
val tornadoFxVersion: String by project

plugins {
    kotlin("jvm") version "1.5.0"
}

repositories {
    mavenCentral()
}


dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    implementation("no.tornado:tornadofx:$tornadoFxVersion")
}