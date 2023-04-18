enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    repositories {
        maven("https://jitpack.io")
        mavenCentral()
        mavenLocal()
    }
}

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
    includeBuild("build-logic")
}

rootProject.name = "Minestom"
include("code-generators")
include("jmh-benchmarks")
include("jcstress-tests")
include("demo")
include("testing")
