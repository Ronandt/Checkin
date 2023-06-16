package com.example.checkin

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(email: String, password: String, emailCallback: (String) -> Unit, passwordCallback: (String) -> Unit, navigationCallback: () -> Unit, navController: NavController, context: Context) {
    var scope = rememberCoroutineScope()
    var errorAPIErrorMessage by remember {mutableStateOf("")}
    var activated by remember {mutableStateOf(false )}
    Column(modifier = Modifier.fillMaxWidth()) {
        val error by remember(email) {
            derivedStateOf {
                !TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        }
        CheckInVector()
        OutlinedTextField(value = email, onValueChange = emailCallback, singleLine = true, label = { Text("Email") }, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp), supportingText = {if(error) {


            Text(text = "This is not an email!", color = Color.Red)

        } else if(activated && email == "") {
            Text("Fill in your email")
        }}, colors = TextFieldDefaults.outlinedTextFieldColors(errorBorderColor = Color.Red), isError = error || (email == "" && activated))


        OutlinedTextField(value = password, onValueChange = passwordCallback, singleLine = true, visualTransformation = PasswordVisualTransformation(), label= { Text("Password") }, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp))




        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = {}, modifier = Modifier.align(Alignment.CenterHorizontally), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)) {
            Text("Use Biometric", fontWeight = FontWeight.Bold)
        }
        Button(onClick = {
            if(error || email.isEmpty()) {
                activated = true
                return@Button
            }
            scope.launch(Dispatchers.IO) {
                var result = CheckInService.API.login(UserLoginRequest(email, password, "123"))
                    .body()
                if(result?.status == "error") {
                    errorAPIErrorMessage = result.result.get("message").toString()

                } else if(result?.status == "success") {
                   val sharedPref =  context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("accountid", result.result.get("accountid").toString())
                        putString("accountid", result.result.get("orgid").toString())
                        putString("accountid", result.result.get("email").toString())
                        apply()
                    }
                    withContext(Dispatchers.Main) {
                        navController.navigate("home")

                    }

                } else {
                    println("this should not happen")
                }
            }
                         }, colors = ButtonDefaults.buttonColors(containerColor = lightBlueColour), modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .fillMaxWidth()
            .padding(15.dp), shape = RoundedCornerShape(4.dp)
        ) {
            Text("Submit".uppercase())
        }


        if(errorAPIErrorMessage != "") {
            Text(errorAPIErrorMessage, color = Color.Red, modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally))
        }

    }
}

@Composable
fun CheckInVector() {
    Box(Modifier.background(greyColour)) {
        Image(
            painterResource(id = R.drawable.check_in_logo), contentDescription = "Check in logo", contentScale = ContentScale.Crop, modifier = Modifier
                .height(400.dp)
                .fillMaxWidth())

    }
}