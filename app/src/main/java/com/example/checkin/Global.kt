package com.example.checkin

import android.app.Application

class Global: Application(){
    val getDB by lazy{ LocalDataSource(applicationContext).getDatabase()}


}