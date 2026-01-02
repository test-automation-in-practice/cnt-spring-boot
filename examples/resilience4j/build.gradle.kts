plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")

    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aspectj")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    implementation("tools.jackson.module:jackson-module-kotlin")

    implementation("io.github.resilience4j:resilience4j-all")
    implementation("io.github.resilience4j:resilience4j-kotlin")
    // TODO module for 4 is not yet released but this one seems to still work
    implementation("io.github.resilience4j:resilience4j-spring-boot3")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-aspectj-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")

    testImplementation("com.ninja-squad:springmockk")
    testImplementation("io.mockk:mockk-jvm")
    testImplementation("io.github.logrecorder:logrecorder-assertions")
    testImplementation("io.github.logrecorder:logrecorder-junit5")
    testImplementation("io.github.logrecorder:logrecorder-logback")
}
