package com.inc.pmu

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.CountDownTimer
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.inc.pmu.models.Card
import com.inc.pmu.viewmodels.ViewModelListener
import com.inc.pmu.viewmodels.ViewModelPMU
import com.inc.pmu.viewmodels.ViewModelPMUFactory


class GameBoard : Fragment(R.layout.game_page) {

    private lateinit var vmGame: ViewModelPMU
    private lateinit var context: Context

    private lateinit var deckButton : ImageButton
    private lateinit var playedCards : ImageView
    private lateinit var popupButton : Button

    companion object {
        fun newInstance() = GameBoard()
    }

    override fun onStart() {
        super.onStart()

        vmGame = ViewModelProvider(requireActivity(), ViewModelPMUFactory())[ViewModelPMU::class.java]
        context = requireContext()

        deckButton = requireView().findViewById(R.id.deck)
        playedCards = requireView().findViewById(R.id.playedCards)
        popupButton = requireView().findViewById(R.id.AlertWithCustomStyle)


        deckButton.setOnClickListener {
            vmGame.drawCard()
            deckButton.isClickable = false
            Log.d(Global.TAG, "Bouton non clickable")
            val timer = object: CountDownTimer(15000, 1000) {
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

        popupButton.setOnClickListener{
            basicAlert(requireView())
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
                    Log.d(Global.TAG, "Drawn card: $card")
                    var drawCard : Drawable = getCardDrawable(card, context)
                    playedCards.setImageDrawable(drawCard)
                }
            }
        )
    }

    fun basicAlert(view: View) {

        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))
            .setTitle("Androidly Alert")
            .setMessage("We have a message")

        val alertDialog = builder.create()

        alertDialog.show()

        val timer = object: CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }
            override fun onFinish() {
                alertDialog.cancel()
            }
        }
        timer.start()

    }

        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
            Toast.makeText(context,
                android.R.string.yes, Toast.LENGTH_SHORT).show()
        }
        val negativeButtonClick = { dialog: DialogInterface, which: Int ->
            Toast.makeText(context,
                android.R.string.no, Toast.LENGTH_SHORT).show()
        }

    fun getCardDrawable(card : Card, context : Context) : Drawable {
        var uri : String = "@drawable/${card.toString()}"
        var imageId : Int = context.resources.getIdentifier(uri,null, context.packageName)
        Log.d(Global.TAG, imageId.toString())
        return context.resources.getDrawable(imageId)
    }
}