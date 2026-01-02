plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")

    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-restclient")
    implementation("org.springframework.boot:spring-boot-starter-webclient")

    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("org.zalando:logbook-logstash")
    implementation("org.zalando:logbook-netty")
    implementation("org.zalando:logbook-okhttp")
    implementation("org.zalando:logbook-spring-boot-starter")

    implementation("com.squareup.okhttp3:okhttp")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.springframework.boot:spring-boot-starter-restclient-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webclient-test")

    testImplementation("org.wiremock.integrations:wiremock-spring-boot-standalone")
}
