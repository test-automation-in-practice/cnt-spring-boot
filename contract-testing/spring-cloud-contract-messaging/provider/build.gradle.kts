import org.springframework.cloud.contract.verifier.config.TestFramework.JUNIT5

plugins {
    id("org.springframework.boot")
    id("org.springframework.cloud.contract")
    id("io.spring.dependency-management")

    id("maven-publish")

    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.kafka:spring-kafka")

    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-verifier")

    contractTestImplementation("org.springframework.cloud:spring-cloud-contract-spec-kotlin")
    // needed because of issues compiling Kotlin contracts
    contractTestImplementation("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:${property("kotlinVersion")}")
}

contracts {
    // generate JUnit 5 test classes
    setTestFramework(JUNIT5)
    // map a base class for all contract tests
    setBaseClassForTests("provider.ContractTestBase")
}

publishing {
    publications {
        // define a publication for generated stubs to be used by the consumers
        create<MavenPublication>("stubs") {
            // a messaging contract test requires both jar types
            artifact(tasks.verifierStubsJar)
            artifact(tasks.bootJar)
        }
    }
}
