plugins {
    id("kotlin-library-conventions")
}

dependencies {
    implementation(project(":catalog"))
    implementation(project(":collecting"))

    implementation("com.auth0:java-jwt:3.18.3")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-security")
}
