plugins {
    id("java-library-conventions")
}

dependencies {
    implementation(project(":catalog"))
    implementation(project(":collecting"))
}
