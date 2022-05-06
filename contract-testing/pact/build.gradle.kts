import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

subprojects {
    apply {
        plugin("io.spring.dependency-management")
    }
    the<DependencyManagementExtension>().apply {
        dependencies {
            dependency("au.com.dius.pact.consumer:junit5:4.3.6")
            dependency("au.com.dius.pact.provider:junit5:4.3.6")
        }
    }
}
