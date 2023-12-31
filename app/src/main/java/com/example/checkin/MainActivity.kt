package com.example.checkin

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.FileUtils
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Switch
import androidx.compose.material.TopAppBar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.Card
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.TextButton

import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.checkin.ui.theme.CheckinTheme
import com.google.android.gms.common.util.IOUtils
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.UUID

class MainActivity : FragmentActivity() {
    private lateinit var logoutCountdownTimer: CountDownTimer
    private var configState by mutableStateOf("Portrait")
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var email by rememberSaveable {
                mutableStateOf("")
            }

            var password by rememberSaveable {
                mutableStateOf("")
            }
            val navControllerState = rememberNavController()
            val scope = rememberCoroutineScope()
            println("COMPOSED")
            val cp = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

            }
            LaunchedEffect(key1 = Unit ) {

                cp.launch(android.Manifest.permission.CAMERA)
                cp.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }



                logoutCountdownTimer = object : CountDownTimer(60000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        println("Seconds remaining: " + millisUntilFinished/1000)
                    }

                    override fun onFinish() {
                        var sharedPref = applicationContext.getSharedPreferences("userInfo",Context.MODE_PRIVATE)

                        with(sharedPref.edit()) {
                            this.clear()
                            apply()
                        }

                        if(navControllerState.currentBackStackEntry?.destination?.route != "login") {
                            navControllerState.navigate("login") {
                                launchSingleTop = true
                            }
                            email = ""
                            password = ""

                        }

                    }
                }


            //val scaffoldState = rememberScaffoldState()
            CheckinTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                    
                ) {


                        Scaffold(bottomBar = {
                            if(navControllerState.currentBackStackEntryAsState().value?.destination?.route !in listOf("login", "editProfile", "changePassword", "changeImage")) NavBar(navState = navControllerState)
                        }) {
                            Box(modifier = Modifier.padding(it)) {
                                NavHost(navController = navControllerState, "login", modifier = Modifier) {
                                    composable("login") {
                                        LoginScreen(email = email, password = password ,emailCallback = {

                                            email = it}, passwordCallback = {password = it}, navigationCallback = {
                                            navControllerState.navigate("home")
                                        }, navControllerState, this@MainActivity)
                                    }
                                    composable("updateProfile") {
                                        UpdateProfileScreen(navControllerState, context = this@MainActivity)
                                    }
                                    composable("home") {
                                        androidx.compose.material.Scaffold(floatingActionButton = { FloatingActionButton(
                                            onClick = { navControllerState.navigate("scanCode") }, backgroundColor = Color.LightGray) {

                                            Icon(painter = painterResource(R.drawable.baseline_qr_code_scanner_24), contentDescription = "Scan qr code")
                                        }}, floatingActionButtonPosition =  FabPosition.End) {
                                            Box(Modifier.padding(it)) {
                                                HomeScreen(logoutCountdownTimer, navControllerState, applicationContext)
                                            }

                                        }

                                    }
                                    composable("records") {
                                        RecordsScreen(navControllerState, this@MainActivity)
                                    }
                                    composable("editProfile") {
                                        EditProfileScreen(navController = navControllerState, this@MainActivity)
                                    }
                                    composable("changePassword") {
                                        ChangePasswordScreen(navController = navControllerState)
                                    }
                                    composable("changeImage") {
                                        ChangeImageScreen(navController = navControllerState, this@MainActivity)
                                    }
                                    composable("editRecords/{id}/{date}/{timeIn}/{timeOut}") {

                                        it.arguments!!.getString("date")?.let { it1 ->

                                            it.arguments!!.getString("timeIn")?.let { it2 ->

                                                it.arguments!!.getString("timeOut")?.let { it3 ->

                                                    EditRecordsScreen(it?.arguments?.getString("id")!!,
                                                        it1, it2, it3, navControllerState
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    composable("scanCode") {
                                        var state = rememberScaffoldState()
                                        var scope = rememberCoroutineScope()
                                        androidx.compose.material.Scaffold(
                                            floatingActionButtonPosition = FabPosition.Center,
                                            scaffoldState = state,
                                            floatingActionButton = {
                                                FloatingActionButton(onClick = {

                                                   val sharedPref = this@MainActivity.getSharedPreferences("checkInOut", Context.MODE_PRIVATE)
                                                    if(sharedPref.getString("check", "") in listOf("", "Out") /*&& sharedPref.getString("date", "") !in listOf(
                                                      records  = JSONObject(File(context.filesDir, "a").readText())
                                                            LocalDate.now().format(
                                                            DateTimeFormatter.ofPattern("dd/MM/yyyy")))*/) {
                                                        scope.launch {
                                                            try {
                                                                CheckInService.API.checkIn(
                                                                    CheckInRequest("123", "123")
                                                                )
                                                            } catch(e: Exception) {
                                                                var records  = JSONObject(File(this@MainActivity.filesDir, "a").readText())
                                                                var localSession = File(this@MainActivity.filesDir, "checkSessionInfo")
                                                                var d= JSONArray(File(this@MainActivity.filesDir, "localRecords").readText().toString())
                                                                println(d)
                                                                val allRecordsInfo: JSONArray? = d.getJSONObject(d.length()?.minus(1) ?: 0)?.getJSONArray("days")





                                                                var data = JSONObject(localSession.readText())
                                                                var resultData = data.getJSONObject("result").getJSONArray("data")
                                                                val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                                                val currentZone = ZoneId.systemDefault()
                                                                val currentUnixTimeMillis = LocalDateTime.now().atZone(currentZone).toInstant().toEpochMilli()
                                                                var checkedIn = resultData.getJSONObject(0).getJSONArray("last_checked_in").getJSONObject(0)
                                                                var new = File(this@MainActivity.filesDir, "updatedRecord")


                                                                checkedIn.put("date", currentDate)
                                                                checkedIn.put("time", currentUnixTimeMillis)
                                                                var jsonObject = JSONObject()
                                                                jsonObject.put("time_in", currentUnixTimeMillis)
                                                                jsonObject.put("time_out", 0L)
                                                                jsonObject.put("date", currentDate)
                                                                jsonObject.put("new", true)
                                                                jsonObject.put("entry_id", UUID.randomUUID())
                                                                allRecordsInfo?.put(jsonObject)

                                                                if(!new.exists()) {
                                                                    new.writeText(JSONArray().toString())
                                                                }

                                                                var newJsonObjects = JSONArray(new.readText())
                                                                newJsonObjects.put(jsonObject)
                                                                new.writeText(newJsonObjects.toString())



                                                                File(this@MainActivity.filesDir, "localRecords").writeText(d.toString())
                                                                localSession.writeText(data.toString())
                                                                println(localSession)
                                                            }
                                                        }

                                                        with(sharedPref.edit()) {
                                                            this.putString("check", "In")
                                                            this.putString(  "date",  LocalDate.now().format(
                                                                DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                                                            this.apply()
                                                        }

                                                        scope.launch {
                                                            state.snackbarHostState.showSnackbar("Checked in!", null, SnackbarDuration.Short)
                                                        }



                                                    } else if(sharedPref.getString("check", "") == "In") {
                                                        //checkout
                                                        scope.launch {
                                                            try {
                                                                CheckInService.API.checkOut(CheckInRequest("123", "123"))

                                                            } catch(e: Exception) {
                                                                println("OIWTF")

                                                                var localSession = File(this@MainActivity.filesDir, "checkSessionInfo")
                                                                //var allRecordsUpdate = File(this@MainActivity.filesDir, "unusedRecordsInfo")
                                                             //   var allRecordsData = JSONArray(allRecordsUpdate.readText())


                                                                // Retrieve the "days" array from the JSON object

                                                                // Retrieve the "days" array from the JSON object

// Get the last object in the "days" array
                                                                val currentZone = ZoneId.systemDefault()
                                                                val currentUnixTimeMillis = LocalDateTime.now().atZone(currentZone).toInstant().toEpochMilli()
// Get the last object in the "days" array


                                                               // data = CheckInService.API.getRecords("123").body()?.string()?.let {JSONObject(it)}?.getJSONObject("result")?.getJSONArray("data")
                                                                var data = JSONObject(localSession.readText())

                                                                var resultData = data.getJSONObject("result").getJSONArray("data")
                                                                val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

                                                                var checkedIn = resultData.getJSONObject(0).getJSONArray("last_checked_in").getJSONObject(0)
                                                                var checkedOut = resultData.getJSONObject(1).getJSONArray("last_checked_out").getJSONObject(0)
                                                                checkedOut.put("date", currentDate)
                                                                checkedOut.put("time", currentUnixTimeMillis)
                                                                localSession.writeText(data.toString())

                                                                var new = File(this@MainActivity.filesDir, "updatedRecord")
                                                                var newJSON = JSONArray(new.readText())
                                                                var d= JSONArray(File(this@MainActivity.filesDir, "localRecords").readText().toString())
                                                                println(d)
                                                                var records  = JSONObject(File(this@MainActivity.filesDir, "a").readText())
                                                                val allRecordsInfo: JSONArray? = d.getJSONObject(d.length()?.minus(1) ?: 0)?.getJSONArray("days")
                                                                var listOfRecords = records?.getJSONArray("data")
                                                                val lastObject = allRecordsInfo?.getJSONObject(
                                                                    allRecordsInfo?.length()?.minus(1) ?: 0
                                                                )
                                                                if(newJSON.length() != 0) {
                                                                    var jsonOb = newJSON.getJSONObject(newJSON.length() -1)
                                                                    jsonOb.put("time_out", currentUnixTimeMillis)
                                                                    new.writeText(newJSON.toString())
                                                                }


// Add the "time_out" property to the last object

// Add the "time_out" property to the last object
                                                                lastObject?.put("time_out", currentUnixTimeMillis)

                                                                File(this@MainActivity.filesDir, "localRecords").writeText(d.toString())


                                                            }

                                                        }
                                                        with(sharedPref.edit()) {
                                                            this.putString("check", "Out")
                                                            this.apply()
                                                        }


                                                        scope.launch {
                                                            state.snackbarHostState.showSnackbar("Checked out!", null, SnackbarDuration.Short)
                                                        }
                                                    } else {
                                                        Toast.makeText(this@MainActivity, "You cannot check in as you have already checked out", Toast.LENGTH_SHORT).show()
                                                    }


                                                }, backgroundColor = Color.LightGray) {
                                                    Icon(painterResource(id = R.drawable.baseline_camera_alt_24), contentDescription = "Camera")

                                                }
                                            }

                                        ) {
                                            Box(Modifier.padding(it)) {
                                                ScanCodeScreen()
                                            }

                                        }

                                    }


                                }
                            }






                        }


            }
        }
    }
}

    override fun onUserInteraction() {
        super.onUserInteraction()
        println("USER INTERACTED")
        var sharedPref = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        if(sharedPref.getString("accountid", null) != null) {
            logoutCountdownTimer.cancel()
            logoutCountdownTimer.start()
        }


    }



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}



@Composable
fun NavBar(navState: NavController) {
BottomNavigation(backgroundColor = greyColour, modifier = Modifier
    .padding(top = 15.dp)
    .height(70.dp)) {
    BottomNavigationItem(selected = false, unselectedContentColor = Color.White, onClick = { navState.navigate("Home") {
        this.launchSingleTop = true
        restoreState = true

    } }, icon = {
        Icon(Icons.Default.Home, contentDescription = "Home", modifier = Modifier.padding(bottom = 10.dp))

    }, label =   {      Text(text = "Home".uppercase(), color = Color.White,  modifier = Modifier.padding(top = 10.dp))}
    )
    BottomNavigationItem(selected = false, unselectedContentColor = Color.White, onClick = { navState.navigate("records") {
        this.launchSingleTop = true
        restoreState = true

    } }, icon = {
        Icon(Icons.Default.Check, contentDescription = "Records", modifier = Modifier.padding(bottom = 10.dp))

    }, label =   {      Text(text = "Records".uppercase(), color = Color.White,  modifier = Modifier.padding(top = 10.dp))}
    )
    BottomNavigationItem(selected = false, unselectedContentColor = Color.White, onClick = { navState.navigate("updateProfile") {
        this.launchSingleTop = true
        restoreState = true

    } }, icon = {
        Icon(Icons.Default.Person, contentDescription = "Update profile", modifier = Modifier.padding(bottom = 10.dp))

    }, label =   {      Text(text = "Profile".uppercase(), color = Color.White, modifier = Modifier.padding(top = 10.dp))}
    )
}
}



    @Composable
    fun ScanCodeScreen() {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Scan Room QR", modifier = Modifier
                .width(250.dp)
                .padding(bottom = 50.dp, top = 50.dp), textAlign = TextAlign.Center, fontSize = 40.sp, lineHeight = 40.sp)
            Row(horizontalArrangement = Arrangement.SpaceAround) {
                Text(text = "[", fontSize = 250.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                Text(text = "]", fontSize = 250.sp, fontWeight = FontWeight.SemiBold)
            }

        }
    }


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CheckinTheme {
        //RecordsScreen(null!!)
    }
}}