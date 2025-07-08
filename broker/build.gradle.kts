plugins {
    id("java")
}

repositories {
    maven {
        url = uri("https://maven.aliyun.com/repository/public")
    }
    maven {
        url = uri("https://packages.aliyun.com/65442f8f3aaed849b035c821/maven/repo-dubbo-mutiny")
        credentials {
            username = "63c4e472b7bea95c53d1625e"
            password = "SJFkqm-_akW_"
        }
    }
}

/* ⇢ definitions */
description = "axis-broker ${project.version} gradle configurations"

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}