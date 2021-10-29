import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.6" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
    id("org.springframework.cloud.contract") version "3.0.3" apply false
    id("org.asciidoctor.jvm.convert") version "3.3.2" apply false

    kotlin("jvm") version "1.5.31" apply false
    kotlin("plugin.spring") version "1.5.31" apply false
    kotlin("plugin.jpa") version "1.5.31" apply false
    kotlin("plugin.noarg") version "1.5.31" apply false
}

extra["springCloudVersion"] = "2020.0.4"
extra["mockkVersion"] = "1.12.0"
extra["pactVersion"] = "4.2.14"
extra["kotlinVersion"] = "1.5.31"
extra["springmockkVersion"] = "3.0.1"

allprojects {
    repositories { mavenCentral() }

    tasks {
        withType<JavaCompile> {
            sourceCompatibility = "11"
            targetCompatibility = "11"
        }
        withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xjsr305=strict")
                jvmTarget = "11"
            }
        }
        withType<Test> {
            group = "verification"
            useJUnitPlatform()
            testLogging { events(FAILED, SKIPPED) }
        }
    }
}
