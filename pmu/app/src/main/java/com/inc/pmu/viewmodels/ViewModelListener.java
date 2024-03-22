package com.inc.pmu.viewmodels;

import com.inc.pmu.models.Suit;

public class ViewModelListener {
    
    public void onConnectionEstablished(){}
    public void onPlayerListUpdate(String[] playerList){}
    public void onBetStart(){}
    public void onBetValidated(Suit suit){}
    public void onGameCreated(){}
    public void onCardDrawn(String card){}
    public void onPlayerDoingPushUps(){}
    public void onStartVote(){}
    public void onVoteFinished(boolean voteResult){}
    
    
    
    
}
