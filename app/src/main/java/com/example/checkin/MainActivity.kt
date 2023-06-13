package com.example.checkin

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.checkin.ui.theme.CheckinTheme

class MainActivity : ComponentActivity() {
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

            CheckinTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navControllerState, "login", modifier = Modifier) {
                        composable("login") {
                            LoginScreen(email = email, password = password ,emailCallback = {

                                email = it}, passwordCallback = {password = it}, navigationCallback = {
                                navControllerState.navigate("home")
                            })
                        }
                        composable("updateProfile") {
                            UpdateProfileScreen()
                        }
                        composable("home") {
                            HomeScreen()
                        }

                    }


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

@Composable
fun UpdateProfileScreen() {

}

@Composable
fun HomeScreen() {

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(email: String, password: String, emailCallback: (String) -> Unit, passwordCallback: (String) -> Unit, navigationCallback: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        CheckInVector()
        OutlinedTextField(value = email, onValueChange = emailCallback, singleLine = true, label = {Text("Email")}, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp), supportingText = {if(!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {


                Text(text = "This is not an email!", color = Color.Red)
            } }, colors = TextFieldDefaults.outlinedTextFieldColors(errorBorderColor = Color.Red),)


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
            .padding(15.dp), shape = CutCornerShape(4.dp)) {
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
        Greeting("Android")
    }
}