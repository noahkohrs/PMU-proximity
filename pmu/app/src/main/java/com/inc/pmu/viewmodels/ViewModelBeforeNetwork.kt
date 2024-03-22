package com.inc.pmu.viewmodels

import androidx.lifecycle.ViewModel

class ViewModelBeforeNetwork : ViewModel() {
    private var username = ""

    public fun getUsername(): String {
        return username
    }

    public fun setUsername(name: String) {
        username = name
    }
}