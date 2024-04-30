package com.inc.pmu

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.CountDownTimer
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.Log
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.inc.pmu.models.Card
import com.inc.pmu.models.Suit
import com.inc.pmu.viewmodels.ViewModelListener
import com.inc.pmu.viewmodels.ViewModelPMU
import com.inc.pmu.viewmodels.ViewModelPMUFactory


class GameBoard : Fragment(R.layout.game_page) {


    private lateinit var vmGame: ViewModelPMU
    private lateinit var context: Context
    private lateinit var view: View

    private lateinit var deckButton : ImageButton
    private lateinit var playedCards : ImageView
    private lateinit var spades : ImageView
    private lateinit var clubs : ImageView
    private lateinit var hearts : ImageView
    private lateinit var diamonds : ImageView

    private lateinit var spadesString : String
    private lateinit var clubsString : String
    private lateinit var heartsString : String
    private lateinit var diamondsString : String


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
        clubs = requireView().findViewById(R.id.c1)
        hearts = requireView().findViewById(R.id.h1)
        diamonds = requireView().findViewById(R.id.d1)
        deckButton = requireView().findViewById(R.id.deck)
        playedCards = requireView().findViewById(R.id.playedCards)
        pushButton = requireView().findViewById(R.id.pushButton)

        currentSuit = requireView().findViewById(R.id.playerSuit)
        currentNbPushUps = requireView().findViewById(R.id.currentPushUps)

        spadesString = resources.getString(R.string.spades)
        clubsString = resources.getString(R.string.clubs)
        heartsString = resources.getString(R.string.hearts)
        diamondsString = resources.getString(R.string.diamonds)

