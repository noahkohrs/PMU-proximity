package com.inc.pmu.viewmodels;

import com.inc.pmu.models.Player;
import com.inc.pmu.models.Suit;
import com.inc.pmu.models.Card;

import java.util.Collection;

public class ViewModelListener {
    
    public void onConnectionEstablished(){}
    public void onPlayerListUpdate(String[] playerList){}
    public void onBetStart(){}
    public void onBetValidated(Suit suit, Collection<Player> players){}
    public void onGameCreated(){}
    public void onCardDrawn(Card card){}
    public void onPlayerDoingPushUps(String puuid){}
    public void onStartVote(){}
    public void onVoteFinished(String puuid, boolean voteResult){}
}
