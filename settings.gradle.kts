pluginManagement {
    repositories {
        mavenLocal()
        maven("https://maven.neuralnexus.dev/mirror")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.8.0")
}

rootProject.name = "testbed"

val legacyVersions = listOf(
    "1_7_10",
    "1_8_9",
    "1_9_4",
    "1_10_2",
    "1_11_2",
    "1_12_2"
).forEach { version ->
    include(":versions:legacy:v$version")
    project(":versions:legacy:v$version").projectDir = file("versions/legacy/v$version")
}

val modernVersions = listOf(
    "1_13_2",
    "1_14_4",
    "1_15_2",
    "1_16_1",
    "1_16_5",
    "1_17_1",
    "1_18",
    "1_18_2",
    "1_19",
    "1_19_2",
    "1_19_3",
    "1_19_4",
    "1_20_1",
    "1_20_2",
    "1_20_4",
    "1_20_6",
    "1_21_1",
    "1_21_3",
    "1_21_4",
    "1_21_5"
).forEach { version ->
    include(":versions:modern:v$version")
    project(":versions:modern:v$version").projectDir = file("versions/modern/v$version")
}
