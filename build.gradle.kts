import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.8" apply false
    id("io.spring.dependency-management") version "1.1.0" apply false
    id("org.asciidoctor.jvm.convert") version "3.3.2" apply false

    kotlin("jvm") version "1.7.22" apply false
    kotlin("plugin.spring") version "1.7.22" apply false
    kotlin("plugin.jpa") version "1.7.22" apply false
    kotlin("plugin.noarg") version "1.7.22" apply false
}

allprojects {
    repositories { mavenCentral(); mavenLocal() }

    if (project.childProjects.isEmpty()) {
        apply {
            plugin("io.spring.dependency-management")
        }
        the<DependencyManagementExtension>().apply {
            imports {
                mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
                mavenBom("org.springframework.cloud:spring-cloud-dependencies:2021.0.5")

                mavenBom("io.github.logrecorder:logrecorder-bom:2.5.0")
                mavenBom("io.github.openfeign:feign-bom:12.1")
                mavenBom("org.jetbrains.kotlin:kotlin-bom:1.7.22")
                mavenBom("org.testcontainers:testcontainers-bom:1.17.6")
            }
            dependencies {
                dependency("com.ninja-squad:springmockk:3.1.2")
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
