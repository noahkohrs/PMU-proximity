package com.inc.pmu.viewmodels;

import android.util.Log;

import com.inc.pmu.Global;

public class VMStateMachine {

    public VMStateMachine() {
        state = State.GAME_SETUP;
    }
    enum Transition {
        START_GAME,
        DRAW_CARD,
        VOTE_SUCCESS,
        VOTE_FAIL,
        ASK_FOR_PUSH_UPS,
        CONFIRM_PUSH_UPS
    }

    enum State {
        GAME_SETUP,
        WAITING_FOR_DRAWING,
        CARD_DRAWN,
        DOING_PUSH_UPS,
        VALIDATING_PLAYER
    }

    private State state = State.GAME_SETUP;

    public void updateState(Transition transition) {
        State prev = state;
        switch (state) {
            case GAME_SETUP:
                if (transition == Transition.START_GAME) {
                    state = State.WAITING_FOR_DRAWING;
                }
                break;
            case WAITING_FOR_DRAWING:
                if (transition == Transition.DRAW_CARD) {
                    state = State.CARD_DRAWN;
                }
                break;
            case CARD_DRAWN:
                if (transition == Transition.DRAW_CARD) {
                    state = State.CARD_DRAWN;
                } else if (transition == Transition.ASK_FOR_PUSH_UPS) {
                    state = State.DOING_PUSH_UPS;
                }
                break;
            case DOING_PUSH_UPS:
                if (transition == Transition.CONFIRM_PUSH_UPS) {
                    state = State.VALIDATING_PLAYER;
                }
                break;
            case VALIDATING_PLAYER:
                if (transition == Transition.VOTE_SUCCESS) {
                    state = State.WAITING_FOR_DRAWING;
                } else if (transition == Transition.VOTE_FAIL) {
                    state = State.DOING_PUSH_UPS;
                }
                break;
        }
        Log.d(Global.TAG, "State [" + prev + " -> " + state + "] by " + transition);
    }

    public void notifyStartGame() {
        updateState(Transition.START_GAME);
    }

    public void notifyDrawCard() {
        updateState(Transition.DRAW_CARD);
    }

    public void notifyVoteSuccess() {
        updateState(Transition.VOTE_SUCCESS);
    }

    public void notifyVoteFail() {
        updateState(Transition.VOTE_FAIL);
    }

    public void notifyAskForPushUps() {
        updateState(Transition.ASK_FOR_PUSH_UPS);
    }

    public void notifyConfirmPushUps() {
        updateState(Transition.CONFIRM_PUSH_UPS);
    }


    public boolean isGameSetup() {
        return state == State.GAME_SETUP;
    }

    public boolean isWaitingForDrawing() {
        return state == State.WAITING_FOR_DRAWING;
    }

    public boolean isCardDrawn() {
        return state == State.CARD_DRAWN;
    }

    public boolean isDoingPushUps() {
        return state == State.DOING_PUSH_UPS;
    }

    public boolean isValidatingPlayer() {
        return state == State.VALIDATING_PLAYER;
    }
}
