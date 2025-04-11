import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import xyz.wagyourtail.unimined.api.minecraft.task.RemapJarTask

plugins {
    alias(libs.plugins.shadow)
    id(libs.plugins.unimined.get().pluginId)
}

base {
    archivesName = "${projectId}-${minecraftVersion}"
}

java.toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
java.sourceCompatibility = JavaVersion.toVersion(javaVersion)
java.targetCompatibility = JavaVersion.toVersion(javaVersion)

sourceSets {
    setupSourceSets("fabric", "forge", "neoforge")
}

@Suppress("UnstableApiUsage")
configurations {
    val mainCompileOnly by creating
    val modImplementation by creating
    setupConfigurations("fabric", "forge", "neoforge")
}

// ------------------------------------------- Vanilla -------------------------------------------
unimined.minecraft {
    version(minecraftVersion)
    mappings {
        mojmap()
        devFallbackNamespace("official")
    }
    defaultRemapJar = false
}

tasks.jar {
    archiveClassifier.set("vanilla")
}

// ------------------------------------------- Fabric -------------------------------------------
unimined.minecraft(sourceSets.getByName("fabric")) {
    combineWith(sourceSets.main.get())
    fabric {
        loader(fabricLoaderVersion)
    }
    defaultRemapJar = true
}

tasks.named<RemapJarTask>("remapFabricJar") {
    asJar.archiveClassifier.set("fabric-remap")
    mixinRemap {
        disableRefmap()
    }
}

tasks.register<ShadowJar>("relocateFabricJar") {
    dependsOn("remapFabricJar")
    from(jarToFiles("remapFabricJar"))
    archiveClassifier.set("fabric")
    dependencies {
        exclude("dev/neuralnexus/testbed/mixin/v1_20_4/vanilla/**")
    }
    relocate("dev.neuralnexus.testbed.v1_20_4.vanilla", "dev.neuralnexus.testbed.v1_20_4.y_intmdry")
}

// ------------------------------------------- Forge -------------------------------------------
unimined.minecraft(sourceSets.getByName("forge")) {
    combineWith(sourceSets.main.get())
    minecraftForge {
        loader(forgeVersion)
        mixinConfig("testbed.mixins.v1_20_4.forge.json")
    }
    defaultRemapJar = true
}

tasks.named<RemapJarTask>("remapForgeJar") {
    asJar.archiveClassifier.set("forge-remap")
    mixinRemap {
        disableRefmap()
    }
}

tasks.register<ShadowJar>("relocateForgeJar") {
    dependsOn("remapForgeJar")
    from(jarToFiles("remapForgeJar"))
    archiveClassifier.set("forge")
    dependencies {
        exclude("dev/neuralnexus/testbed/mixin/v1_20_4/vanilla/**")
    }
    relocate("dev.neuralnexus.testbed.v1_20_4.vanilla", "dev.neuralnexus.testbed.v1_20_4.searge")
}

// ------------------------------------------- NeoForge -------------------------------------------
unimined.minecraft(sourceSets.getByName("neoforge")) {
    combineWith(sourceSets.main.get())
    neoForge {
        loader(neoForgeVersion)
    }
    defaultRemapJar = false
}

tasks.register<ShadowJar>("relocateNeoForgeJar") {
    from(sourceSets.getByName("neoforge").output)
    archiveClassifier.set("neoforge")
    dependencies {
        exclude("dev/neuralnexus/testbed/mixin/v1_20_4/vanilla/**")
        exclude("dev/neuralnexus/testbed/v1_20_4/vanilla/**")
    }
}

// ------------------------------------------- Common -------------------------------------------
dependencies {
    setupCompileDeps(listOf(
        project(":api"),
        ), "main", "fabric", "forge", "neoforge")

    setupCompileDeps(listOf(
        libs.mixin,
        ), "main")

    setupRuntimeDeps(listOf(
        project(":api"),
        ), "fabric", "forge", "neoforge")

    listOf(
        "fabric-api-base",
        "fabric-command-api-v2",
        "fabric-lifecycle-events-v1",
        "fabric-networking-api-v1"
    ).forEach {
        "fabricModImplementation"(fabricApi.fabricModule(it, fabricVersion))
    }
}

tasks.shadowJar {
    listOf(
        "relocateFabricJar",
        "relocateForgeJar",
        "relocateNeoForgeJar"
    ).forEach {
        dependsOn(it)
        from(jarToFiles(it))
    }
    archiveClassifier.set("")
}

tasks.build.get().dependsOn(tasks.shadowJar)
