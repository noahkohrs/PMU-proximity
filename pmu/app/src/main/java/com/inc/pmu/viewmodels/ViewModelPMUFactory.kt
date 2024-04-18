package com.inc.pmu.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.inc.pmu.TAG

class ViewModelPMUFactory(private val mode : Mode) : ViewModelProvider.Factory {

    public enum class Mode {
        HOST, CLIENT, NONE
    }

    companion object {
        private var SELECTED : Mode = Mode.NONE
    }

    constructor() : this(Mode.NONE)

    init {
        if (mode == Mode.HOST || mode == Mode.CLIENT) {
            SELECTED = mode
        } else {
            if (SELECTED == Mode.NONE)
                Log.d(TAG.TAG, "MODE SHOULD NOT BE NONE")
        }
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return when (SELECTED) {
            Mode.HOST -> {
                ViewModelHost() as T
            }
            Mode.CLIENT -> {
                ViewModelClient() as T
            }
            Mode.NONE -> {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }

    }
}