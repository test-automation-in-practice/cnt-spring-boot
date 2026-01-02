plugins {
    id("org.springframework.boot")
    id("org.asciidoctor.jvm.convert")
    id("io.spring.dependency-management")

    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.springframework.boot:spring-boot-restdocs")
    testImplementation("org.springframework.boot:spring-boot-starter-graphql-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webclient-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")

    testImplementation("org.springframework.restdocs:spring-restdocs-webtestclient")

    testImplementation("io.rest-assured:rest-assured")
    testImplementation("io.rest-assured:kotlin-extensions")
    testImplementation("io.mockk:mockk-jvm")
    testImplementation("com.ninja-squad:springmockk")
}

tasks {
    asciidoctor {
        dependsOn("test")
        baseDirFollowsSourceDir()
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

    asciidoctorj {
        fatalWarnings("include file not found")
    }

    bootJar {
        dependsOn("asciidoctor")
        from(file("build/docs/asciidoc/index.html")) {
            into("BOOT-INF/classes/static/docs")
        }
    }
}
