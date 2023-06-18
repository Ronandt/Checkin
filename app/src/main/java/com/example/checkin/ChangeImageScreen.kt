 package com.example.checkin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider

import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import java.io.File
import java.util.concurrent.Executors

 @Composable
fun ChangeImageScreen(navController: NavController, context: Context) {
    var imageSharedPref = context.getSharedPreferences("imageInfo", Context.MODE_PRIVATE)
    val userSharedPref = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
     var openCamera by remember { mutableStateOf(false)}


    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        if(it != null) {
            with(imageSharedPref.edit()) {
                this.putString(userSharedPref.getString("accountid", null), it.toString())
                context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                apply()
                println()
            }
            navController.navigateUp()
        }
    }



    /*AndroidView(factory = {
        PreviewView()
    }, update = {})*/
Column() {

    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        openCamera = true
                    }, horizontalArrangement = Arrangement.Center) {
                Text(
                    "Camera", fontSize = 15.sp
                )
            }
        }
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

                    }, horizontalArrangement = Arrangement.Center){
                Text(
                    "Photo Library/Gallery",  fontSize = 15.sp
                )

            }

        }
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigateUp()
                    },horizontalArrangement = Arrangement.Center) {
                Text(
                    "Cancel",  fontSize = 15.sp
                )
            }
        }


    }
    Spacer(modifier = Modifier.weight(1f))
    if(openCamera) {

        Camera(navController, context)


    } 
}

}

@Composable
fun Camera(navController: NavController, context: Context) {
    var imageSharedPref = context.getSharedPreferences("imageInfo", Context.MODE_PRIVATE)
    val userSharedPref = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
    val lifecycleOwner = LocalLifecycleOwner.current //get lifecycle
    val context = LocalContext.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }
    val imageCapture = remember {ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()}
    val fileName = remember {File(context.getExternalFilesDir(null), "${System.currentTimeMillis()}.png")}
    val outputFileOptions = remember {ImageCapture.OutputFileOptions.Builder(fileName)}
    Column() {
        Box() {


//have the view in compose
            AndroidView(factory = {
                PreviewView(context)




            }, update= {//callback to the invoked afte the layout is inflated. Think about your onclick listener stuff
                //add listener to the singleton
                cameraProviderFuture.addListener({
                    //get provider
                    val cameraProvider = cameraProviderFuture.get()

                    //build the preview
                    val preview = Preview.Builder().build()
                    preview.setSurfaceProvider(it.surfaceProvider)

                    //choose the selector
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA


                    try {
                        //unbind all and rebind
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner,cameraSelector, preview, imageCapture) //combine all into one

                    } catch(e: Exception) {
                        Log.d(e.message, "f")
                    }
                }, ContextCompat.getMainExecutor(context)) //run in context thread
            }, modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                imageCapture.takePicture(outputFileOptions.build(), ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onError(error: ImageCaptureException)
                        {
                            Toast.makeText(context, "There was an error capturing the image", Toast.LENGTH_SHORT).show()
                            println("HIHIHI")
                            println(error)
                        }
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            println(":DSDFFD")
                            println(outputFileResults)
                            println(outputFileResults.savedUri.toString())
                            with(imageSharedPref.edit()) {
                                this.putString(userSharedPref.getString("accountid", null),outputFileResults.savedUri.toString())
                                //context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                apply()

                            }

                            navController.navigateUp()
                        }
                    })
            }, modifier = Modifier.align(Alignment.BottomCenter)) {
                Text("Capture")
            }
        }



    }


}

