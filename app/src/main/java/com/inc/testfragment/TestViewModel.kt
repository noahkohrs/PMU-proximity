package com.inc.testfragment

import androidx.lifecycle.ViewModel

class TestViewModel : ViewModel() {
    private var number = 0
    public fun addOne() {
        number += 1
    }

    public fun minusOne() {
        number -= 1
    }

    public fun getNumber(): Int {
        return number
    }
}