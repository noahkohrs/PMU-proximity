package com.inc.pmu.viewmodels;

import com.inc.pmu.models.Player;
import com.inc.pmu.models.Suit;

import java.util.Collection;

public class ViewModelListener {
    
    public void onConnectionEstablished(){}
    public void onPlayerListUpdate(String[] playerList){}
    public void onBetStart(){}
    public void onBetValidated(Suit suit, Collection<Player> players){}
    public void onGameCreated(){}
    public void onCardDrawn(String card){}
    public void onPlayerDoingPushUps(){}
    public void onStartVote(){}
    public void onVoteFinished(boolean voteResult){}
    
    
    
    
}
