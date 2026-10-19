package com.example.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

enum class ExpiryStatus {
    EXPIRED,        // Datum < idag
    WITHIN_30_DAYS, // 0..30 dagar
    WITHIN_90_DAYS, // 31..90 dagar
    VALID,          // > 90 dagar
    NONE            // Inget datum angivet
}

data class ExpiryInfo(
    val status: ExpiryStatus,
    val daysRemaining: Long?,
    val displayText: String,
    val badgeLabel: String
)

object DateUtils {

    private val ISO_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val SWEDISH_DATE_FORMATTER = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("sv", "SE"))

    /**
     * Kontrollerar och beräknar utgångsstatus utifrån dagens datum.
     */
    fun getExpiryInfo(dateStr: String?): ExpiryInfo {
        if (dateStr.isNullOrBlank()) {
            return ExpiryInfo(
                status = ExpiryStatus.NONE,
                daysRemaining = null,
                displayText = "Inget utgångsdatum angivet",
                badgeLabel = "Saknar datum"
            )
        }

        return try {
            val expiryDate = LocalDate.parse(dateStr.trim(), ISO_DATE_FORMATTER)
            val today = LocalDate.now()
            val days = ChronoUnit.DAYS.between(today, expiryDate)

            when {
                days < 0 -> {
                    val daysAgo = -days
                    val text = if (daysAgo == 1L) "Gick ut igår" else "Gick ut för $daysAgo dagar sedan (${formatSwedishDate(expiryDate)})"
                    ExpiryInfo(
                        status = ExpiryStatus.EXPIRED,
                        daysRemaining = days,
                        displayText = text,
                        badgeLabel = "Utgånget"
                    )
                }
                days == 0L -> {
                    ExpiryInfo(
                        status = ExpiryStatus.WITHIN_30_DAYS,
                        daysRemaining = 0,
                        displayText = "Går ut idag!",
                        badgeLabel = "Går ut idag"
                    )
                }
                days <= 30 -> {
                    val text = if (days == 1L) "Går ut imorgon" else "Går ut om $days dagar (${formatSwedishDate(expiryDate)})"
                    ExpiryInfo(
                        status = ExpiryStatus.WITHIN_30_DAYS,
                        daysRemaining = days,
                        displayText = text,
                        badgeLabel = "$days d kvar"
                    )
                }
                days <= 90 -> {
                    ExpiryInfo(
                        status = ExpiryStatus.WITHIN_90_DAYS,
                        daysRemaining = days,
                        displayText = "Går ut om $days dagar (${formatSwedishDate(expiryDate)})",
                        badgeLabel = "$days d kvar"
                    )
                }
                else -> {
                    ExpiryInfo(
                        status = ExpiryStatus.VALID,
                        daysRemaining = days,
                        displayText = "Giltigt till ${formatSwedishDate(expiryDate)}",
                        badgeLabel = "Giltigt"
                    )
                }
            }
        } catch (_: Exception) {
            ExpiryInfo(
                status = ExpiryStatus.NONE,
                daysRemaining = null,
                displayText = dateStr,
                badgeLabel = "Okänt"
            )
        }
    }

    fun formatSwedishDate(date: LocalDate): String {
        return date.format(SWEDISH_DATE_FORMATTER)
    }

    fun isValidIsoDate(str: String): Boolean {
        return try {
            LocalDate.parse(str.trim(), ISO_DATE_FORMATTER)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun todayIsoString(): String {
        return LocalDate.now().format(ISO_DATE_FORMATTER)
    }
}
