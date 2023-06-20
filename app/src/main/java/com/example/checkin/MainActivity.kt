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
import androidx.activity.compose.setContent
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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.checkin.ui.theme.CheckinTheme
import com.google.android.gms.common.util.IOUtils
import kotlinx.coroutines.launch
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class MainActivity : FragmentActivity() {
    private lateinit var logoutCountdownTimer: CountDownTimer
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var email by remember {
                mutableStateOf("")
            }
            var emailError by remember { mutableStateOf(false)}
            var password by remember {
                mutableStateOf("")
            }
            val navControllerState = rememberNavController()
            val scope = rememberCoroutineScope()
            println("COMPOSED")


                logoutCountdownTimer = object : CountDownTimer(1000000, 1000) {
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
                                                HomeScreen(logoutCountdownTimer, navControllerState)
                                            }

                                        }

                                    }
                                    composable("records") {
                                        RecordsScreen()
                                    }
                                    composable("editProfile") {
                                        EditProfileScreen(navController = navControllerState)
                                    }
                                    composable("changePassword") {
                                        ChangePasswordScreen(navController = navControllerState)
                                    }
                                    composable("changeImage") {
                                        ChangeImageScreen(navController = navControllerState, this@MainActivity)
                                    }
                                    composable("scanCode") {
                                        androidx.compose.material.Scaffold(
                                            floatingActionButtonPosition = FabPosition.Center,
                                            floatingActionButton = {
                                                FloatingActionButton(onClick = {

                                                   val sharedPref = this@MainActivity.getSharedPreferences("checkInOut", Context.MODE_PRIVATE)
                                                    if(sharedPref.getString("check", "") in listOf("", "Out") && sharedPref.getString("date", "") !in listOf(
                                                            LocalDate.now().format(
                                                            DateTimeFormatter.ofPattern("dd/MM/yyyy")))) {
                                                        scope.launch {
                                                            CheckInService.API.checkIn(CheckInRequest("123", "123"))

                                                        }

                                                        with(sharedPref.edit()) {
                                                            this.putString("check", "In")
                                                            this.putString(  "date",  LocalDate.now().format(
                                                                DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                                                            this.apply()
                                                        }
                                                    } else if(sharedPref.getString("check", "") == "In") {
                                                        //checkout
                                                        scope.launch {
                                                            CheckInService.API.checkOut(CheckInRequest("123", "123"))
                                                        }
                                                        with(sharedPref.edit()) {
                                                            this.putString("check", "Out")
                                                            this.apply()
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
    } }, icon = {
        Icon(Icons.Default.Home, contentDescription = "Home", modifier = Modifier.padding(bottom = 10.dp))

    }, label =   {      Text(text = "Home".uppercase(), color = Color.White,  modifier = Modifier.padding(top = 10.dp))}
    )
    BottomNavigationItem(selected = false, unselectedContentColor = Color.White, onClick = { navState.navigate("records") {
        this.launchSingleTop = true
    } }, icon = {
        Icon(Icons.Default.Check, contentDescription = "Records", modifier = Modifier.padding(bottom = 10.dp))

    }, label =   {      Text(text = "Records".uppercase(), color = Color.White,  modifier = Modifier.padding(top = 10.dp))}
    )
    BottomNavigationItem(selected = false, unselectedContentColor = Color.White, onClick = { navState.navigate("updateProfile") {
        this.launchSingleTop = true
    } }, icon = {
        Icon(Icons.Default.Person, contentDescription = "Update profile", modifier = Modifier.padding(bottom = 10.dp))

    }, label =   {      Text(text = "Profile".uppercase(), color = Color.White, modifier = Modifier.padding(top = 10.dp))}
    )
}
}

    @Composable
    fun RecordsScreen() {

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
        androidx.compose.material.Scaffold(floatingActionButton = { FloatingActionButton(
            onClick = {  }, backgroundColor = Color.LightGray) {
            Icon(painter = painterResource(R.drawable.baseline_qr_code_scanner_24), contentDescription = "Scan qr code")
        }}, floatingActionButtonPosition =  FabPosition.End) {
            Box(Modifier.padding(it)) {
               // HomeScreen(logoutCountdownTimer)
            }

        }
    }
}}