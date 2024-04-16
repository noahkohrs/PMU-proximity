package com.inc.pmu

import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.inc.pmu.viewmodels.ViewModelListener
import com.inc.pmu.viewmodels.ViewModelPMU
import com.inc.pmu.viewmodels.ViewModelPMUFactory

class WaitingForPlayer : Fragment(R.layout.waiting_for_player) {

    private lateinit var homePageButton: Button
    private lateinit var launchButton: Button

    private lateinit var vmGame: ViewModelPMU

    private lateinit var activity: FragmentActivity

    companion object {
        fun newInstance() = WaitingForPlayer()
    }

    fun quitAction() {
        vmGame.stopConnection()
        val fragment = HomePage.newInstance()
        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    override fun onStart() {
        super.onStart()

        activity = requireActivity()

        vmGame = ViewModelProvider(requireActivity(), ViewModelPMUFactory())[ViewModelPMU::class.java]

        homePageButton = requireView().findViewById(R.id.quitButton)
        launchButton = requireView().findViewById(R.id.lauchButton)

        homePageButton.setOnClickListener {
            quitAction()
        }

       launchButton.setOnClickListener {
           vmGame.startBet()
            val fragment = PushUpBet.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }

        launchButton.isClickable = false
        launchButton.setBackgroundColor(resources.getColor(R.color.unavailable))

        vmGame = ViewModelProvider(requireActivity(), ViewModelPMUFactory())[ViewModelPMU::class.java]
        if (vmGame.isHost()) {
            requireView().findViewById<TextView>(R.id.playerList).text = vmGame.localUsername
        }

        vmGame.addListener(
            object : ViewModelListener() {
                override fun onPlayerListUpdate(playerList: Array<out String>?) {
                    if (playerList != null) {
                        var textPlayer =  requireView().findViewById<TextView>(R.id.playerList)
                        var data : String = ""
                        for (player in playerList) {
                            data += player + '\n'
                        }
                        textPlayer.text = data

                        if (playerList.size > 1 && vmGame.isHost()) {
                            launchButton.isClickable = true
                            launchButton.setBackgroundColor(resources.getColor(R.color.selectOrValidate))
                        }
                        else {
                            launchButton.isClickable = false
                            launchButton.setBackgroundColor(resources.getColor(R.color.unavailable))
                        }
                    }
                }
                override fun onConnectionLost() {
                    quitAction()
                }
            }
        )

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing to disable the default back button behavior
            }
        }

        vmGame.addListener(
            object : ViewModelListener() {
                override fun onBetStart() {
                    vmGame.removeAllListeners()
                    Log.d(Global.TAG, "Start bet !")
                    val fragment = PushUpBet.newInstance()
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit()
                }
            }
        )

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
}