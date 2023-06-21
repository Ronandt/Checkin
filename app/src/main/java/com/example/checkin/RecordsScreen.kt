package com.example.checkin

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.checkin.CheckInAPIService
import com.example.checkin.CheckInService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RecordsScreen() {
    var records by remember {mutableStateOf<JSONObject?>(null)}
    var listOfRecords by remember {mutableStateOf<JSONArray?>(null)}
    var scope = rememberCoroutineScope()
    var filter by remember {mutableStateOf<String>("")}
    LaunchedEffect(Unit) {
        records = CheckInService.API.getRecords("123").body()?.string()
            ?.let { JSONObject(it).getJSONObject("result") }
        listOfRecords = records?.getJSONArray("data")


        println(records)

    }



    Column() {

        TextField(value = filter, onValueChange = {filter = it},
            Modifier
                .fillMaxWidth()
                .background(Color.White), label={Text("Search")}, colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White), placeholder = {Text("Enter date")})
        LazyColumn(contentPadding = PaddingValues(15.dp)) {
            listOfRecords?.length()?.let {
                items(it) {
                    //listOfRecords!!.get(it)
                    //Text(listOfRecords!!.get(it).toString())
                    for(x in 0 until listOfRecords!!.getJSONObject(it).getJSONArray("days").length()) {

                        var swipeableState = rememberSwipeableState(initialValue = 0)
                        var visible by remember {mutableStateOf(true)}

                        if(visible && (filter == "" ||
                                    listOfRecords!!.getJSONObject(it).getJSONArray("days")
                                        .getJSONObject(x).getString("date").replace("\"", "").contains(filter)
                                    )) {
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.LightGray)
                                .swipeable(
                                    state = swipeableState,
                                    anchors = mapOf(0f to 0, 1000f to 1),
                                    orientation = Orientation.Horizontal,
                                    thresholds = { _, _ -> FractionalThreshold(0.3f) }
                                )) {
                                Row() {
                                    IconButton(onClick = { /*TODO*/ }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                                    }
                                    IconButton(onClick = {
                                        scope.launch {
                                            CheckInService.API.deleteEntry(
                                                DeleteRequest(
                                                    listOfRecords!!.getJSONObject(it)
                                                        .getJSONArray("days").getJSONObject(x)
                                                        .getString("entry_id"),
                                                    "123"
                                                )
                                            )
                                             withContext(Dispatchers.Main) {
                                                 visible = false
                                             }
                                        }

                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete")


                                    }
                                }

                                Row(horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .offset {
                                            IntOffset(
                                                swipeableState.offset.value.roundToInt() / 3,
                                                0
                                            )
                                        }
                                        .background(Color.White)
                                        .padding(top = 10.dp, start = 5.dp)) {
                                    Text(
                                        listOfRecords!!.getJSONObject(it).getJSONArray("days")
                                            .getJSONObject(x).getString("date").replace("\"", ""),
                                        Modifier.align(
                                            Alignment.CenterVertically
                                        )
                                    )

                                    Column(
                                        verticalArrangement = Arrangement.SpaceAround,
                                        modifier = Modifier
                                    ) {
                                        Text(
                                            TimeConverter.convertUnixToAMPM(
                                                listOfRecords!!.getJSONObject(
                                                    it
                                                ).getJSONArray("days").getJSONObject(x)
                                                    .getLong("time_in")
                                            ), modifier = Modifier
                                        )
                                        if (TimeConverter.convertUnixToAMPM(
                                                listOfRecords!!.getJSONObject(
                                                    it
                                                ).getJSONArray("days").getJSONObject(x)
                                                    .getLong("time_out")
                                            ) != ""
                                        ) {
                                            Text(
                                                TimeConverter.convertUnixToAMPM(
                                                    listOfRecords!!.getJSONObject(
                                                        it
                                                    ).getJSONArray("days").getJSONObject(x)
                                                        .getLong("time_out")
                                                ), Modifier
                                            )
                                        }
                                    }


                                }
                            }

                            //Text(listOfRecords!!.getJSONObject(it).getJSONArray("days").getJSONObject(x).toString())
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun SwipeableRow() {

}