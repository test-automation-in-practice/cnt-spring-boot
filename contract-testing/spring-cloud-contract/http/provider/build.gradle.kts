import org.springframework.cloud.contract.verifier.config.TestFramework

plugins {
    id("org.springframework.boot")
    id("org.springframework.cloud.contract")
    id("io.spring.dependency-management")

    id("maven-publish")

    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-verifier")
    testImplementation("io.mockk:mockk")

    contractTestImplementation("org.springframework.cloud:spring-cloud-contract-spec-kotlin")
    contractTestImplementation("org.springframework.cloud:spring-cloud-contract-pact")
    // needed because of issues compiling Kotlin contracts
    contractTestImplementation("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:${property("kotlinVersion")}")
}

contracts {
    // generate JUnit 5 test classes
    setTestFramework(TestFramework.JUNIT5)
    // sets a fallback for any mapping misses and defines base package of generated tests
    setBaseClassForTests("provider.ContractTestBase")
    // mapping of base classes for different contracts
    baseClassMappings {
        // any Spring Cloud Contract DSL-based tests should use a base class with callback methods
        baseClassMapping(""".*\.scc""", "provider.SpringCloudContractTestBase")
        // any PACT-based tests should use a base class with default users for security
        baseClassMapping(""".*\.pact""", "provider.PactContractTestBase")
    }
}

publishing {
    publications {
        // define a publication for generated stubs to be used by the consumers
        create<MavenPublication>("stubs") {
            artifact(tasks.verifierStubsJar)
        }
    }
}
