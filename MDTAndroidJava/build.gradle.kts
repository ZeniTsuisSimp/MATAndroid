plugins {
    id("com.android.application") version "8.5.2" apply false
}

subprojects {
    val localAppData = System.getenv("LOCALAPPDATA") ?: rootDir.absolutePath
    layout.buildDirectory.set(file("$localAppData\\MDTAndroidJavaBuild\\${project.name}"))
}
