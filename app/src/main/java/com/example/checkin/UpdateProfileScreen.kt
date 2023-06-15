package com.example.checkin

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Switch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun UpdateProfileScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = { navController.navigate("editProfile")}) {
            Icon(Icons.Default.Edit, contentDescription = "Edit profile", Modifier.align(Alignment.End))
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

        Row(modifier = Modifier.padding(15.dp)) {
            Switch(checked = true, onCheckedChange = {})
            Text(text = "Biometric login", modifier = Modifier.offset(x = (20.dp), y = 10.dp))
        }


    }
}