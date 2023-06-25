package com.example.checkin

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

enum class Status {
    Available, Unavailable, Losing, Lost
}


class NetworkObserver(val context: Context) {
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
    fun observeNetworkState(): Flow<Status> {
        return callbackFlow<Status>{
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    trySend(Status.Available)
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    trySend(Status.Unavailable)
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    trySend(Status.Losing)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    trySend(Status.Lost)
                }
            }
            connectivityManager.registerDefaultNetworkCallback(callback)
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }

        }.distinctUntilChanged()


    }

}