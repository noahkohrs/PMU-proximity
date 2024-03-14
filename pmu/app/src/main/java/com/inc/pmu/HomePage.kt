package com.inc.pmu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.inc.pmu.models.Global
import com.inc.pmu.models.Player

class HomePage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)
    }

    override fun onStart() {
        super.onStart()
        val pseudoIntent = intent
        var pseudo = pseudoIntent.getStringExtra("Pseudo")
        if (pseudo == null) {
            pseudo = "default"
        }
        val p1 = Player("jk", pseudo)
        Log.d(Global.TAG, p1.playerName)
    }

    fun onClickCreate(view: View) {
        intent.setClass(this,WaitingForPlayer::class.java)
        startActivities(arrayOf(intent))
    }

    fun onClickJoin(view: View) {
        intent.setClass(this,JoinGame::class.java)
        startActivities(arrayOf(intent))
    }
}