        var suit = vmGame.game.players[vmGame.localPuuid]!!.bet.suit
        when(suit) {
            // Get the text from strings.xml
            Suit.HEARTS -> currentSuit.text = spadesString
            Suit.SPADES -> currentSuit.text = clubsString
            Suit.CLUBS -> currentSuit.text = heartsString
            Suit.DIAMONDS -> currentSuit.text = diamondsString
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
            if (!vmGame.checkWin()){
                vmGame.drawCard()
                deckButton.isClickable = false
                Log.d(TAG.TAG, "Bouton non clickable")
                val timer = object: CountDownTimer(Const.MIN_TIME_FOR_A_NEW_DRAW, 100) {
                    override fun onTick(millisUntilFinished: Long) {
                        //affiche les secondes sur le deck transparent
                    }
                    override fun onFinish() {
                        deckButton.isClickable = true
                        Log.d(TAG.TAG, "Bouton re-clickable !")
                    }
                }
                timer.start()
            }
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
                    drawnCardLogic(card)
                }
            }
        )

        // Initial board update (reconnection case)
        updateBoard()
        if (vmGame.game.currentCard != null)
            drawnCardLogic(vmGame.game.currentCard)


        vmGame.addListener(
            object : ViewModelListener() {
                override fun onBoardUpdate() {
                    updateBoard()
                }
            }
        )

        vmGame.addListener(
            object : ViewModelListener() {
                override fun onPlayerDoingPushUps(puuid : String) {
                    pushButton.isClickable = false
                    pushButton.setBackgroundColor(context.resources.getColor(R.color.unavailable))
                    if (puuid == vmGame.localPuuid) {
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

                    if (vmGame.localPuuid == puuid) {
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
                    val player = vmGame.game.players.get(vmGame.localPuuid)!!
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

    fun drawnCardLogic(card : Card) {

        Log.d(TAG.TAG, "Drawn card: $card")
        var drawCard : Drawable = getCardDrawable(card, context)
        playedCards.setImageDrawable(drawCard)

        var cardPos : Int = vmGame.game.board.riderPos[card.suit] as Int
        var dividerId = getDividerFromPos(cardPos, context)
        var divider : View = view.findViewById(dividerId)

        var c : ImageView
        when(card.suit) {
            Suit.HEARTS -> c = hearts
            Suit.SPADES -> c = spades
            Suit.CLUBS -> c = clubs
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

        var suitString: String
        when(card.suit) {

            Suit.HEARTS -> suitString = heartsString
            Suit.SPADES -> suitString = spadesString
            Suit.CLUBS -> suitString = clubsString
            Suit.DIAMONDS -> suitString = diamondsString

        }
        pushButton.text = resources.getString(R.string.cancelCard) + "\n" + suitString
        if (vmGame.game.players[vmGame.localPuuid]?.bet?.suit == card.suit) {
            pushButton.isClickable = false
            pushButton.setBackgroundColor(context.resources.getColor(R.color.unavailable))
        }
        else {
            pushButton.isClickable = true
            pushButton.setBackgroundColor(context.resources.getColor(R.color.white))
        }
    }

    fun updateBoard() {
        currentNbPushUps.text = vmGame.game.players[vmGame.localPuuid]!!.currentPushUps.toString()

        val constraintSet = ConstraintSet()
        constraintSet.clone(context, R.layout.game_page)

        val margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, context.resources.displayMetrics).toInt()

        for (suit in Suit.entries) {
            val c: ImageView = when (suit) {
                Suit.HEARTS -> hearts
                Suit.SPADES -> spades
                Suit.CLUBS -> clubs
                Suit.DIAMONDS -> diamonds
            }

            val cardPos: Int = vmGame.game.board.riderPos[suit] as Int
            val dividerId = getDividerFromPos(cardPos, context)
            val divider: View = view.findViewById(dividerId)

            // Apply the new constraints
            constraintSet.connect(c.id, ConstraintSet.TOP, divider.id, ConstraintSet.BOTTOM, margin)
        }

        // Apply the transition
        val transition = ChangeBounds()
        transition.duration = Const.CARD_TRANSITION_DURATION
        TransitionManager.beginDelayedTransition(view as ViewGroup, transition)
        constraintSet.applyTo(view as ConstraintLayout)
    }


    fun doPushups(view: View): AlertDialog {
        // <string name="doPushupsMsg">Do %1$d push-ups</string>
        val msg: String = resources.getString(R.string.doPushupsMsg, vmGame.game.players[vmGame.localPuuid]?.currentPushUps)
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))
            .setMessage(msg)
            .setPositiveButton(resources.getString(R.string.doPushUpsConfirm), positivePushupsButtonClick)

        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        alertDialog.show()

        if (vmGame.isHost()) {
            deckButton.isClickable = true
        }
        return alertDialog
    }

    fun waitForPushups(view: View, puuid : String): AlertDialog {
        val msg = resources.getString(R.string.otherDoPushUpsMsg, vmGame.game.players[puuid]?.playerName, vmGame.game.players[puuid]?.currentPushUps)
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))
            .setMessage(msg)

        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        alertDialog.show()

        return alertDialog
    }

    val positivePushupsButtonClick = { dialog: DialogInterface, which: Int ->
        val msg = resources.getString(R.string.pushUpsValidated)
        Toast.makeText(context,
            msg, Toast.LENGTH_SHORT).show()
        vmGame.pushUpsDone()
    }

    fun waitingForVotes(view: View): AlertDialog {
        val msg = resources.getString(R.string.waitingForVote)
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))
            .setMessage(msg)

        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        alertDialog.show()

        return alertDialog
    }

    fun votes(view: View): AlertDialog {
        val msg = resources.getString(R.string.voteMsg)
        val yes = resources.getString(R.string.yesBtn)
        val no = resources.getString(R.string.noBtn)
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))
            .setMessage(msg)
            .setPositiveButton(yes, positiveVoteButton)
            .setNegativeButton(no, negativeVoteButton)

        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        alertDialog.show()

        return  alertDialog
    }

    val positiveVoteButton = { dialog: DialogInterface, which: Int ->
        Toast.makeText(context,
            resources.getString(R.string.voteValid), Toast.LENGTH_SHORT).show()
        vmGame.vote(true)
    }

    val negativeVoteButton = { dialog: DialogInterface, which: Int ->
        Toast.makeText(context,
            resources.getString(R.string.voteRefused), Toast.LENGTH_SHORT).show()
        vmGame.vote(false)
    }

    val yesPuchupsButton = { dialog: DialogInterface, which: Int ->
        Toast.makeText(context,
            resources.getString(R.string.voteValid), Toast.LENGTH_SHORT).show()
        vmGame.doPushUps()
    }

    val noPuchupsButton = { dialog: DialogInterface, which: Int ->
        Toast.makeText(context,
            resources.getString(R.string.voteRefused), Toast.LENGTH_SHORT).show()
    }

    fun getCardDrawable(card : Card, context : Context) : Drawable {
        var uri : String = "@drawable/${card.toString()}"
        var imageId : Int = context.resources.getIdentifier(uri,null, context.packageName)
        Log.d(TAG.TAG, imageId.toString())
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
        val title = resources.getString(R.string.winTitle)
        val msg = resources.getString(R.string.winText)
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))
            .setTitle(title)
            .setMessage(msg)

        when(suit) {
            Suit.HEARTS.name -> {
                builder
                    .setPositiveButton(spadesString, distributedToSpade)
                    .setNeutralButton(clubsString, distributedToDiamond)
                    .setNegativeButton(diamondsString, distributedToClub)
            }
            Suit.SPADES.name -> {
                builder
                    .setPositiveButton(clubsString, distributedToClub)
                    .setNeutralButton(diamondsString, distributedToDiamond)
                    .setNegativeButton(heartsString, distributedToHeart)
            }
            Suit.CLUBS.name -> {
                builder
                    .setPositiveButton(diamondsString, distributedToDiamond)
                    .setNeutralButton(heartsString, distributedToHeart)
                    .setNegativeButton(spadesString, distributedToSpade)
            }
            Suit.DIAMONDS.name -> {
                builder
                    .setPositiveButton(heartsString, distributedToHeart)
                    .setNeutralButton(spadesString, distributedToSpade)
                    .setNegativeButton(clubsString, distributedToClub)
            }
        }

        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        alertDialog.show()

        return  alertDialog
    }

    val distributedToHeart = fun(dialog: DialogInterface, which: Int): Unit {
        val text = resources.getString(R.string.pushUpsDistributedTo, heartsString)
        Toast.makeText(
            context,
            text, Toast.LENGTH_SHORT
        ).show()
        val suit = Suit.HEARTS
        vmGame.givePushUps(suit.name)
        val fragment = HomePage.newInstance()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    val distributedToSpade = fun(dialog: DialogInterface, which: Int): Unit {
        val text = resources.getString(R.string.pushUpsDistributedTo, spadesString)
        Toast.makeText(
            context,
            text, Toast.LENGTH_SHORT
        ).show()
        val suit = Suit.SPADES
        vmGame.givePushUps(suit.name)
        val fragment = HomePage.newInstance()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    val distributedToClub = fun(dialog: DialogInterface, which: Int): Unit {
        val text = resources.getString(R.string.pushUpsDistributedTo, clubsString)
        Toast.makeText(
            context,
            text, Toast.LENGTH_SHORT
        ).show()
        val suit = Suit.CLUBS
        vmGame.givePushUps(suit.name)
        val fragment = HomePage.newInstance()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    val distributedToDiamond = fun(dialog: DialogInterface, which: Int): Unit {
        val text = resources.getString(R.string.pushUpsDistributedTo, diamondsString)
        Toast.makeText(
            context,
            text, Toast.LENGTH_SHORT
        ).show()
        val suit = Suit.DIAMONDS
        vmGame.givePushUps(suit.name)
        val fragment = HomePage.newInstance()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    fun looserPopup(view: View, suit: String) : AlertDialog {
        val title = resources.getString(R.string.loseTitle)
        val msg = resources.getString(R.string.loseText, suit)
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))
            .setTitle(title)
            .setMessage(msg)

        val alertDialog = builder.create()
        alertDialog.setCancelable(false)

        alertDialog.show()

        return  alertDialog
    }

    fun loserPushUps(view: View, bet: Int) : AlertDialog {
        val msg = resources.getString(R.string.pushUpsReceived, bet)
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))
            .setMessage(msg)
            .setPositiveButton(resources.getString(R.string.quitBtn), DialogInterface.OnClickListener() {
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