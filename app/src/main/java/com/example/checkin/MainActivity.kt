package com.example.checkin

import android.annotation.SuppressLint
import android.os.Bundle
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.checkin.ui.theme.CheckinTheme

class MainActivity : ComponentActivity() {
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
            //val scaffoldState = rememberScaffoldState()
            CheckinTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                    
                ) {

                        Scaffold(bottomBar = {
                            if(navControllerState.currentBackStackEntryAsState().value?.destination?.route !in listOf("login", "editProfile", "changePassword")) NavBar(navState = navControllerState)
                        }) {
                            Box(modifier = Modifier.padding(it)) {
                                NavHost(navController = navControllerState, "login", modifier = Modifier) {
                                    composable("login") {
                                        LoginScreen(email = email, password = password ,emailCallback = {

                                            email = it}, passwordCallback = {password = it}, navigationCallback = {
                                            navControllerState.navigate("home")
                                        })
                                    }
                                    composable("updateProfile") {
                                        UpdateProfileScreen(navControllerState)
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

                                }
                            }






                        }


            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(navController: NavController) {
    var oldPassword by remember {mutableStateOf("")}
    var newPassword by remember {mutableStateOf("")}
    var confirmNewPassword by remember {mutableStateOf("")}
    androidx.compose.material.Scaffold(topBar = { TopAppBar(navigationIcon = {
        IconButton(onClick = {
            navController.navigateUp()
        }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back")
        }

    }, backgroundColor = greyColour, title = {Text("Change Password", color = Color.White)}, contentColor = Color.White)
    }) {
            it -> Box(modifier = Modifier.padding(it)) {
        Column() {
            OutlinedTextField(modifier = Modifier.fillMaxWidth(),value = oldPassword, onValueChange = {oldPassword = it}, label = {
                Text("Old Password")
            }, visualTransformation = PasswordVisualTransformation())
            OutlinedTextField(modifier = Modifier.fillMaxWidth(),value = newPassword, onValueChange = {newPassword = it}, label = {
                Text("New Password")
            })

            OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = confirmNewPassword, onValueChange = {confirmNewPassword = it}, label = {
                Text("Confirm New Password")
            })


            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = lightBlueColour), modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(15.dp), shape = RoundedCornerShape(4.dp)
            ) {
                Text("Submit".uppercase())
            }
        }


    }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EditProfileScreen(navController: NavController) {
        androidx.compose.material.Scaffold(topBar = { TopAppBar(navigationIcon = {
            IconButton(onClick = {
                navController.navigateUp()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back")
            }

        }, backgroundColor = greyColour, title = {Text("Edit Profile", color = Color.White)}, actions = { IconButton(onClick = { /*TODO*/ }) {
            Icon(imageVector =Icons.Default.Check, contentDescription = "Save")
            }}, contentColor = Color.White)
        }) {
            it -> Box(modifier = Modifier.padding(it)) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .height(130.dp), elevation = 10.dp) {
                        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            Image(painter = painterResource(id = R.drawable.profile_img), contentDescription = "Profile picture",
                                Modifier
                                    .size(100.dp)
                                    .clip(
                                        CircleShape
                                    ))
                        }
                    }

                    Card(
                        Modifier
                            .fillMaxWidth()
                            .height(60.dp), elevation = 10.dp, ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = "Username")
                            Text("Jane Teo")
                        }


                    }
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .height(60.dp), elevation = 10.dp) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Email, contentDescription = "Username")
                            Text("Jane Teo")
                        }


                    }
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .height(60.dp), elevation = 10.dp) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(painter = painterResource(R.drawable.baseline_business_24), contentDescription = "Username")
                            Text("Jane Teo")
                        }


                    }
                }
        }
        }
    }
@Composable
fun UpdateProfileScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = { navController.navigate("editProfile")}) {
            Icon(Icons.Default.Edit, contentDescription = "Edit profile",Modifier.align(Alignment.End))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Image(painter = painterResource(id = R.drawable.profile_img), contentDescription = "Profile image", modifier = Modifier
                .padding(bottom = 100.dp, end = 40.dp, start = 40.dp)
                .size(100.dp)
                .clip(
                    CircleShape
                ), contentScale = ContentScale.Crop)
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Text("Jane Teo", fontSize = 40.sp, modifier = Modifier.padding())
                Row(Modifier.fillMaxWidth()) {
                    Icon(imageVector = Icons.Default.Email, contentDescription = "Email",)
                    Text(text = "janeteo@gmail.com")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(120.dp), modifier =  Modifier.fillMaxWidth()) {
                    Icon(painterResource(R.drawable.baseline_business_24), contentDescription = "Organisation")
                    Text(text = "NYP")
                }
            }

        }
        Divider()
        Row(modifier = Modifier.clickable {
            navController.navigate("changePassword")
        }.padding(25.dp)) {

            Icon(imageVector = Icons.Outlined.Lock, contentDescription = "Password")
            Text(text = "Password")
        }
        Divider(thickness = 3.dp)

        Row(modifier = Modifier.padding(25.dp)) {
            Icon(imageVector = Icons.Outlined.ExitToApp, contentDescription = "Logout")
            Text("Logout")
        }
        Divider(thickness= 3.dp)

        Row(modifier =Modifier.padding(15.dp)) {
            Switch(checked = true, onCheckedChange = {})
            Text(text = "Biometric login", modifier = Modifier.offset(x = (20.dp), y = 10.dp))
        }


    }
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

}
    @Composable
    fun RecordsScreen() {

    }
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(email: String, password: String, emailCallback: (String) -> Unit, passwordCallback: (String) -> Unit, navigationCallback: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val error by remember(email) {
            derivedStateOf {
                !TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        }
        CheckInVector()
        OutlinedTextField(value = email, onValueChange = emailCallback, singleLine = true, label = {Text("Email")}, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp), supportingText = {if(error) {


                Text(text = "This is not an email!", color = Color.Red)
            } }, colors = TextFieldDefaults.outlinedTextFieldColors(errorBorderColor = Color.Red), isError = error)


        OutlinedTextField(value = password, onValueChange = passwordCallback, singleLine = true, visualTransformation = PasswordVisualTransformation(), label= {Text("Password")}, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp))
        


        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = {}, modifier = Modifier.align(Alignment.CenterHorizontally), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)) {
            Text("Use Biometric", fontWeight = FontWeight.Bold)
        }
        Button(onClick = navigationCallback, colors = ButtonDefaults.buttonColors(containerColor = lightBlueColour), modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .fillMaxWidth()
            .padding(15.dp), shape = RoundedCornerShape(4.dp)
        ) {
            Text("Submit".uppercase())
        }
    }
}
@Composable
fun CheckInVector() {
    Box(Modifier.background(greyColour)) {
        Image(painterResource(id = R.drawable.check_in_logo), contentDescription = "Check in logo", contentScale = ContentScale.Crop, modifier = Modifier
            .height(400.dp)
            .fillMaxWidth())

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CheckinTheme {
ChangePasswordScreen(navController = rememberNavController())
    }
}}