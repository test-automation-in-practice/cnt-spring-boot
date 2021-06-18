import org.springframework.cloud.contract.verifier.config.TestFramework

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.springframework.cloud.contract")

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
    testImplementation("io.mockk:mockk:${property("mockkVersion")}")

    contractTestImplementation("org.springframework.cloud:spring-cloud-contract-spec-kotlin")
    contractTestImplementation("org.springframework.cloud:spring-cloud-contract-pact")
    // needed because of issues compiling Kotlin contracts
    contractTestImplementation("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:${property("kotlinVersion")}")
}

contracts {
    setTestFramework(TestFramework.JUNIT5)
    setBaseClassForTests("provider.ContractTestBase")
}

publishing {
    publications {
        create<MavenPublication>("stubs") {
            artifact(tasks.verifierStubsJar)
        }
    }
}
