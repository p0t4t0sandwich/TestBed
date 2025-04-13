plugins {
    id("java-library")
    alias(libs.plugins.shadow)
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
    implementation(variantOf(libs.modapi) {
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

tasks.shadowJar {
    dependencies {
        exclude(dependency("com.mojang:brigadier"))
    }
}

tasks.build.get().dependsOn(tasks.shadowJar)
