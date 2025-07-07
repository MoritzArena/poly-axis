plugins {
    id("java")
    id("java-library")
    id("io.quarkus")
}

/* ⇢ definitions */
description = "axis-api ${project.version} gradle configurations"

/* ⇢ dependencies */
dependencies {
    /* quarkus bom */
    implementation(enforcedPlatform(libs.quarkus))
    /* internal projects */
    api(project(":persistence"))
    /* test */
    testImplementation(libs.quarkus.junit5)
    testImplementation(libs.rest.assured)
}