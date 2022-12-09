import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.6" apply false
    id("io.spring.dependency-management") version "1.0.15.RELEASE" apply false
    id("org.asciidoctor.jvm.convert") version "3.3.2" apply false

    kotlin("jvm") version "1.7.20" apply false
    kotlin("plugin.spring") version "1.7.20" apply false
    kotlin("plugin.jpa") version "1.7.20" apply false
    kotlin("plugin.noarg") version "1.7.20" apply false
}

allprojects {
    repositories { mavenCentral() }

    if (project.childProjects.isEmpty()) {
        apply {
            plugin("io.spring.dependency-management")
        }
        the<DependencyManagementExtension>().apply {
            imports {
                mavenBom("io.github.openfeign:feign-bom:12.1")
                mavenBom("org.springframework.cloud:spring-cloud-dependencies:2021.0.5")
                mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
                mavenBom("org.testcontainers:testcontainers-bom:1.17.6")
            }
            dependencies {
                dependency("com.ninja-squad:springmockk:3.1.2")
                dependency("io.github.logrecorder:logrecorder-assertions:2.4.0")
                dependency("io.github.logrecorder:logrecorder-junit5:2.4.0")
                dependency("io.github.logrecorder:logrecorder-logback:2.4.0")
                dependency("io.mockk:mockk-jvm:1.13.3")
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
