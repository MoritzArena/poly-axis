plugins {
    id("java")
    id("java-library")
}

/* ⇢ definitions */
description = "axis-persistence ${project.version} gradle configurations"

/* ⇢ dependencies */
dependencies {
    /* quarkus bom */
    implementation(enforcedPlatform(libs.quarkus))
    /* quarkus internal */
    api(libs.quarkus.arc)
    api(libs.quarkus.vertx)
    /* quarkus rest & jackson */
    api(libs.quarkus.rest.jackson)
    /* quarkus reactive database */
    // implementation(libs.lettuce.core)
    // implementation(libs.quarkus.redis.client)
    // implementation(libs.quarkus.mongodb.panache)
    // implementation(libs.quarkus.cassandra.client)
    // implementation(libs.quarkus.reactive.pg.client)
    // implementation(libs.quarkus.hibernate.reactive.panache)
}