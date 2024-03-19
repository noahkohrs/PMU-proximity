package com.inc.pmu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class WaitingPage : Fragment(R.layout.waiting_page) {

    companion object {
        fun newInstance() = WaitingPage()
    }
    override fun onStart() {
        super.onStart()
    }
}