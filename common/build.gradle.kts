plugins {
    id("java")
    id("java-library")
}

/* ⇢ definitions */
description = "axis-common ${project.version} gradle configurations"

/* ⇢ dependencies */
dependencies {
    /* quarkus bom */
    implementation(enforcedPlatform(libs.quarkus))
    /* microprofile config */
    compileOnly(libs.microprofile.config.api)
    /* jackson layer */
    implementation(platform(libs.jackson.bom))
    compileOnly(libs.jackson.annotations)
    compileOnly(libs.jackson.core)
    compileOnly(libs.jackson.databind)
}