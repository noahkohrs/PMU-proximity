package com.inc.pmu

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.inc.pmu.models.Player
import com.inc.pmu.models.Suit

class BetChoice : AppCompatActivity() {

    var player : Player? = null
    var suit : Suit? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bet_choice)
    }

    override fun onStart() {
        super.onStart()
        Log.d(Global.TAG, "OnStart method")
        val playerIntent = intent
        //player = playerIntent.getSerializableExtra("Player") as Player
    }

    fun onClickHearts(view: View) {
        var button : Button = findViewById(R.id.coeurButton) as Button
        button.setBackgroundColor(Color.YELLOW)
        suit = Suit.HEARTS
    }

    fun onClickSpades(view: View) {
        var button : Button = findViewById(R.id.piqueButton) as Button
        button.setBackgroundColor(Color.YELLOW)
        suit = Suit.SPADES
    }

    fun onClickClubs(view: View) {
        var button : Button = findViewById(R.id.trefleButton) as Button
        button.setBackgroundColor(Color.YELLOW)
        suit = Suit.CLUBS
    }

    fun onClickDiamonds(view: View) {
        var button : Button = findViewById(R.id.carreauButton) as Button
        button.setBackgroundColor(Color.YELLOW)
        suit = Suit.DIAMONDS
    }

    fun onClickJouer(view: View) {
        intent.setClass(this,WaitingPage::class.java)
        intent.putExtra("Player", player)
        intent.putExtra("Suit", suit)
        startActivities(arrayOf(intent))
    }
}