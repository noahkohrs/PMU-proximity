package com.inc.pmu

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.inc.pmu.models.Global
import com.inc.pmu.models.Player

class WaitingForPlayer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.waiting_for_player)
    }

    override fun onStart() {
        super.onStart()
    }
}