import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.7" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
    id("org.springframework.cloud.contract") version "3.1.1" apply false
    id("org.asciidoctor.jvm.convert") version "3.3.2" apply false

    kotlin("jvm") version "1.6.21" apply false
    kotlin("plugin.spring") version "1.6.21" apply false
    kotlin("plugin.jpa") version "1.6.21" apply false
    kotlin("plugin.noarg") version "1.6.21" apply false
}

extra["kotlinVersion"] = "1.6.21"

allprojects {
    repositories { mavenCentral() }

    if (project.childProjects.isEmpty()) {
        apply {
            plugin("io.spring.dependency-management")
        }
        the<DependencyManagementExtension>().apply {
            imports {
                mavenBom("io.github.openfeign:feign-bom:11.7")
                mavenBom("org.springframework.cloud:spring-cloud-dependencies:2021.0.2")
                mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
            }
            dependencies {
                dependency("com.ninja-squad:springmockk:3.1.1")
                dependency("io.github.logrecorder:logrecorder-assertions:2.1.0")
                dependency("io.github.logrecorder:logrecorder-logback:2.1.0")
                dependency("io.mockk:mockk:1.12.3")
                dependency("io.rest-assured:kotlin-extensions:4.3.3")
                dependency("org.testcontainers:testcontainers:1.17.1")
                dependency("org.testcontainers:kafka:1.17.1")
                dependency("org.testcontainers:rabbitmq:1.17.1")
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
                incremental = false
            }
        }
        withType<Test> {
            group = "verification"
            useJUnitPlatform()
            testLogging { events(FAILED, SKIPPED) }
        }
    }
}
