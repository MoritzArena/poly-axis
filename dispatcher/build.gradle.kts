plugins {
    id("java")
    id("io.quarkus")
}

/* ⇢ definitions */
description = "axis-dispatcher ${project.version} gradle configurations"

dependencies {
    /* quarkus bom */
    implementation(enforcedPlatform(libs.quarkus))
    /* quarkus reactive web */
    implementation(libs.quarkus.reactive.routes)
    /* quarkus langchain4j */
    implementation(libs.quarkus.langchain4j.core)
    implementation(libs.quarkus.langchain4j.openai)
    /* internal projects */
    implementation(project(":api"))
    implementation(project(":network")) // for rpc negotiating
}