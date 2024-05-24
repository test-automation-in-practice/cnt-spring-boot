import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.file.DuplicatesStrategy.INCLUDE
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.3.2" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false
    id("org.asciidoctor.jvm.convert") version "3.3.2" apply false

    kotlin("jvm") version "2.0.0" apply false
    kotlin("plugin.spring") version "2.0.0" apply false
    kotlin("plugin.jpa") version "2.0.0" apply false
    kotlin("plugin.noarg") version "2.0.0" apply false
}

allprojects {
    repositories {
        mavenCentral()
    }

    if (project.childProjects.isEmpty()) {
        apply {
            plugin("io.spring.dependency-management")
        }
        the<DependencyManagementExtension>().apply {
            imports {
                mavenBom("io.github.logrecorder:logrecorder-bom:2.10.0")
                mavenBom("io.github.openfeign:feign-bom:13.3")
                mavenBom("org.jetbrains.kotlin:kotlin-bom:2.0.0")
                mavenBom("org.testcontainers:testcontainers-bom:1.20.1")
                mavenBom("org.zalando:logbook-bom:3.9.0")

                mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.3")
                mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
            }
            dependencies {
                dependency("com.github.dasniko:testcontainers-keycloak:2.6.0")
                dependency("com.ninja-squad:springmockk:4.0.2")
                dependency("io.kotest:kotest-assertions-core:5.9.1")
                dependency("io.mockk:mockk-jvm:1.13.12")
            }
        }
    }

    tasks {
        withType<Copy> { duplicatesStrategy = INCLUDE }
        withType<Jar> { duplicatesStrategy = INCLUDE }
        withType<JavaCompile> {
            sourceCompatibility = "21"
            targetCompatibility = "21"
        }
        withType<KotlinCompile> {
            compilerOptions {
                freeCompilerArgs.add("-Xjsr305=strict")
                apiVersion.set(KOTLIN_2_0)
                jvmTarget.set(JVM_21)
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
