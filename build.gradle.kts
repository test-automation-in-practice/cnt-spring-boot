import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
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

extra["kotlinVersion"] = "1.5.31"

allprojects {
    repositories { mavenCentral() }

    if (project.childProjects.isEmpty()) {
        apply {
            plugin("io.spring.dependency-management")
        }
        the<DependencyManagementExtension>().apply {
            imports {
                mavenBom("io.github.openfeign:feign-bom:11.7")
                mavenBom("org.springframework.cloud:spring-cloud-dependencies:2020.0.4")
                mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
            }
            dependencies {
                dependency("info.novatec.testit:logrecorder-logback:1.4.0")
                dependency("io.mockk:mockk:1.12.0")
                dependency("org.testcontainers:testcontainers:1.16.2")
                dependency("au.com.dius.pact.consumer:junit5:4.2.14")
                dependency("au.com.dius.pact.provider:junit5:4.2.14")
                dependency("com.ninja-squad:springmockk:3.0.1")
                dependency("org.testcontainers:kafka:1.16.2")
                dependency("org.testcontainers:rabbitmq:1.16.2")
            }
        }
    }

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
