package com.inc.pmu

import android.graphics.Color
import android.util.Log
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.inc.pmu.models.Player
import com.inc.pmu.models.Suit
import com.inc.pmu.viewmodels.ViewModelListener
import com.inc.pmu.viewmodels.ViewModelPMU
import com.inc.pmu.viewmodels.ViewModelPMUFactory

class BetChoice : Fragment(R.layout.bet_choice) {

    var suitChosen : Suit? = null

    private lateinit var vmGame: ViewModelPMU

    private lateinit var buttonH : Button
    private lateinit var buttonS : Button
    private lateinit var buttonC : Button
    private lateinit var buttonD : Button
    private lateinit var buttonPlay : Button
    private lateinit var availableButtons : ArrayList<Button>



    companion object {
        fun newInstance() = BetChoice()
    }

    override fun onStart() {
        super.onStart()

        vmGame = ViewModelProvider(requireActivity(), ViewModelPMUFactory())[ViewModelPMU::class.java]

        buttonH = requireView().findViewById(R.id.coeurButton)
        buttonS = requireView().findViewById(R.id.piqueButton)
        buttonC = requireView().findViewById(R.id.trefleButton)
        buttonD = requireView().findViewById(R.id.carreauButton)
        buttonPlay = requireView().findViewById(R.id.jouerButton)

        availableButtons = arrayListOf(buttonH, buttonS, buttonC, buttonD)


        var betListener = object : ViewModelListener() {
            override fun onBetValidated(suit: Suit?, players: MutableCollection<Player>?) {
                Log.d(Global.TAG, "OnBetValidated")

                var nSuitBet = 0
                if (players != null) {
                    for (player in players) {
                        if (player.bet.number != -1 && player.bet.suit == suit) {
                            nSuitBet++
                        }
                    }


                    if (nSuitBet > players.size/4) {
                        setUnchoosable(suit)
                    }
                }
            }
        }

        buttonH.setOnClickListener {
            for (button in availableButtons) {
                button.setBackgroundColor(Color.WHITE)
            }
            buttonH.setBackgroundColor(Color.YELLOW)
            buttonPlay.isClickable = true
            buttonPlay.setBackgroundColor(Color.YELLOW)
            suitChosen = Suit.HEARTS
        }

        buttonS.setOnClickListener {
            for (button in availableButtons) {
                button.setBackgroundColor(Color.WHITE)
            }
            buttonS.setBackgroundColor(Color.YELLOW)
            buttonPlay.isClickable = true
            buttonPlay.setBackgroundColor(Color.YELLOW)
            suitChosen = Suit.SPADES
        }

        buttonC.setOnClickListener {
            for (button in availableButtons) {
                button.setBackgroundColor(Color.WHITE)
            }
            buttonC.setBackgroundColor(Color.YELLOW)
            buttonPlay.isClickable = true
            buttonPlay.setBackgroundColor(Color.YELLOW)
            suitChosen = Suit.CLUBS
        }

        buttonD.setOnClickListener {
            for (button in availableButtons) {
                button.setBackgroundColor(Color.WHITE)
            }
            buttonD.setBackgroundColor(Color.YELLOW)
            buttonPlay.isClickable = true
            buttonPlay.setBackgroundColor(Color.YELLOW)
            suitChosen = Suit.DIAMONDS
        }

        buttonPlay.setOnClickListener {
            suitChosen?.let { it1 -> vmGame.bet(vmGame.counter, it1) }
            vmGame.removeListener(betListener)
            val fragment = TeamsPage.newInstance()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }

        vmGame.addListener(betListener)

        buttonPlay.isClickable = false

        var unavailableSuits = vmGame.getSuitsUnavailable()
        for (suit in unavailableSuits) {
            setUnchoosable(suit)
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing to disable the default back button behavior
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }


    fun setUnchoosable(suit : Suit?) {
        when (suit) {
            Suit.SPADES -> {
                buttonS.setBackgroundColor(resources.getColor(R.color.unavailable))
                buttonS.isClickable = false
                availableButtons.remove(buttonS)
            }
            Suit.HEARTS -> {
                buttonH.setBackgroundColor(resources.getColor(R.color.unavailable))
                buttonH.isClickable = false
                availableButtons.remove(buttonH)
            }
            Suit.CLUBS -> {
                buttonC.setBackgroundColor(resources.getColor(R.color.unavailable))
                buttonC.isClickable = false
                availableButtons.remove(buttonC)
            }
            Suit.DIAMONDS -> {
                buttonD.setBackgroundColor(resources.getColor(R.color.unavailable))
                buttonD.isClickable = false
                availableButtons.remove(buttonD)
            }
            else -> {}
        }
        if (suitChosen == suit) {
            suitChosen = null
            buttonPlay.isClickable = false
            buttonPlay.setBackgroundColor(resources.getColor(R.color.unavailable))
        }
    }


}