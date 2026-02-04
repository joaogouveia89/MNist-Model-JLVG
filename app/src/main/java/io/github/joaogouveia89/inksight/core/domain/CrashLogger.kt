package io.github.joaogouveia89.inksight.core.domain

/**
 * Interface for logging non-fatal exceptions and fatal crashes.
 * Decouples the app from specific implementations like Firebase Crashlytics.
 */
interface CrashLogger {
    fun logException(throwable: Throwable)
    fun logMessage(message: String)
    fun setCustomKey(key: String, value: String)
}
