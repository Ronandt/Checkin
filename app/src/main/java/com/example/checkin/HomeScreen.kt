package com.example.checkin

import android.content.Context
import android.os.CountDownTimer
import java.util.UUID
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import okhttp3.ResponseBody
import okhttp3.internal.toLongOrDefault
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date


@Composable
fun HomeScreen(timer: CountDownTimer, navController: NavController, context: Context) {
    var checkSessionInfo by remember {mutableStateOf<JSONObject?>(null)}

    var data by remember {mutableStateOf<JSONArray?>(null)}
    var totalTime by remember {mutableStateOf<String?>(null)}

    var weeklyRecordCharts = remember { mutableStateMapOf<String, Int>("Monday" to 0, "Tuesday" to 0, "Wednesday" to 0, "Thursday" to 0, "Friday" to 0, "Saturday" to 0, "Sunday" to 0) }
    LaunchedEffect(Unit) {

        try {
            checkSessionInfo =CheckInService.API.getCheckedInDetails("123").body()?.string()?.let { JSONObject(it) }
            data = CheckInService.API.getRecords("123").body()?.string()?.let {JSONObject(it)}?.getJSONObject("result")?.getJSONArray("data")
            val allRecordsInfo: JSONArray? = data?.getJSONObject(data?.length()?.minus(1) ?: 0)?.getJSONArray("days")
            var info = checkSessionInfo?.getJSONObject("result")?.getJSONArray("data")
            println(checkSessionInfo?.getJSONObject("result")?.getJSONArray("data")?.getJSONObject(0)?.getJSONArray("last_checked_in")?.getJSONObject(0)?.getString("date"))
            println(checkSessionInfo?.getJSONObject("result")?.getJSONArray("data")?.getJSONObject(0)?.getJSONArray("last_checked_in")?.getJSONObject(0))
            var total = 0L
            for(i in 0 until allRecordsInfo?.length()!!) {
                weeklyRecordCharts[Instant.ofEpochMilli(allRecordsInfo.getJSONObject(i).getLong("time_in"))
                    .atZone(java.time.ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("EEEE")).toString()] = weeklyRecordCharts[Instant.ofEpochMilli(allRecordsInfo.getJSONObject(i).getLong("time_in"))
                    .atZone(java.time.ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("EEEE")).toString()]!! + 1

                if(allRecordsInfo.getJSONObject(i).getString("date") ==   LocalDate.now().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy"))) {

                    total += (if(allRecordsInfo.getJSONObject(i).getString("time_out") == "0") System.currentTimeMillis() else allRecordsInfo.getJSONObject(i).getString("time_out").toLongOrDefault(0) ) - allRecordsInfo!!.getJSONObject(i).getLong("time_in")


                }
            }
            println(weeklyRecordCharts.toMap())
            totalTime = TimeConverter.convertUnixToHM(total)

            val db = LocalDataSource(context).getDatabase().userDao()
            for(i in 0 until data?.length()!!) {
                var days = data?.getJSONObject(i)?.getJSONArray("days")

                for(x in 0 until (days?.length() ?: 0)) {
                    var time_out  = days?.getJSONObject(i)?.getString("time_out")
                    var time_in = days?.getJSONObject(i)?.getString("time_in")

                    var date = days?.getJSONObject(i)?.getString("date")
                    if(!days?.getJSONObject(i)?.getString("entry_id")?.let { db.recordExists(it) }!!) {
                        if (time_in != null) {
                            if (time_out != null) {
                                days?.getJSONObject(i)?.getString("entry_id")
                                    ?.let { Records(id = it,timeIn = time_in.toLongOrDefault(0L), timeOut = time_out.toLongOrDefault(0),date = date!!, new = false, accessKey = "123") }
                                    ?.let { db.storeAllRecords(it) }
                            }
                        }
                    }

                }

            }
        } catch(e: Exception) {

        }





        timer.start()

    }

    LaunchedEffect(Unit) {


    }


    val sharedPreferences = LocalContext.current.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val userSharedPreferences = LocalContext.current.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
    var openDialog by remember { mutableStateOf(sharedPreferences.getString("biometricsEnabled", null) == null && userSharedPreferences.getString("accountid", null) != null) }

    if(openDialog) {
        AlertDialog(onDismissRequest = { }, buttons= {
            Row() {
                Button(onClick = { openDialog = false
                    with(sharedPreferences.edit()) {
                        putString("biometricsEnabled", "enabled")
                        apply()
                    }
                }) {
                    Text("Enable")
                }
                Button(onClick = {openDialog = false
                    with(sharedPreferences.edit()) {
                        putString("biometricsEnabled", "disabled")
                        apply()
                    }
                }) {
                    Text("Disable")
                }
            }
        }, title= { Text("Biometrics") }, text = { Text("Would you like to enable biometrics?") })
    }
    
    
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement =  Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Card(modifier = Modifier
            .fillMaxWidth(0.95f)
            .height(150.dp),  elevation = 8.dp) {
            Column {
                Text("Total time clocked in today", modifier = Modifier.padding(bottom = 10.dp, top = 10.dp))
                Text( "" +
                        "" + if(checkSessionInfo?.getJSONObject("result")?.getJSONArray("data")?.getJSONObject(0)?.getJSONArray("last_checked_in")?.getJSONObject(0)?.getString("date") == LocalDate.now().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy"))) {

                            totalTime} else {
                                                                       "Have not checked in yet"
                }
, fontSize = 50.sp, modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally), fontWeight = FontWeight.Light, textAlign = TextAlign.Center
                )
            }
        }
        Card(modifier = Modifier
            .fillMaxWidth(0.95f)
            .height(150.dp),  elevation = 8.dp) {
            Column {
                Text("Last checked in: "  + checkSessionInfo?.getJSONObject("result")?.getJSONArray("data")?.getJSONObject(0)?.getJSONArray("last_checked_in")?.getJSONObject(0)?.getString("date"), modifier = Modifier.padding(bottom = 10.dp, top = 10.dp))


                Text(
                            "" + checkSessionInfo?.getJSONObject("result")?.getJSONArray("data")?.getJSONObject(0)?.getJSONArray("last_checked_in")?.getJSONObject(0)?.getString("time")?.toLongOrDefault(0)
                                ?.let { TimeConverter.convertUnixToAMPM(it) }, fontSize = 50.sp, modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally), fontWeight = FontWeight.Light, textAlign = TextAlign.Center
                )

            }
        }
        Card(modifier = Modifier
            .fillMaxWidth(0.95f)
            .height(150.dp), elevation = 8.dp) {
            Column {
                Text("Last checked out: " + checkSessionInfo?.getJSONObject("result")?.getJSONArray("data")?.getJSONObject(1)?.getJSONArray("last_checked_out")?.getJSONObject(0)?.getString("date"), modifier = Modifier.padding(bottom=10.dp, top = 10.dp))
                checkSessionInfo?.getJSONObject("result")?.getJSONArray("data")?.getJSONObject(1)?.getJSONArray("last_checked_out")?.getJSONObject(0)?.getString("time")?.toLongOrDefault(0)
                    ?.let {
                        TimeConverter.convertUnixToAMPM(it)
                            ?.let {
                                Text(
                                    it, fontSize = 50.sp, modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.CenterHorizontally), fontWeight = FontWeight.Light, textAlign = TextAlign.Center)
                            }
                    }
            }

        }
    }
    


}
