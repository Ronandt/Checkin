package com.example.checkin

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(navController: NavController) {
    var oldPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmNewPassword by rememberSaveable { mutableStateOf("") }
    var APIError by rememberSaveable {mutableStateOf("")}
    var scope = rememberCoroutineScope()
    var sharedPref = LocalContext.current.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
    val biometricSharedPref = LocalContext.current.getSharedPreferences("biometricSafe", Context.MODE_PRIVATE)
    var scaffoldState = rememberScaffoldState()
    androidx.compose.material.Scaffold(topBar = { TopAppBar(navigationIcon = {
        IconButton(onClick = {
            navController.navigateUp()
        }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back")
        }

    }, backgroundColor = greyColour, title = { Text("Change Password", color = Color.White) }, contentColor = Color.White)
    }, scaffoldState = scaffoldState) {
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
            Button(onClick = {
                scope.launch(Dispatchers.IO){
                    try {
                        val responseData: ResponseData? = CheckInService.API.changePassword(ChangePasswordRequest(confirmPassword = confirmNewPassword, accountId = sharedPref.getString("accountid", "")!!, newPassword = newPassword, oldPassword = oldPassword, accessKey = sharedPref.getString("accesskey", "")!!)).body()
                        if(responseData?.status == "success") {
                            withContext(Dispatchers.Main) {
                                scaffoldState.snackbarHostState.showSnackbar("Successfully saved")
                                with(biometricSharedPref.edit()) {
                                    this.putString("password", newPassword)
                                    apply()
                                }
                                navController.navigate("login") {
                                    popUpTo("changePassword") {
                                        inclusive = true
                                    }
                                }

                            }

                        } else if(responseData?.status == "error") {
                            APIError = responseData?.result?.get("message") as String
                            withContext(Dispatchers.Main) {
                                scaffoldState.snackbarHostState.showSnackbar(APIError)
                            }
                        }
                    } catch (e: Exception) {
                        println("Something wrong happened")
                    }

                }

            }, colors = ButtonDefaults.buttonColors(containerColor = lightBlueColour), modifier = Modifier
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
