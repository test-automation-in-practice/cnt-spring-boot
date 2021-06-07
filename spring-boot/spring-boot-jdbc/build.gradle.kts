plugins {
	id("org.springframework.boot")
	id("io.spring.dependency-management")

	kotlin("jvm")
	kotlin("plugin.spring")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.flywaydb:flyway-core")

	runtime("mysql:mysql-connector-java")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("io.mockk:mockk:${property("mockkVersion")}")
	testImplementation("com.h2database:h2")
}
