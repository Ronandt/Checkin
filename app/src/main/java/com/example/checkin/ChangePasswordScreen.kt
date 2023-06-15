package com.example.checkin

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(navController: NavController) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    androidx.compose.material.Scaffold(topBar = { TopAppBar(navigationIcon = {
        IconButton(onClick = {
            navController.navigateUp()
        }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back")
        }

    }, backgroundColor = greyColour, title = { Text("Change Password", color = Color.White) }, contentColor = Color.White)
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
