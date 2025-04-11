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
    setupSourceSets("forge") // "fabric"
}

@Suppress("UnstableApiUsage")
configurations {
    val mainCompileOnly by creating
    // val modImplementation by creating
    setupConfigurations("forge") // "fabric"
}

// ------------------------------------------- Vanilla -------------------------------------------
unimined.minecraft {
    version(minecraftVersion)
    mappings {
        searge()
        mcp(mappingsChannel, mappingsVersion)
    }
    defaultRemapJar = false
}

tasks.jar {
    archiveClassifier.set("vanilla")
}

// ------------------------------------------- Fabric -------------------------------------------
//unimined.minecraft(sourceSets.getByName("fabric")) {
//    combineWith(sourceSets.main.get())
//    fabric {
//        loader(fabricLoaderVersion)
//    }
//    defaultRemapJar = true
//}
//
//tasks.named<RemapJarTask>("remapFabricJar") {
//    asJar.archiveClassifier.set("fabric-remap")
//    mixinRemap {
//        disableRefmap()
//    }
//}
//
//tasks.register<ShadowJar>("relocateFabricJar") {
//    dependsOn("remapFabricJar")
//    from(jarToFiles("remapFabricJar"))
//    archiveClassifier.set("fabric")
//    dependencies {
//        exclude("dev/neuralnexus/testbed/mixin/v1_13_2/vanilla/**")
//    }
//    relocate("dev.neuralnexus.testbed.v1_13_2.vanilla", "dev.neuralnexus.testbed.v1_13_2.y_intmdry")
//}

// ------------------------------------------- Forge -------------------------------------------
unimined.minecraft(sourceSets.getByName("forge")) {
    combineWith(sourceSets.main.get())
    minecraftForge {
        loader(forgeVersion)
        // mixinConfig("testbed.mixins.v1_13_2.forge.json")
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
        exclude("dev/neuralnexus/testbed/mixin/v1_13_2/vanilla/**")
    }
    relocate("dev.neuralnexus.testbed.v1_13_2.vanilla", "dev.neuralnexus.testbed.v1_13_2.l_searge")
}

// ------------------------------------------- Common -------------------------------------------
dependencies {
    setupCompileDeps(listOf(
        project(":api"),
    ), "main", "forge") // "fabric"

    setupCompileDeps(listOf(
        libs.mixin,
    ), "main")

    setupRuntimeDeps(listOf(
        project(":api"),
    ), "forge") // "fabric"

//    listOf(
//        "fabric-api-base",
//        "fabric-command-api-v1",
//        "fabric-lifecycle-events-v1",
//        "fabric-networking-api-v1"
//    ).forEach {
//        "fabricModImplementation"(fabricApi.fabricModule(it, fabricVersion))
//    }
}

tasks.shadowJar {
    listOf(
//        "relocateFabricJar",
        "relocateForgeJar"
    ).forEach {
        dependsOn(it)
        from(jarToFiles(it))
    }
    archiveClassifier.set("")
}

tasks.build.get().dependsOn(tasks.shadowJar)
