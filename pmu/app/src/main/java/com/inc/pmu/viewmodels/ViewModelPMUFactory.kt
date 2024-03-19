package com.inc.pmu.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.inc.pmu.Global

class ViewModelPMUFactory(private val mode : Mode, private val connectionsClient: ConnectionsClient?) : ViewModelProvider.Factory {

    public enum class Mode {
        HOST, CLIENT, NONE
    }

    companion object {
        private var SELECTED : Mode = Mode.NONE
        private lateinit var client : ConnectionsClient
    }

    constructor() : this(Mode.NONE, null)

    init {
        if (mode == Mode.HOST || mode == Mode.CLIENT) {
            SELECTED = mode
        } else {
            if (SELECTED == Mode.NONE)
                Log.d(Global.TAG, "MODE SHOULD NOT BE NONE")
        }

        if (connectionsClient != null) {
            client = connectionsClient
        }
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        
        return when (SELECTED) {
            Mode.HOST -> {
                ViewModelHost(client) as T
            }
            Mode.CLIENT -> {
                ViewModelClient(client) as T
            }
            Mode.NONE -> {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

    }
}