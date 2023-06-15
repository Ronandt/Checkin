package com.example.checkin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

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

    }, backgroundColor = greyColour, title = { Text("Edit Profile", color = Color.White) }, actions = { IconButton(onClick = { /*TODO*/ }) {
        Icon(imageVector = Icons.Default.Check, contentDescription = "Save")
    }
    }, contentColor = Color.White)
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