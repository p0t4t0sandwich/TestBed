plugins {
    id("java-library")
}

base {
    archivesName = "bedapi"
}

dependencies {
    // Brigadier
    api(libs.brigadier)

    // Mixin
    compileOnly(libs.mixin)
    compileOnly(libs.asm.tree)

    // Tooling
    api(variantOf(libs.modapi) {
        classifier("downgraded-8-shaded")
    })
}

java.disableAutoTargetJvm()
java {
    withSourcesJar()
    withJavadocJar()
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    targetCompatibility = JavaVersion.toVersion(javaVersion)
}
