import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.1" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
    id("org.asciidoctor.jvm.convert") version "3.3.2" apply false

    kotlin("jvm") version "1.7.0" apply false
    kotlin("plugin.spring") version "1.7.0" apply false
    kotlin("plugin.jpa") version "1.7.0" apply false
    kotlin("plugin.noarg") version "1.7.0" apply false
}

allprojects {
    repositories { mavenCentral() }

    if (project.childProjects.isEmpty()) {
        apply {
            plugin("io.spring.dependency-management")
        }
        the<DependencyManagementExtension>().apply {
            imports {
                mavenBom("io.github.openfeign:feign-bom:11.8")
                mavenBom("org.springframework.cloud:spring-cloud-dependencies:2021.0.3")
                mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
                mavenBom("org.testcontainers:testcontainers-bom:1.17.2")
            }
            dependencies {
                dependency("com.ninja-squad:springmockk:3.1.1")
                dependency("io.github.logrecorder:logrecorder-assertions:2.2.0")
                dependency("io.github.logrecorder:logrecorder-logback:2.2.0")
                dependency("io.mockk:mockk:1.12.4")
                dependency("io.rest-assured:kotlin-extensions:4.5.1")
            }
        }
    }

    tasks {
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
            testLogging { events(FAILED, SKIPPED) }
        }
    }
}
