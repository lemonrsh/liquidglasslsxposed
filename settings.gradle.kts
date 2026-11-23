pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Legacy Xposed API 82 (used by LSPosed). If this host is down, see README (libs/ fallback).
        maven("https://api.xposed.info/") { content { includeGroup("de.robv.android.xposed") } }
    }
}
rootProject.name = "LiquidGlass"
include(":app")
