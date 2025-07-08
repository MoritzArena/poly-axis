plugins {
    id("java")
    id("java-library")
    id("io.quarkus")
}

/* ⇢ definitions */
description = "axis-persistence ${project.version} gradle configurations"

/* ⇢ dependencies */
dependencies {
    /* quarkus bom */
    implementation(enforcedPlatform(libs.quarkus))
    /* internal projects */
    implementation(project(":common"))

    /* quarkus reactive database */
    // implementation(libs.lettuce.core)
    // implementation(libs.quarkus.redis.client)
    // implementation(libs.quarkus.mongodb.panache)
    // implementation(libs.quarkus.cassandra.client)
    implementation(libs.quarkus.reactive.pg.client)
    implementation(libs.quarkus.hibernate.reactive.panache)

    /* test */
    testImplementation(libs.quarkus.junit5)
    testImplementation(libs.quarkus.test.vertx)
}
// TODO 待解决 单测无法正确加载使用问题
// TODO 待解决 resources不能正确加载问题
// Configure test task with system properties as in maven-surefire-plugin configuration
tasks.test {
    useJUnitPlatform()
}
tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}