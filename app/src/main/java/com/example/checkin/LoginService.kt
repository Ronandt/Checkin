package com.example.checkin

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder

class LoginService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
       return null
    }

    override fun onCreate() {
        super.onCreate()
        var timer = object : CountDownTimer(1000000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                TODO("Not yet implemented")
            }

            override fun onFinish() {
                TODO("Not yet implemented")
            }

        }

    }

}