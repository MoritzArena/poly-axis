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
    implementation(libs.quarkus.reactive.pg.client)
    implementation(libs.quarkus.hibernate.reactive.panache)
    testImplementation(libs.quarkus.junit5)
}
// TODO 待解决 单测无法正确加载使用问题
// TODO 待解决 resources不能正确加载问题
tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}
// Configure test task with system properties as in maven-surefire-plugin configuration
tasks.test {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
    systemProperty("maven.home", System.getenv("MAVEN_HOME") ?: "")
}
tasks.register<Test>("integrationTest") {
    group = "verification"
    description = "Run integration tests."

    // Use the test source set or create a new one if needed.
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath

    // Set system properties similar to the Maven configuration.
    systemProperty("native.image.path", "$buildDir/${project.name}-runner")
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
    systemProperty("maven.home", System.getenv("MAVEN_HOME") ?: "")
}