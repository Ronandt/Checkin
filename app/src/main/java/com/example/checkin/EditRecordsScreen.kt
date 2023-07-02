package com.example.checkin

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import okhttp3.internal.toLongOrDefault
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecordsScreen(info: String,date: String, timeIn: String, timeOut: String, navController: NavController) {
    var checkIn by rememberSaveable { mutableStateOf(TimeConverter.convertUnixToAMPM(timeIn.toLongOrDefault(0))) }
    var checkOut by rememberSaveable {mutableStateOf(TimeConverter.convertUnixToAMPM(timeOut.toLongOrDefault(0)))}
    var date by rememberSaveable {mutableStateOf(date)}
    val scope = rememberCoroutineScope()
    var validation = remember(checkIn, checkOut,date) {
        derivedStateOf {
            isValidDate(date, "dd/MM/yyyy") && isValidAmPm(checkIn) && isValidAmPm(checkOut)
        }
    }
    Column() {
        OutlinedTextField(date, onValueChange = {date = it}, label = {Text("Date")})
        OutlinedTextField(checkIn, onValueChange = {checkIn = it}, label = {Text("Check in")})
   OutlinedTextField(checkOut, onValueChange = {checkOut = it}, label = {Text("Check out")})

        Button(onClick = {
            scope.launch {
                if(isValidDate(date, "dd/MM/yyyy") && isValidAmPm(checkIn) && isValidAmPm(checkOut)) {

                    CheckInService.API.editEntry(UpdateRequest(date, "123", info, convertDateTimeToUnix(date, checkIn), convertDateTimeToUnix(date, checkOut)))
                    navController.navigateUp()

                }

            }
        }) {
            Text("Save")
        }

        if(!(validation.value)) {
            Text("Incorrect format")
        }
    }
}

fun isValidDate(dateString: String, format: String): Boolean {
    val dateFormat = SimpleDateFormat(format)
    dateFormat.isLenient = false

    try {
        dateFormat.parse(dateString)
        return true
    } catch (e: Exception) {
        return false
    }
}

fun isValidAmPm(timeString: String): Boolean {
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    dateFormat.isLenient = false

    try {
        dateFormat.parse(timeString)
        return true
    } catch (e: Exception) {
        return false
    }
}

fun convertDateTimeToUnix(dateString: String, timeString: String): Long {
    val dateTimeString = "$dateString $timeString"
    val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH)
    val dateTime = dateFormat.parse(dateTimeString)

    val calendar = Calendar.getInstance()
    calendar.time = dateTime

    return calendar.timeInMillis
}