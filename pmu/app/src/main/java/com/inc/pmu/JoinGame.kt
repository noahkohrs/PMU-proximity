package com.inc.pmu

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.inc.pmu.models.Global
import com.inc.pmu.models.Player

class JoinGame : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.join_page)
    }

    override fun onStart() {
        super.onStart()
    }

    fun onClickHomePage(view: View) {
        intent.setClass(this,HomePage::class.java)
        startActivities(arrayOf(intent))
    }
}