plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")

    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.github.logrecorder:logrecorder-assertions")
    testImplementation("io.github.logrecorder:logrecorder-junit5")
    testImplementation("io.github.logrecorder:logrecorder-logback")
    testImplementation("io.mockk:mockk-jvm")
    testImplementation("com.ninja-squad:springmockk")
}
