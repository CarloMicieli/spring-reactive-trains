plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.3.0")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:2.6.3")
    implementation("io.spring.gradle:dependency-management-plugin:1.0.11.RELEASE")
    implementation("me.qoomon:gradle-git-versioning-plugin:5.1.5")
}