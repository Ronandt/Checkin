package com.example.checkin

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun EditProfileScreen(navController: NavController, context: Context) {
    val sharedPrefSession = LocalContext.current.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
    val sharedPrefBiometric = LocalContext.current.getSharedPreferences("biometricSafe", Context.MODE_PRIVATE)
    var profileDetails by remember { mutableStateOf<ResponseData?>(null)}

    var username by rememberSaveable {mutableStateOf("")}
    var email by rememberSaveable {mutableStateOf("")}
    var organisation by rememberSaveable {mutableStateOf("")}
    val keyboard = LocalSoftwareKeyboardController.current

    var scope = rememberCoroutineScope()
    var scaffoldState = rememberScaffoldState()
    LaunchedEffect(Unit){
        var file = File(context.filesDir, "updateProfile")
        try {

            if(!file.exists()) {
                file.createNewFile()
            }
      /*       val json = JSONObject(File(context.filesDir, "updateProfile").readText())
            if(username !=  json.getString("username").toString() || email != json.getString("email").toString() || organisation != json.getString("organisation").toString()) {
                 CheckInService.API.updateProfileDetails(ChangeUserInfoRequest(username = json.getString("username").toString(), email =  json.getString("email").toString(), organisation = json.getString("organisation").toString(), accountId = sharedPrefSession.getString("accountid", "")!!, accessKey = "123")).body()
            }*/

            profileDetails = CheckInService.API.getProfileDetails(GetUserInfoRequest(sharedPrefSession.getString("accountid", "")!!)).body()

            username = profileDetails?.result?.get("username").toString()
            email = profileDetails?.result?.get("email").toString()
            organisation = profileDetails?.result?.get("organisation").toString()
            val json = JSONObject(file.readText())
            if(username !=  json.getString("username").toString() || email != json.getString("email").toString() || organisation != json.getString("organisation").toString()) {
                 CheckInService.API.updateProfileDetails(ChangeUserInfoRequest(username = json.getString("username").toString(), email =  json.getString("email").toString(), organisation = json.getString("organisation").toString(), accountId = sharedPrefSession.getString("accountid", "")!!, accessKey = "123")).body()
                username = json.getString("username").toString()
                email = json.getString("email").toString()
                organisation = json.getString("organisation").toString()

            }

                file.writeText( """{"username": "${username}", "email": "${email}", "organisation": "${organisation}"}""")



        } catch (e: Exception) {
            val json = JSONObject(file.readText())
            username = json.getString("username").toString()
            email = json.getString("email").toString()
            organisation = json.getString("organisation").toString()

        }


    }

    androidx.compose.material.Scaffold(scaffoldState = scaffoldState, topBar = { TopAppBar(navigationIcon = {
        IconButton(onClick = {
            navController.navigateUp()
        }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back")
        }

    }, backgroundColor = greyColour, title = { Text("Edit Profile", color = Color.White) }, actions = { IconButton(onClick = {
        scope.launch(Dispatchers.IO) {
            try {
                var file = File(context.filesDir, "updateProfile")
                var result: ResponseData? = CheckInService.API.updateProfileDetails(ChangeUserInfoRequest(username = username, email = email, organisation = organisation, accountId = sharedPrefSession.getString("accountid", "")!!, accessKey = "123")).body()
                val json = JSONObject(file.readText())

                json.put("username", username )
                json.put("email", email)
                json.put("organisation", organisation)
                file.writeText(json.toString())
                withContext(Dispatchers.Main) {
                    keyboard?.hide()
                    scaffoldState.snackbarHostState.showSnackbar(result?.result?.get("message").toString())

                }

                if(result?.status == "success") {
                    with(sharedPrefBiometric.edit()) {
                        this.putString("email", email)
                        apply()
                    }

                }
            } catch(e: Exception) {
                var file = File(context.filesDir, "updateProfile")
                val json = JSONObject(file.readText())

                json.put("username", username )
                json.put("email", email)
                json.put("organisation", organisation)
                file.writeText(json.toString())
                println(json)
                println(e.message)
            }

        }
    }) {
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
            Column(Modifier.verticalScroll(rememberScrollState())) {


                TrailingIconTextField(icon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Username"
                    )
                },
                    label = { Text("Username") }, { username = it }, username
                )
                TrailingIconTextField(icon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Username"
                    )
                },
                    label = { Text("Email") }, { email = it }, email
                )
                TrailingIconTextField(icon = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_business_24),
                        contentDescription = "Username"
                    )
                },
                    label = { Text("Organisation") }, { organisation = it }, organisation
                )
            }

        }

    }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrailingIconTextField(icon: @Composable () -> Unit, label: @Composable () -> Unit, onChangeCallback: (String) -> Unit, state: String) {
    OutlinedTextField(value = state, onValueChange = onChangeCallback,  leadingIcon = icon, singleLine = true, modifier =   Modifier
        .fillMaxWidth()
        )
}