package com.example.checkin

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.Settings.Global.putString
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
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import coil.compose.AsyncImage
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

@SuppressLint("SuspiciousIndentation")
@Composable
fun UpdateProfileScreen(navController: NavController, context: Context) {
    val biometricSharedPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val imageSharedPref = context.getSharedPreferences("imageInfo", Context.MODE_PRIVATE)
    val getUserInfo = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
    var username by rememberSaveable {mutableStateOf("")}
    var email by rememberSaveable {mutableStateOf("")}
    var organisation by rememberSaveable {mutableStateOf("")}

    var enableBiometrics by remember {mutableStateOf(
        biometricSharedPref.getString("biometricsEnabled", "disabled") == "enabled"
    )}
     var userDetails: ResponseData by remember {mutableStateOf(ResponseData("", mapOf("accountid" to "unknown", )))};

    LaunchedEffect(Unit) {
        val file = File(context.filesDir, "updateProfile")

        try {
              userDetails = CheckInService.API.getProfileDetails(GetUserInfoRequest(getUserInfo.getString("accountid", "")!!)).body()!!

                if(!file.exists()) {
                    file.createNewFile()
                }

            username = userDetails.result?.get("username").toString()
            email = userDetails.result?.get("email").toString()
            organisation = userDetails.result?.get("organisation").toString()

            val json = JSONObject(File(context.filesDir, "updateProfile").readText())
            if(username !=  json.getString("username").toString() || email != json.getString("email").toString() || organisation != json.getString("organisation").toString()) {
                CheckInService.API.updateProfileDetails(ChangeUserInfoRequest(username = json.getString("username").toString(), email =  json.getString("email").toString(), organisation = json.getString("organisation").toString(), accountId = getUserInfo.getString("accountid", "")!!, accessKey = "123")).body()
                username = json.getString("username").toString()
                email = json.getString("email").toString()
                organisation = json.getString("organisation").toString()

            }
                file.writeText( """{"username": "${username}", "email": "${email}", "organisation": "${organisation}"}""")



            println(userDetails)
        } catch(e: Exception) {
            println(e.message)
            val json = JSONObject(file.readText())
            username = json.getString("username").toString()
            email = json.getString("email").toString()
            organisation = json.getString("organisation").toString()
        }

    }
    Column(modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = { navController.navigate("editProfile")}) {
            Icon(Icons.Default.Edit, contentDescription = "Edit profile", Modifier.align(Alignment.End))
        }


        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            if(!(imageSharedPref.getString(getUserInfo.getString("accountid", null), "")== ""))  {
                val imageUri =imageSharedPref.getString(getUserInfo.getString("accountid", null), "")
                println(Uri.decode(imageSharedPref.getString(getUserInfo.getString("accountid", null), "")))
                AsyncImage(model = imageUri, contentDescription = "Image", contentScale = ContentScale.Crop, modifier = Modifier
                    .padding(bottom = 100.dp, end = 40.dp, start = 40.dp)
                    .size(100.dp)
                    .clip(
                        CircleShape
                    )
                    .clickable { navController.navigate("changeImage") })
        } else {
                Image(painter = painterResource(id = R.drawable.profile_img), contentDescription = "Profile image", modifier = Modifier
                    .padding(bottom = 100.dp, end = 40.dp, start = 40.dp)
                    .size(100.dp)
                    .clip(
                        CircleShape
                    )
                    .clickable { navController.navigate("changeImage") }, contentScale = ContentScale.Crop)
            }


            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                if(username == null) {
                    CircularProgressIndicator(Modifier.padding(vertical = 15.dp))
                } else {
                    Text(username, fontSize = 40.sp, modifier = Modifier.padding())
                    Row(Modifier.fillMaxWidth()) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = "Email",)
                        Text(text = email)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(120.dp), modifier =  Modifier.fillMaxWidth()) {
                        Icon(painterResource(R.drawable.baseline_business_24), contentDescription = "Organisation")
                        Text(text = (organisation ?: "NYP"))
                    }
                }

            }

        }
        Divider()
        Row(modifier = Modifier
            .clickable {
                navController.navigate("changePassword")
            }
            .padding(25.dp).fillMaxWidth()) {

            Icon(imageVector = Icons.Outlined.Lock, contentDescription = "Password")
            Text(text = "Password")
        }
        Divider(thickness = 3.dp)

        Row(modifier = Modifier.clickable{  var sharedpref = context.getSharedPreferences("", Context.MODE_PRIVATE)
        with(sharedpref.edit()) {
            clear()
            apply()
        }
            navController.navigate("login")
        }.padding(25.dp).fillMaxWidth()) {
            Icon(imageVector = Icons.Outlined.ExitToApp, contentDescription = "Logout")

            Text("Logout")
        }
        Divider(thickness= 3.dp)

        Row(modifier = Modifier.padding(15.dp)) {
            Switch(checked = enableBiometrics, onCheckedChange = {
                enableBiometrics = it
                with(biometricSharedPref.edit()) {
                    putString("biometricsEnabled", if(enableBiometrics) "enabled" else "disabled")
                    apply()
                }
            })
            Text(text = "Biometric login", modifier = Modifier.offset(x = (20.dp), y = 10.dp))
        }


    }
}