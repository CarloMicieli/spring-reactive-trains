plugins {
    id("kotlin-library-conventions")
}

dependencies {
    implementation(project(":catalog"))
    implementation(project(":collecting"))
}
