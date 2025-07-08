pluginManagement {
    val quarkusPluginVersion: String by settings
    val quarkusPluginId: String by settings
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
    plugins {
        id(quarkusPluginId) version quarkusPluginVersion
    }
}
rootProject.name = "poly-axis"

/* ⇢ database provider */
include("persistence")
/* ⇢ dubbo & rpc provider */
include("network")
/* ⇢ common provider */
include("common")
/* ⇢ quarkus security provider */
include("security")

/* ⇢ application broker (downstream) */
include("broker")
/* ⇢ application dispatcher (upstream) */
include("dispatcher")
/* ⇢ application distribution */
include("distribution")