package com.inc.pmu

import android.R
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.inc.pmu.models.Card
import com.inc.pmu.models.Suit
import com.inc.pmu.viewmodels.ViewModelListener
import com.inc.pmu.viewmodels.ViewModelPMU


class GameBoard : Fragment(R.layout.game_page) {

    private lateinit var vmGame: ViewModelPMU

    private lateinit var deckButton : Button
    private lateinit var spades : ImageView
    private lateinit var club : ImageView
    private lateinit var heart : ImageView
    private lateinit var diamonds : ImageView

    companion object {
        fun newInstance() = GameBoard()
    }


    override fun onStart() {
        super.onStart()

        deckButton = requireActivity().findViewById(R.id.deck)
        spades = requireActivity().findViewById(R.id.s1)
        club = requireActivity().findViewById(R.id.c1)
        heart = requireActivity().findViewById(R.id.h1)
        diamonds = requireActivity().findViewById(R.id.d1)


        deckButton.setOnClickListener {
            
        }

        vmGame.addListener(
            object : ViewModelListener() {
                override fun onCardDrawn(card: Card) {
                    var cardPos : Int = vmGame.game.board.riderPos.get(card.suit) as Int
                    val id = resources.getIdentifier(("divider"+cardPos), "id", context!!.packageName)
                    var divider : View = requireActivity().findViewById(id) as View
                    var c : ImageView
                    when(card.suit) {
                        Suit.HEARTS -> c = heart
                        Suit.SPADES -> c = spades
                        Suit.CLUBS -> c = club
                        Suit.DIAMONDS -> c = diamonds
                    }

                    val params = c.layoutParams as ConstraintLayout.LayoutParams
                    params.topToBottom = divider.id
                    c.requestLayout()
                }
            }
        )
    }
}