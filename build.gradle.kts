import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.file.DuplicatesStrategy.INCLUDE
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES

plugins {
    id("org.springframework.boot") version "4.0.2" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("org.asciidoctor.jvm.convert") version "4.0.5" apply false

    kotlin("jvm") version "2.2.21" apply false
    kotlin("plugin.spring") version "2.2.21" apply false
    kotlin("plugin.jpa") version "2.2.21" apply false
    kotlin("plugin.noarg") version "2.2.21" apply false
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
                mavenBom("org.jetbrains.kotlin:kotlin-bom:2.2.21")
                mavenBom("org.testcontainers:testcontainers-bom:1.21.4")
                mavenBom("org.zalando:logbook-bom:4.0.2")

                mavenBom("org.springframework.modulith:spring-modulith-bom:2.0.2")
                mavenBom("org.springframework.cloud:spring-cloud-dependencies:2025.1.0")
                mavenBom(BOM_COORDINATES)
            }
            dependencies {
                dependency("com.github.dasniko:testcontainers-keycloak:2.6.0")
                dependency("com.ninja-squad:springmockk:5.0.1")
                dependency("io.kotest:kotest-assertions-core:6.1.3")
                dependency("io.mockk:mockk-jvm:1.14.9")
                dependency("io.rest-assured:rest-assured:6.0.0")
                dependency("io.rest-assured:kotlin-extensions:6.0.0")
                dependency("com.squareup.okhttp3:okhttp:5.3.2")
                dependency("org.wiremock.integrations:wiremock-spring-boot-standalone:4.1.0")
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
                freeCompilerArgs.add("-Xannotation-default-target=param-property")
                apiVersion.set(KOTLIN_2_0)
                jvmTarget.set(JVM_21)
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
