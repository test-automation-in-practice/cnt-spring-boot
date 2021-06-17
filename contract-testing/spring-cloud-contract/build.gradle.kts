import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    id("io.spring.dependency-management")
}

subprojects {
    apply {
        plugin("io.spring.dependency-management")
    }

    group = "ws.cnt.ct.scc"
    version = "1.0.0"

    dependencyManagement {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
            mavenBom(SpringBootPlugin.BOM_COORDINATES)
        }
    }
}
