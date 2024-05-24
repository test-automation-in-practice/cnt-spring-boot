plugins {
    id("org.springframework.boot")
    id("org.asciidoctor.jvm.convert")
    id("io.spring.dependency-management")

    kotlin("jvm")
    kotlin("plugin.spring")
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.modulith:spring-modulith-bom:1.1.5")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.modulith:spring-modulith-events-amqp")
    implementation("org.springframework.modulith:spring-modulith-starter-core")
    implementation("org.springframework.modulith:spring-modulith-starter-jdbc")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework:spring-webflux") // for WebTestClient
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.modulith:spring-modulith-starter-test")

    testImplementation("com.ninja-squad:springmockk")
    testImplementation("io.github.logrecorder:logrecorder-assertions")
    testImplementation("io.github.logrecorder:logrecorder-junit5")
    testImplementation("io.github.logrecorder:logrecorder-logback")
    testImplementation("io.kotest:kotest-assertions-core")
    testImplementation("io.mockk:mockk-jvm")
    testImplementation("org.testcontainers:mongodb")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:rabbitmq")
}

tasks {
    asciidoctor {
        inputs.dir(file("build/generated-snippets"))
        inputs.dir(file("build/spring-modulith-docs"))
        dependsOn(test)
        baseDirFollowsSourceDir()
        forkOptions {
            jvmArgs("--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED", "--add-opens", "java.base/java.io=ALL-UNNAMED")
        }
        options(
            mapOf(
                "doctype" to "book",
                "backend" to "html5"
            )
        )
        attributes(
            mapOf(
                "snippets" to file("build/generated-snippets"),
                "source-highlighter" to "coderay",
                "toclevels" to "3",
                "sectlinks" to "true",
                "data-uri" to "true",
                "nofooter" to "true"
            )
        )
    }
    bootJar {
        dependsOn(asciidoctor)
        from(asciidoctor) {
            into("BOOT-INF/classes/static/docs")
        }
    }
    test {
        outputs.dir(file("build/generated-snippets"))
        outputs.dir(file("build/spring-modulith-docs"))
    }
}

asciidoctorj {
    fatalWarnings("include file not found") // make build fail if generated files are missing
    modules {
        diagram.use()
        diagram.setVersion("2.2.13")
    }
}
