plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
}

subprojects {
    val localAppData = System.getenv("LOCALAPPDATA") ?: rootDir.absolutePath
    layout.buildDirectory.set(file("$localAppData\\MDTAndroidBuild\\${project.name}"))
}
