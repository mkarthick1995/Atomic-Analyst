plugins {
    id("com.android.application") version "8.13.2" apply false
    id("org.jetbrains.kotlin.android") version "2.3.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.0" apply false
    id("com.google.devtools.ksp") version "2.3.4" apply false
    id("com.google.dagger.hilt.android") version "2.58" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.5" apply false
}

tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}
