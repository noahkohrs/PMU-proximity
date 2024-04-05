package com.inc.pmu

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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.inc.pmu.models.Card
import com.inc.pmu.models.Suit
import com.inc.pmu.viewmodels.ViewModelListener
import com.inc.pmu.viewmodels.ViewModelPMU
import com.inc.pmu.viewmodels.ViewModelPMUFactory


class GameBoard : Fragment(R.layout.game_page) {
    val MIN_TIME_DRAW_CARD: Long = 2000


    private lateinit var vmGame: ViewModelPMU
    private lateinit var context: Context
    private lateinit var view: View

    private lateinit var deckButton : ImageButton
    private lateinit var playedCards : ImageView
    private lateinit var spades : ImageView
    private lateinit var club : ImageView
    private lateinit var heart : ImageView
    private lateinit var diamonds : ImageView
    private lateinit var sideCards : Array<Array<ImageView>>

    private lateinit var pushButton: Button

    private lateinit var alertDialogue: AlertDialog

    private lateinit var  currentSuit: TextView
    private lateinit var currentNbPushUps: TextView

    companion object {
        fun newInstance() = GameBoard()
    }


    override fun onStart() {
        super.onStart()

        vmGame = ViewModelProvider(requireActivity(), ViewModelPMUFactory())[ViewModelPMU::class.java]
        context = requireContext()
        view = requireView()

        spades = requireView().findViewById(R.id.s1)
        club = requireView().findViewById(R.id.c1)
        heart = requireView().findViewById(R.id.h1)
        diamonds = requireView().findViewById(R.id.d1)
        deckButton = requireView().findViewById(R.id.deck)
        playedCards = requireView().findViewById(R.id.playedCards)
        pushButton = requireView().findViewById(R.id.pushButton)

        currentSuit = requireView().findViewById(R.id.playerSuit)
        currentNbPushUps = requireView().findViewById(R.id.currentPushUps)

        var suit = vmGame.game.players.get(vmGame.localId)!!.bet.suit
        when(suit) {
            Suit.HEARTS -> currentSuit.text = "Coeur"
            Suit.SPADES -> currentSuit.text = "Pique"
            Suit.CLUBS -> currentSuit.text = "Trèfle"
            Suit.DIAMONDS -> currentSuit.text = "Carreau"
        }

        sideCards = Array(vmGame.game.board.sideCards.size) { Array(2) { spades } }
        for (i in 1..vmGame.game.board.sideCards.size) {
            var id = resources.getIdentifier(("id/left"+i),"id", context.packageName)
            var leftCard : ImageView = requireView().findViewById(id)
            id = resources.getIdentifier(("id/right"+i),"id", context.packageName)
            var rightCard : ImageView = requireView().findViewById(id)
            var sideCardsi : Array<ImageView> = arrayOf(leftCard, rightCard)
            sideCards[i-1] = sideCardsi
        }


        deckButton.setOnClickListener {
            vmGame.checkWin()
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


        pushButton.setOnClickListener {
            vmGame.doPushUps()
        }

        pushButton.isClickable = false

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

                    var cardPos : Int = vmGame.game.board.riderPos.get(card.suit) as Int
                    var dividerId = getDividerFromPos(cardPos, context)
                    var divider : View = view.findViewById(dividerId)

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

                    val board = vmGame.game.board
                    val indexSideCards = board.sideCardsDiscoverIndex
                    for (i in 0..<indexSideCards) {
                        var leftCard : Drawable = getCardDrawable(board.sideCards[i][0], context)
                        var rightCard : Drawable = getCardDrawable(board.sideCards[i][1], context)
                        sideCards[i][0].setImageDrawable(leftCard)
                        sideCards[i][1].setImageDrawable(rightCard)
                    }

                    var suit_string: String
                    when(card.suit) {
                        Suit.HEARTS -> suit_string = "Coeur"
                        Suit.SPADES -> suit_string = "Pique"
                        Suit.CLUBS -> suit_string = "Trèfle"
                        Suit.DIAMONDS -> suit_string = "Carreau"

                    }
                    pushButton.text = "Faire reculer\n" + suit_string
                    if (vmGame.game.players.get(vmGame.localId)?.bet?.suit == card.suit) {
                        pushButton.isClickable = false
                        pushButton.setBackgroundColor(context.resources.getColor(R.color.unavailable))
                    }
                    else {
                        pushButton.isClickable = true
                        pushButton.setBackgroundColor(context.resources.getColor(R.color.white))
                    }
                }
            }
        )

        vmGame.addListener(
            object : ViewModelListener() {
                override fun onBoardUpdate() {
                    currentNbPushUps.text = vmGame.game.players.get(vmGame.localId)!!.currentPushUps.toString()
                    for (suit in Suit.values()) {

                        var c : ImageView
                        when(suit) {
                            Suit.HEARTS -> c = heart
                            Suit.SPADES -> c = spades
                            Suit.CLUBS -> c = club
                            Suit.DIAMONDS -> c = diamonds
                        }

                        var cardPos : Int = vmGame.game.board.riderPos.get(suit) as Int
                        var dividerId = getDividerFromPos(cardPos, context)
                        var divider : View = view.findViewById(dividerId)

                        val params = c.layoutParams as ConstraintLayout.LayoutParams
                        params.topToBottom = divider.id
                        c.requestLayout()
                    }
                }
            }
        )

        vmGame.addListener(
            object : ViewModelListener() {
                override fun onPlayerDoingPushUps(puuid : String) {
                    pushButton.isClickable = false
                    pushButton.setBackgroundColor(context.resources.getColor(R.color.unavailable))
                    if (puuid == vmGame.localId) {
                        alertDialogue = doPushups(view)
                    }
                    else {
                        alertDialogue = waitForPushups(view, puuid)
                    }
                }
            }
        )

        vmGame.addListener(
            object : ViewModelListener() {
                override fun onStartVote(puuid: String) {
                    if (alertDialogue.isShowing) {
                        alertDialogue.dismiss()
                    }

                    if (vmGame.localId == puuid) {
                        alertDialogue = waitingForVotes(view)
                    }
                    else {
                       alertDialogue = votes(view)
                    }
                }
            }
        )

        vmGame.addListener(
            object : ViewModelListener() {
                override fun onVoteFinished(puuid: String?, voteResult: Boolean) {
                    alertDialogue.dismiss()
                }
            }
        )

        vmGame.addListener(
            object : ViewModelListener() {
                override fun onGameEnds(winner: String) {
                    val player = vmGame.game.players.get(vmGame.localId)!!
                    if (player.bet.suit.name == winner) {
                        alertDialogue = winnerPopup(view, winner)
                    }
                    else {
                        alertDialogue = looserPopup(view, winner)
                    }
                }
            }
        )

        vmGame.addListener(
            object : ViewModelListener() {
                override fun onEndPushUps(count: Int) {
                    alertDialogue.dismiss()
                    alertDialogue = loserPushUps(view, count)
                }
            }
        )

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing to disable the default back button behavior
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    fun doPushups(view: View): AlertDialog {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))
            .setMessage("Faites ${vmGame.game.players.get(vmGame.localId)?.currentPushUps} pompes")
            .setPositiveButton("C'est fait", positivePushupsButtonClick)

        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        alertDialog.show()

        if (vmGame.isHost()) {
            deckButton.isClickable = true
        }
        return alertDialog
    }

    fun waitForPushups(view: View, puuid : String): AlertDialog {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))
            .setMessage("${vmGame.game.players.get(puuid)?.playerName} fait des pompes")

        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        alertDialog.show()

        return alertDialog
    }

    val positivePushupsButtonClick = { dialog: DialogInterface, which: Int ->
        Toast.makeText(context,
            "pompes validées", Toast.LENGTH_SHORT).show()
        vmGame.pushUpsDone()
    }

    fun waitingForVotes(view: View): AlertDialog {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))
            .setMessage("Attente des votes")

        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        alertDialog.show()

        return alertDialog
    }

    fun votes(view: View): AlertDialog {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))
            .setMessage("Validez vous les pompes")
            .setPositiveButton("Valider", positiveVoteButton)
            .setNegativeButton("Refuser", negativeVoteButton)

        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        alertDialog.show()

        return  alertDialog
    }

    val positiveVoteButton = { dialog: DialogInterface, which: Int ->
        Toast.makeText(context,
            "Validé", Toast.LENGTH_SHORT).show()
        vmGame.vote(true)
    }

    val negativeVoteButton = { dialog: DialogInterface, which: Int ->
        Toast.makeText(context,
            "Refusé", Toast.LENGTH_SHORT).show()
        vmGame.vote(false)
    }

    val yesPuchupsButton = { dialog: DialogInterface, which: Int ->
        Toast.makeText(context,
            "Validé", Toast.LENGTH_SHORT).show()
        vmGame.doPushUps()
    }

    val noPuchupsButton = { dialog: DialogInterface, which: Int ->
        Toast.makeText(context,
            "Refusé", Toast.LENGTH_SHORT).show()
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

    fun winnerPopup(view: View, suit: String) : AlertDialog {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))
            .setTitle("Vous avez gagné")
            .setMessage("A qui voulez vous distribuer vos pompes")

        when(suit) {
            Suit.HEARTS.name -> {
                builder.setPositiveButton("Pique", distributedToSpade)
                    .setNeutralButton("Carreau", distributedToDiamond)
                    .setNegativeButton("Trèfle", distributedToClub)
            }

            Suit.SPADES.name -> {
                builder.setPositiveButton("Coeur", distributedToHearth)
                    .setNeutralButton("Carreau", distributedToDiamond)
                    .setNegativeButton("Trèfle", distributedToClub)
            }
            Suit.CLUBS.name -> {
                builder.setPositiveButton("Coeur", distributedToHearth)
                    .setNeutralButton("Carreau", distributedToDiamond)
                    .setNegativeButton("Pique", distributedToSpade)
            }
            Suit.DIAMONDS.name -> {
                builder.setPositiveButton("Coeur", distributedToHearth)
                    .setNeutralButton("Pique", distributedToSpade)
                    .setNegativeButton("Trèfle", distributedToClub)
            }
        }

        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        alertDialog.show()

        return  alertDialog
    }

    val distributedToHearth = fun(dialog: DialogInterface, which: Int): Unit {
        Toast.makeText(
            context,
            "pompes distribués à coeur", Toast.LENGTH_SHORT
        ).show()
        val suit = Suit.HEARTS
        vmGame.givePushUps(suit.name)
        val fragment = HomePage.newInstance()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    val distributedToSpade = fun(dialog: DialogInterface, which: Int): Unit {
        Toast.makeText(
            context,
            "pompes distribués à pique", Toast.LENGTH_SHORT
        ).show()
        val suit = Suit.SPADES
        vmGame.givePushUps(suit.name)
        val fragment = HomePage.newInstance()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    val distributedToClub = fun(dialog: DialogInterface, which: Int): Unit {
        Toast.makeText(
            context,
            "pompes distribués à trèfle", Toast.LENGTH_SHORT
        ).show()
        val suit = Suit.CLUBS
        vmGame.givePushUps(suit.name)
        val fragment = HomePage.newInstance()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    val distributedToDiamond = fun(dialog: DialogInterface, which: Int): Unit {
        Toast.makeText(
            context,
            "pompes distribués à carreau", Toast.LENGTH_SHORT
        ).show()
        val suit = Suit.DIAMONDS
        vmGame.givePushUps(suit.name)
        val fragment = HomePage.newInstance()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    fun looserPopup(view: View, suit: String) : AlertDialog {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))
            .setTitle("Vous avez Perdu")
            .setMessage("l'équipe ${suit} a gagné")

        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        alertDialog.show()

        return  alertDialog
    }

    fun loserPushUps(view: View, bet: Int) : AlertDialog {
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))
            .setMessage("Vous avez ${bet} pompes à faire")
            .setPositiveButton("Quitter", DialogInterface.OnClickListener() {
                dialog: DialogInterface, which: Int ->
                val fragment = HomePage.newInstance()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit()
            })

        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        alertDialog.show()

        return  alertDialog
    }
}