package com.inc.pmu.models;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.util.Log;

import com.inc.pmu.viewmodels.ViewModelListener;

public class Validator {

    private HashMap<String, VoteState> vote;
    public String votedPlayerPuuid;

    private boolean IS_DONE=false;
    enum VoteState {
        NOT_VOTED,
        VOTE_TRUE,
        VOTE_FALSE
    }

    public Validator(String votedPlayerPuuid, Set<String> puuids) {
        this.votedPlayerPuuid = votedPlayerPuuid;
        vote = new HashMap<>();
        for (String puuid : puuids) {
            if (!puuid.equals(votedPlayerPuuid))
                vote.put(puuid, VoteState.NOT_VOTED);
        }
    }

    public void vote(String puuid, boolean validate) {
        if (IS_DONE)
            throw new IllegalStateException("Trying to vote while vote done");
        if (puuid.equals(votedPlayerPuuid))
            throw new IllegalArgumentException("The voted player can't be a voter");
        vote.put(puuid, validate ? VoteState.VOTE_TRUE : VoteState.VOTE_FALSE);
    }

    public boolean hasEveryoneVoted() {
        if (IS_DONE)
            throw new IllegalStateException("Trying to vote while vote done");
        Collection<VoteState> values = vote.values();
        int numberOfVotesDones = 0;
        for (VoteState v : values){
            if (v.equals(VoteState.VOTE_TRUE) || v.equals(VoteState.VOTE_FALSE))
                numberOfVotesDones++;
        }
        return numberOfVotesDones >= values.size();
    }

    /**
     * Warning : This will mark the voting process as done
     * @return the vote result
     */
    public boolean getResult() {
        if (IS_DONE)
            throw new IllegalStateException("Trying to vote while vote done");
        Collection<VoteState> values = vote.values();
        int voteTrue = 0;
        int voteFalse = 0;
        for (VoteState v : values){
            if (v.equals(VoteState.VOTE_TRUE))
                voteTrue++;
            else if (v.equals(VoteState.VOTE_FALSE))
                voteFalse++;
        }
        IS_DONE = true;
        return voteTrue >= voteFalse;
    }

    /**
     * To avoid reusing the old one
     * @return if the vote is done
     */
    public boolean isDone() {
        return IS_DONE;
    }

    public static Validator doneValidator() {
        return new Validator();
    }
    private Validator(){
        IS_DONE = true;
    }
}
