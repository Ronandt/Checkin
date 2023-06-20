package com.example.checkin

import android.content.Context
import android.os.CountDownTimer
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
import androidx.compose.runtime.getValue
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
import org.json.JSONObject
import java.util.Date


@Composable
fun HomeScreen(timer: CountDownTimer, navController: NavController) {
    var checkSessionInfo by remember {mutableStateOf<JSONObject?>(null)}
    LaunchedEffect(Unit) {

       checkSessionInfo =CheckInService.API.getCheckedInDetails("123").body()?.string()?.let { JSONObject(it) }
        var info = checkSessionInfo?.getJSONObject("result")?.getJSONArray("data")
        println(checkSessionInfo?.getJSONObject("result")?.getJSONArray("data")?.getJSONObject(0)?.getJSONArray("last_checked_in")?.getJSONObject(0)?.getString("date"))
        println(checkSessionInfo?.getJSONObject("result")?.getJSONArray("data")?.getJSONObject(0)?.getJSONArray("last_checked_in")?.getJSONObject(0))


        timer.start()

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
        Card(modifier = Modifier.fillMaxWidth(0.95f).height(150.dp),  elevation = 8.dp) {
            Column {
                Text("Total time clocked in today", modifier = Modifier.padding(bottom = 10.dp, top = 10.dp))
                Text( "" +
                        "" + (System.currentTimeMillis().toLong() - (checkSessionInfo?.getJSONObject("result")
                    ?.getJSONArray("data")?.getJSONObject(0)?.getJSONArray("last_checked_in")
                    ?.getJSONObject(0)?.getString("time")?.toLongOrNull() ?: System.currentTimeMillis()))
, fontSize = 50.sp, modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally), fontWeight = FontWeight.Light, textAlign = TextAlign.Center
                )
            }
        }
        Card(modifier = Modifier.fillMaxWidth(0.95f).height(150.dp),  elevation = 8.dp) {
            Column {
                Text("Last checked in: "  + checkSessionInfo?.getJSONObject("result")?.getJSONArray("data")?.getJSONObject(0)?.getJSONArray("last_checked_in")?.getJSONObject(0)?.getString("date"), modifier = Modifier.padding(bottom = 10.dp, top = 10.dp))


                Text(
                            "" + checkSessionInfo?.getJSONObject("result")?.getJSONArray("data")?.getJSONObject(0)?.getJSONArray("last_checked_in")?.getJSONObject(0)?.getString("date"), fontSize = 50.sp, modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally), fontWeight = FontWeight.Light, textAlign = TextAlign.Center
                )

            }
        }
        Card(modifier = Modifier.fillMaxWidth(0.95f).height(150.dp), elevation = 8.dp) {
            Column {
                Text("Last checked out: " + checkSessionInfo?.getJSONObject("result")?.getJSONArray("data")?.getJSONObject(1)?.getJSONArray("last_checked_out")?.getJSONObject(0)?.getString("date"), modifier = Modifier.padding(bottom=10.dp, top = 10.dp))
                checkSessionInfo?.getJSONObject("result")?.getJSONArray("data")?.getJSONObject(1)?.getJSONArray("last_checked_out")?.getJSONObject(0)?.getString("time")
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
