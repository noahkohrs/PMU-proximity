package com.inc.pmu

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class WaitingForPlayer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.waiting_for_player)
    }

    override fun onStart() {
        super.onStart()
    }

    fun onClickHomePage(view: View) {
        intent.setClass(this,HomePage::class.java)
        startActivities(arrayOf(intent))
    }

    fun onClickStartGame(view: View) {
        intent.setClass(this,BetChoice::class.java)
        startActivities(arrayOf(intent))
    }
}