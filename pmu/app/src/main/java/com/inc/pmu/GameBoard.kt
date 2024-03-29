package com.inc.pmu

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.inc.pmu.models.Card
import com.inc.pmu.viewmodels.ViewModelListener
import com.inc.pmu.models.Suit
import com.inc.pmu.viewmodels.ViewModelPMU
import com.inc.pmu.viewmodels.ViewModelPMUFactory


class GameBoard : Fragment(R.layout.game_page) {
    val MIN_TIME_DRAW_CARD: Long = 2000


    private lateinit var vmGame: ViewModelPMU
    private lateinit var context: Context
    private lateinit var v : View

    private lateinit var deckButton : ImageButton
    private lateinit var playedCards : ImageView
    private lateinit var spades : ImageView
    private lateinit var club : ImageView
    private lateinit var heart : ImageView
    private lateinit var diamonds : ImageView



    companion object {
        fun newInstance() = GameBoard()
    }


    override fun onStart() {
        super.onStart()

        vmGame = ViewModelProvider(requireActivity(), ViewModelPMUFactory())[ViewModelPMU::class.java]
        context = requireContext()
        v = requireView()

        spades = requireView().findViewById(R.id.s1)
        club = requireView().findViewById(R.id.c1)
        heart = requireView().findViewById(R.id.h1)
        diamonds = requireView().findViewById(R.id.d1)
        deckButton = requireView().findViewById(R.id.deck)
        playedCards = requireView().findViewById(R.id.playedCards)


        deckButton.setOnClickListener {
            vmGame.drawCard()
            deckButton.isClickable = false
            Log.d(Global.TAG, "Bouton non clickable")
            val timer = object: CountDownTimer(MIN_TIME_DRAW_CARD, 100) {
                override fun onTick(millisUntilFinished: Long) {
                    //affiche les secondes sur le deck transparent
                }
                override fun onFinish() {
                    deckButton.isClickable = true
                    Log.d(Global.TAG, "Bouton re-clickable !")
                }
            }
            timer.start()
        }

        if (vmGame.isHost()) {
            deckButton.isClickable = true
        }
        else {
            deckButton.isClickable = false
        }

        vmGame.addListener(
            object : ViewModelListener() {
                override fun onCardDrawn(card: Card) {
                    var cardPos : Int = vmGame.game.board.riderPos.get(card.suit) as Int
                    var dividerId = getDividerFromPos(cardPos, context)
                    var divider : View = v.findViewById(dividerId)

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

        deckButton.isClickable = true

        vmGame.addListener(
            object : ViewModelListener() {
                override fun onCardDrawn(card: Card) {
                    Log.d(Global.TAG, "Drawn card: $card")
                    var drawCard : Drawable = getCardDrawable(card, context)
                    playedCards.setImageDrawable(drawCard)
                }
            }
        )
    }

    fun getCardDrawable(card : Card, context : Context) : Drawable {
        var uri : String = "@drawable/${card.toString()}"
        var imageId : Int = context.resources.getIdentifier(uri,null, context.packageName)
        Log.d(Global.TAG, imageId.toString())
        return context.resources.getDrawable(imageId)
    }

    fun getDividerFromPos(pos : Int, context : Context): Int {
        val res = context.getResources()
        var dividerId : Int = 0
        if (pos == 6) {
            dividerId = res.getIdentifier(("id/arrivee"),"id", context.packageName)
        } else {
            dividerId = res.getIdentifier(("id/divider" + pos), "id", context.packageName)
        }
        return dividerId
    }
}