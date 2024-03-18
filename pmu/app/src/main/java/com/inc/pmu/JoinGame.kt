package com.inc.pmu

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

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