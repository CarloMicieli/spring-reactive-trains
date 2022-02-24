plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.3.0")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:2.6.4")
    implementation("io.spring.gradle:dependency-management-plugin:1.0.11.RELEASE")
    implementation("me.qoomon:gradle-git-versioning-plugin:5.1.5")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    implementation("org.jetbrains.kotlin:kotlin-allopen:1.6.10")
    implementation("org.jetbrains.kotlinx:kover:0.5.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
}