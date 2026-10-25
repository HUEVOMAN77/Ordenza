package dev.spikeysanju.einsen.analytics

/**
 * Local, privacy-friendly replacement for the old Firebase Analytics params.
 * Events are only written to Logcat in debug builds; nothing leaves the device.
 */
object AnalyticsParams {
    const val SCREEN_NAME = "screen_name"
    const val SCREEN_CLASS = "screen_class"
    const val ITEM_ID = "item_id"
    const val ITEM_NAME = "item_name"
    const val CONTENT_TYPE = "content_type"
    const val VALUE = "value"
}
