package com.example.checkin

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Patterns

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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
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

                                        HomeScreen()
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
fun HomeScreen() {
    LaunchedEffect(Unit) {
        logoutCountdownTimer.start()

    }


    val sharedPreferences = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val userSharedPreferences = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
    var openDialog by remember {mutableStateOf(sharedPreferences.getString("biometricsEnabled", null) == null && userSharedPreferences.getString("accountid", null) != null)}

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
      }, title= {Text("Biometrics")}, text = {Text("Would you like to enable biometrics?")})
    }


}

    @Composable
    fun RecordsScreen() {

    }



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CheckinTheme {
ChangePasswordScreen(navController = rememberNavController())
    }
}}