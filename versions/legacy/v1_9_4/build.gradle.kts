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
    create("fabric") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
    create("forge") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

@Suppress("UnstableApiUsage")
configurations {
    val mainCompileOnly by creating
    named("compileOnly") {
        extendsFrom(configurations.getByName("fabricCompileOnly"))
        extendsFrom(configurations.getByName("forgeCompileOnly"))
    }
    val modImplementation by creating
    named("modImplementation") {
        extendsFrom(configurations.getByName("fabricImplementation"))
    }
}

// ------------------------------------------- Vanilla -------------------------------------------
unimined.minecraft {
    version(minecraftVersion)
    mappings {
        calamus()
        feather(28)

        stub.withMappings("searge", "intermediary") {
            // METHODs net/minecraft/unmapped/C_9482745/[m_9076954, getMaxSpeed]()D -> getMaxSpeed
            c(
                "aah",
                listOf(
                    "net/minecraft/entity/item/EntityMinecart",
                    "net/minecraft/entity/vehicle/MinecartEntity"
                )
            ) {
                m("getMaxSpeed", "()D", "m_9076954", "getMaxSpeedForge")
            }
        }
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
        exclude("dev/neuralnexus/testbed/mixin/v1_9_4/vanilla/**")
    }
    relocate("dev.neuralnexus.testbed.v1_9_4.vanilla", "dev.neuralnexus.testbed.v1_9_4.l_intmdry")
}

// ------------------------------------------- Forge -------------------------------------------
unimined.minecraft(sourceSets.getByName("forge")) {
    combineWith(sourceSets.main.get())
    minecraftForge {
        loader(forgeVersion)
        mixinConfig("testbed.mixins.v1_9_4.forge.json")
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
        exclude("dev/neuralnexus/testbed/mixin/v1_9_4/vanilla/**")
    }
    relocate("dev.neuralnexus.testbed.v1_9_4.vanilla", "dev.neuralnexus.testbed.v1_9_4.l_searge")
}

// ------------------------------------------- Common -------------------------------------------
dependencies {
    listOf(
        libs.mixin,
    ).forEach {
        "mainCompileOnly"(it)
        "fabricCompileOnly"(it)
        "forgeCompileOnly"(it)
    }

    listOf(
        "legacy-fabric-api-base",
        "legacy-fabric-command-api-v2",
        "legacy-fabric-lifecycle-events-v1",
        "legacy-fabric-networking-api-v1",
        "legacy-fabric-permissions-api-v1"
    ).forEach {
        "fabricModImplementation"(fabricApi.legacyFabricModule(it, fabricVersion))
    }
}

tasks.shadowJar {
    listOf(
        "relocateFabricJar",
        "relocateForgeJar"
    ).forEach {
        dependsOn(it)
        from(jarToFiles(it))
    }
    archiveClassifier.set("")
}

tasks.build.get().dependsOn(tasks.shadowJar)
