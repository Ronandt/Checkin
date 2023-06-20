package com.example.checkin

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


object TimeConverter {
    fun convertUnixToAMPM(unixTimestamp: Long): String {
        if(unixTimestamp == 0L) {
            return ""
        }
        val dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(unixTimestamp),
            ZoneId.systemDefault()
        )

        // Format LocalDateTime to AM/PM format

        // Format LocalDateTime to AM/PM format
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        var amPmTime = dateTime.format(formatter)

        if(amPmTime[0] == '0') {
            amPmTime = amPmTime.drop(1)
        }

        return amPmTime
    }

    fun convertUnixToHM(unixTimestamp: Long): String {

        val minutes = (unixTimestamp / (1000 * 60)) % 60
        val hours = (unixTimestamp / (1000 * 60 * 60))

        return "$hours hrs $minutes mins"

    }
}