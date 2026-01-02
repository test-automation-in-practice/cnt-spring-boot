plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")

    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.springframework.boot:spring-boot-starter-cache-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-redis-test")

    testImplementation("io.mockk:mockk-jvm")
    testImplementation("com.ninja-squad:springmockk")
    testImplementation("org.testcontainers:testcontainers")
}
