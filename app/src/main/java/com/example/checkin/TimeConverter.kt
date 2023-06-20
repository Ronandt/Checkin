package com.example.checkin

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


object TimeConverter {
    fun convertUnixToAMPM(unixTimestamp: Long): String {
        val dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(unixTimestamp),
            ZoneId.systemDefault()
        )

        // Format LocalDateTime to AM/PM format

        // Format LocalDateTime to AM/PM format
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        val amPmTime = dateTime.format(formatter)
        return amPmTime
    }
}