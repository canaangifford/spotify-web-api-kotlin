/* Spotify Web API, Kotlin Wrapper; MIT License, 2017-2021; Original author: Adam Ratzman */
package com.adamratzman.spotify.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

/**
 * The current time in milliseconds since UNIX epoch.
 */
public actual fun getCurrentTimeMs(): Long = System.currentTimeMillis()

internal actual fun formatDate(format: String, date: Long): String {
    return SimpleDateFormat(format, Locale.getDefault()).format(Date.from(Instant.ofEpochMilli(date)))
}
