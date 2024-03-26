package com.inc.pmu

import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.inc.pmu.models.Card
import com.inc.pmu.viewmodels.ViewModelListener
import com.inc.pmu.viewmodels.ViewModelPMU
import com.inc.pmu.viewmodels.ViewModelPMUFactory

class GameBoard : Fragment(R.layout.game_page) {

    private lateinit var vmGame: ViewModelPMU

    private lateinit var deckButton : ImageButton
    private lateinit var playedCards : ImageView

    companion object {
        fun newInstance() = GameBoard()
    }

    override fun onStart() {
        super.onStart()

        vmGame = ViewModelProvider(requireActivity(), ViewModelPMUFactory())[ViewModelPMU::class.java]

        deckButton = requireView().findViewById(R.id.deck)
        playedCards = requireActivity().findViewById(R.id.playedCards)


        deckButton.setOnClickListener {
            vmGame.drawCard()
        }

        deckButton.isClickable = true

        vmGame.addListener(
            object : ViewModelListener() {
                override fun onCardDrawn(card: Card) {
                    Log.d(Global.TAG, card.toString())
                    if (card != null) {
                        var drawCard : Drawable = linkCardToDrawable(card)
                        playedCards.setImageDrawable(drawCard)
                    }
                }
            }
        )
    }

    fun linkCardToDrawable(card : Card) : Drawable {
        val uri = "@drawable/${card.toString()}"
        var imageIndex : Int = resources.getIdentifier(uri, null, null)
        return resources.getDrawable(imageIndex)
    }
}