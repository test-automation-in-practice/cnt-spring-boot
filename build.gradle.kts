import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.file.DuplicatesStrategy.INCLUDE
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.0" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
    id("org.asciidoctor.jvm.convert") version "3.3.2" apply false

    kotlin("jvm") version "1.9.20" apply false
    kotlin("plugin.spring") version "1.9.20" apply false
    kotlin("plugin.jpa") version "1.9.20" apply false
    kotlin("plugin.noarg") version "1.9.20" apply false
}

allprojects {
    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
    }

    if (project.childProjects.isEmpty()) {
        apply {
            plugin("io.spring.dependency-management")
        }
        the<DependencyManagementExtension>().apply {
            imports {
                mavenBom("io.github.logrecorder:logrecorder-bom:2.9.1")
                mavenBom("io.github.openfeign:feign-bom:13.1")
                mavenBom("org.jetbrains.kotlin:kotlin-bom:1.9.20")
                mavenBom("org.testcontainers:testcontainers-bom:1.19.3")
                mavenBom("org.zalando:logbook-bom:3.6.0")

                mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.0-RC1")
                mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
            }
            dependencies {
                dependency("com.github.dasniko:testcontainers-keycloak:2.6.0")
                dependency("com.ninja-squad:springmockk:4.0.2")
                dependency("io.kotest:kotest-assertions-core:5.8.0")
                dependency("io.mockk:mockk-jvm:1.13.8")

                // currently using older version because of issues updating to Jakarta based version
                dependency("org.apache.activemq:activemq-broker:5.17.3")
                dependency("org.apache.activemq:activemq-client:5.17.3")
                dependency("org.apache.activemq:activemq-jms-pool:5.17.3")
                dependency("org.apache.activemq:activemq-kahadb-store:5.17.3")

                // legacy compatibility
                dependency("de.flapdoodle.embed:de.flapdoodle.embed.mongo.spring30x:4.11.0")
                dependency("org.apache.activemq:artemis-jms-server:2.31.2")
            }
        }
    }

    tasks {
        withType<Copy> { duplicatesStrategy = INCLUDE }
        withType<Jar> { duplicatesStrategy = INCLUDE }
        withType<JavaCompile> {
            sourceCompatibility = "17"
            targetCompatibility = "17"
        }
        withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xjsr305=strict")
                jvmTarget = "17"
                incremental = false
            }
        }
        withType<Test> {
            group = "verification"
            useJUnitPlatform()
            testLogging {
                events(FAILED, SKIPPED)
                exceptionFormat = FULL
            }
        }
    }
}
