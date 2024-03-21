package com.inc.pmu

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class WaitingPage : Fragment(R.layout.waiting_page) {

    companion object {
        fun newInstance() = WaitingPage()
    }
    override fun onStart() {
        super.onStart()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing to disable the default back button behavior
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
}