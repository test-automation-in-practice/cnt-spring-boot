import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.file.DuplicatesStrategy.INCLUDE
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.1" apply false
    id("io.spring.dependency-management") version "1.1.0" apply false
    id("org.asciidoctor.jvm.convert") version "3.3.2" apply false

    kotlin("jvm") version "1.8.22" apply false
    kotlin("plugin.spring") version "1.8.22" apply false
    kotlin("plugin.jpa") version "1.8.22" apply false
    kotlin("plugin.noarg") version "1.8.22" apply false
}

allprojects {
    repositories { mavenCentral(); mavenLocal() }

    if (project.childProjects.isEmpty()) {
        apply {
            plugin("io.spring.dependency-management")
        }
        the<DependencyManagementExtension>().apply {
            imports {
                mavenBom("io.github.logrecorder:logrecorder-bom:2.7.0")
                mavenBom("io.github.openfeign:feign-bom:12.3")
                mavenBom("org.jetbrains.kotlin:kotlin-bom:1.8.22")
                mavenBom("org.testcontainers:testcontainers-bom:1.18.3")
                mavenBom("org.zalando:logbook-bom:3.1.0")

                mavenBom("org.springframework.cloud:spring-cloud-dependencies:2022.0.3")
                mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
            }
            dependencies {
                dependency("com.github.dasniko:testcontainers-keycloak:2.5.0")
                dependency("com.ninja-squad:springmockk:4.0.2")
                dependency("io.mockk:mockk-jvm:1.13.5")
                dependency("org.apache.activemq:activemq-broker:5.17.3")
                dependency("org.apache.activemq:activemq-client:5.17.3")
                dependency("org.apache.activemq:activemq-jms-pool:5.17.3")
                dependency("org.apache.activemq:activemq-kahadb-store:5.17.3")

                // legacy compatibility
                dependency("de.flapdoodle.embed:de.flapdoodle.embed.mongo.spring30x:4.7.0")
                dependency("org.apache.activemq:artemis-jms-server:2.29.0")
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
            testLogging { events(FAILED, SKIPPED) }
        }
    }
}
