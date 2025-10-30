import java.nio.file.Files
import java.nio.file.Paths

System.getenv("JAVA_HOME")
    ?.let { Paths.get(it) }
    ?.takeIf { Files.exists(it.resolve("bin").resolve("jlink")) }
    ?.let { System.setProperty("org.gradle.java.home", it.toString()) }

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PlataHackhathon"
include(":app")
 