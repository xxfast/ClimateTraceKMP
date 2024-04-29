package dev.johnoreilly.climatetrace.di

import dev.johnoreilly.climatetrace.remote.Country
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import io.github.xxfast.kstore.file.utils.DocumentDirectory
import io.github.xxfast.kstore.utils.ExperimentalKStoreApi
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSFileManager

// called by iOS etc
fun initKoin() = initKoin(enableNetworkLogs = false) {}

@OptIn(ExperimentalKStoreApi::class)
actual fun dataModule(): Module = module {
    single<KStore<List<Country>>> {
        val filesDir: String? = NSFileManager.defaultManager.DocumentDirectory?.relativePath
        requireNotNull(filesDir) { "Document directory not found" }
        storeOf(file = "$filesDir/countries.json".toPath(), default = emptyList())
    }
}
