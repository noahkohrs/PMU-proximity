package com.inc.pmu

import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.inc.pmu.models.Player
import com.inc.pmu.models.Suit
import com.inc.pmu.viewmodels.ViewModelHost
import com.inc.pmu.viewmodels.ViewModelListener
import com.inc.pmu.viewmodels.ViewModelPMU
import com.inc.pmu.viewmodels.ViewModelPMUFactory

class TeamsPage : Fragment(R.layout.teams_page) {

    private lateinit var vmGame: ViewModelPMU
    lateinit var playButton : Button
    lateinit var clubTeam : TextView
    lateinit var spadesTeam : TextView
    lateinit var heartTeam : TextView
    lateinit var diamondTeam : TextView

    companion object {
        fun newInstance() = TeamsPage()
    }
    override fun onStart() {
        super.onStart()

        vmGame = ViewModelProvider(requireActivity(), ViewModelPMUFactory())[ViewModelPMU::class.java]

        playButton = requireView().findViewById(R.id.jouerButton)
        clubTeam = requireView().findViewById(R.id.teamTrefle)
        spadesTeam = requireView().findViewById(R.id.teamPique)
        heartTeam = requireView().findViewById(R.id.teamCoeur)
        diamondTeam = requireView().findViewById(R.id.teamCarreau)


        playButton.setOnClickListener {
            if (vmGame is ViewModelHost) {
                val fragment = PushUpBet.newInstance()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit()
            }
        }

        vmGame.addListener(
            object : ViewModelListener() {
                override fun onBetValidated(suit: Suit?, players: MutableCollection<Player>?) {
                    heartTeam.setText("")
                    clubTeam.setText("")
                    spadesTeam.setText("")
                    diamondTeam.setText("")

                    var team : TextView? = null

                    if (players != null) {
                        for (player in players) {
                            when (player.bet?.suit) {
                                Suit.HEARTS -> {
                                    team = heartTeam
                                }

                                Suit.SPADES -> {
                                    team = spadesTeam
                                }

                                Suit.DIAMONDS -> {
                                    team = diamondTeam
                                }

                                Suit.CLUBS -> {
                                    team = clubTeam
                                }

                                else -> {}
                            }
                            team?.append(player.playerName + '\n')
                        }
                    }
                }
            }
        )

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing to disable the default back button behavior
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        setButtonProperties()
    }

    fun setButtonProperties() {
        if (vmGame is ViewModelHost) {
        }
    }
}