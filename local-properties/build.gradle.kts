plugins {
    kotlin("jvm") version "1.5.30"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.register("printMyProp") {
    val myProp = getLocalProperty("myProp")
    doLast {
        logger.warn("myProp = $myProp")
    }
}