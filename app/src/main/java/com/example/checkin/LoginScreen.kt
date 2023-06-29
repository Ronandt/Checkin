package com.example.checkin

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(email: String, password: String, emailCallback: (String) -> Unit, passwordCallback: (String) -> Unit, navigationCallback: () -> Unit, navController: NavController, context: Context) {
    var scope = rememberCoroutineScope()
    var errorAPIErrorMessage by rememberSaveable {mutableStateOf("")}
    var activated by rememberSaveable {mutableStateOf(false )}
    val sharedPref =  context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
    LaunchedEffect(Unit ) {
        with(sharedPref.edit()) {
            clear()
            apply()
        }
    }
    var login: (email: String, password: String) -> Unit = {email, password ->
        scope.launch(Dispatchers.IO) {
            try {
                var result = CheckInService.API.login(UserLoginRequest(email, password, "123"))
                    .body()
                if(result?.status == "error") {
                    errorAPIErrorMessage = result.result.get("message").toString()

                } else if(result?.status == "success") {

                    val sharedPrefStored = context.getSharedPreferences("biometricSafe", Context.MODE_PRIVATE)
                    with(sharedPrefStored.edit()) {
                        putString("email", email)
                        putString("password", password)
                        apply()
                    }
                    with(sharedPref.edit()) {
                        putString("accountid", result.result.get("accountid").toString())
                        putString("orgid", result.result.get("orgid").toString())
                        putString("email", result.result.get("email").toString())
                        putString("accesskey", "123")

                        apply()
                    }
                    withContext(Dispatchers.Main) {
                        errorAPIErrorMessage = ""

                        navController.navigate("home")

                    }

                } else {
                    println("this should not happen")
                }
            } catch (e: Exception) {
                errorAPIErrorMessage = "We coould not connect to our server. Please try again"
            }

        }
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        val error by remember(email) {
            derivedStateOf {
                !TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        }
        CheckInVector()
        Column(Modifier.verticalScroll(rememberScrollState())) {
            OutlinedTextField(value = email,
                onValueChange = emailCallback,
                singleLine = true,
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp),
                supportingText = {
                    if (error) {


                        Text(text = "This is not an email!", color = Color.Red)

                    } else if (activated && email == "") {
                        Text("Fill in your email")
                    }
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(errorBorderColor = Color.Red),
                isError = error || (email == "" && activated)
            )


            OutlinedTextField(
                value = password,
                onValueChange = passwordCallback,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp)
            )




            Spacer(modifier = Modifier.weight(1f))

            if (context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                    .getString("biometricsEnabled", "disabled") == "enabled"
            ) {

                TextButton(
                    onClick = {
                        val sharedPrefSafe =
                            context.getSharedPreferences("biometricSafe", Context.MODE_PRIVATE)
                        val biometricSharedPref =
                            context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                        if (!AuthenticationManager.canAuthenticate(context)) {
                            Toast.makeText(
                                context,
                                "You can't authenticate with biometrics",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (AuthenticationManager.canAuthenticate(context) && biometricSharedPref.getString(
                                "biometricsEnabled",
                                "disabled"
                            ) == "enabled" && sharedPrefSafe.getString(
                                "password",
                                null
                            ) != null && sharedPrefSafe.getString("email", null) != null
                        ) {
                            AuthenticationManager.biometricPrompt(context, {
                                Toast.makeText(context, "Auth cancelled", Toast.LENGTH_SHORT).show()
                            }, {
                                login(
                                    sharedPrefSafe.getString("email", null)!!,
                                    sharedPrefSafe.getString("password", null)!!
                                )
                                Toast.makeText(
                                    context,
                                    "Authentication succeeded",
                                    Toast.LENGTH_SHORT
                                ).show()


                            }, {
                                Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT)
                                    .show()
                            }).authenticate(AuthenticationManager.generate())
                        } else {
                            Toast.makeText(context, "Authentication failed?", Toast.LENGTH_SHORT)
                                .show()
                        }

                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                ) {
                    Text("Use Biometric", fontWeight = FontWeight.Bold)
                }

            }

            Button(
                onClick = {
                    if (error || email.isEmpty()) {
                        activated = true
                        return@Button
                    }
                    login(email, password)
                },
                colors = ButtonDefaults.buttonColors(containerColor = lightBlueColour),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .padding(15.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("Submit".uppercase())
            }


            if (errorAPIErrorMessage != "") {
                Text(
                    errorAPIErrorMessage, color = Color.Red, modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )
            }
        }

    }
}

@Composable
fun CheckInVector() {
    BoxWithConstraints() {
        val constraintsScope = this
        var mode by remember {mutableStateOf("")}
        mode = if(maxWidth < maxHeight) "Portrait" else "Landscape"
        if(mode == "Portrait") {
            Image(
                painterResource(id = R.drawable.check_in_logo), contentDescription = "Check in logo", contentScale = ContentScale.Crop, modifier = Modifier
                    .height(400.dp)
                    .fillMaxWidth())

        } else if(mode == "Landscape") {
            Box(modifier = Modifier.fillMaxWidth().background(greyColour)) {
                Image(
                    painterResource(id = R.drawable.check_in_logo), contentDescription = "Check in logo", contentScale = ContentScale.FillHeight, modifier = Modifier
                        .height(200.dp)
                        .align(Alignment.Center)
                )
            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    LoginScreen(
        email = "",
        password = "",
        emailCallback = {} ,
        passwordCallback ={} ,
        navigationCallback = { /*TODO*/ },
        navController = rememberNavController(),
        context = LocalContext.current
    )
}