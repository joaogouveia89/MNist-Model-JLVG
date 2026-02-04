package io.github.joaogouveia89.inksight.core.data

import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.joaogouveia89.inksight.core.domain.CrashLogger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseCrashLogger @Inject constructor() : CrashLogger {

    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun logException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }

    override fun logMessage(message: String) {
        crashlytics.log(message)
    }

    override fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }
}
