package com.inc.pmu

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.inc.pmu.models.Player
import com.inc.pmu.viewmodels.ViewModelClient
import com.inc.pmu.viewmodels.ViewModelPMU
import com.inc.pmu.viewmodels.ViewModelPMUFactory

class HomePage : AppCompatActivity() {

    lateinit var connectionsClient : ConnectionsClient
    lateinit var viewModel : ViewModelPMU
    var username = ""

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
        username = pseudo
        val p1 = Player("jk", pseudo)
        //Log.d(Global.TAG, p1.playerName)
    }

    fun onClickCreate(view: View) {
        connectionsClient = Nearby.getConnectionsClient(applicationContext)
        viewModel = ViewModelProvider(this, ViewModelPMUFactory(ViewModelPMUFactory.Mode.HOST,connectionsClient)).get(ViewModelPMU::class.java)
        viewModel.localUsername = username
        viewModel = ViewModelProvider(this, ViewModelPMUFactory()).get(ViewModelPMU::class.java)
        viewModel.startHosting()
        Log.d(Global.TAG, "Je m'appelle ${viewModel.localUsername} et je viens de créer une partie !")
        intent.setClass(this,WaitingForPlayer::class.java)
        startActivities(arrayOf(intent))
    }

    fun onClickJoin(view: View) {
        connectionsClient = Nearby.getConnectionsClient(applicationContext)
        viewModel = ViewModelProvider(this, ViewModelPMUFactory(ViewModelPMUFactory.Mode.CLIENT,connectionsClient)).get(ViewModelPMU::class.java)
        viewModel.localUsername = username
        viewModel = ViewModelProvider(this, ViewModelPMUFactory()).get(ViewModelPMU::class.java)
        viewModel.startDiscovering()
        Log.d(Global.TAG, "Je m'appelle ${viewModel.localUsername} et je cherche une partie !")
        viewModel.startDiscovering()
        intent.setClass(this,JoinGame::class.java)
        startActivities(arrayOf(intent))
    }
}