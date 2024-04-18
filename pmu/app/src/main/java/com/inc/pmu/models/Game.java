package com.inc.pmu.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Game implements IGame, Jsonisable {
    
    public Board board;
    public Map<String, Player> players; // key: puuid, value: player

    public Card currentCard;
    private boolean currentRoundHaveBeenCanceled = true;

    public Game(Player host) {
        // TODO : Board from json file as a client
        this.players = new HashMap<String, Player>();
        players.put(host.puuid, host);
    }

    @Override
    public void addPlayer(Player player) {
        players.put(player.puuid, player);
    }

    @Override
    public void removePlayer(String puuid) {
        players.remove(puuid);
    }

    @Override
    public void cardDrawn(Card card) {
        board.moveRiderForward(card.suit);
        currentRoundHaveBeenCanceled = false;
        currentCard = card;
    }

    @Override
    public void roundCancelled(String puuid) {
        if (puuid == null) {
            throw new NullPointerException("Null puuid given");
        }

        Player p = players.get(puuid);
        if (p==null) {
            throw new NullPointerException("Player founded unavailable");
        }
        p.currentPushUps = p.currentPushUps*2;
        currentRoundHaveBeenCanceled = true;
        board.moveRiderBackward(currentCard.suit);
    }

    public boolean isRoundCanceled() {
        return currentRoundHaveBeenCanceled;
    }

    @Override
    public JSONObject toJson() {
        JSONObject game = new JSONObject();
        try {
            game.put("board", board.toJson());
            if (currentCard != null)
                game.put("currentCard", currentCard.toJson());
            JSONArray players = new JSONArray();
            for (Player p : this.players.values()) {
                players.put(p.toJson());
            }
            game.put("players", players);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return game;
    }

    private Game(Board board, Map<String, Player> players, Card currentCard) {
        this.board = board;
        this.players = players;
        this.currentCard = currentCard;
    }

    public static Game fromJson(JSONObject json) {
        Board board;
        Map<String, Player> players;
        Card currentCard = null;
        try {
            board = Board.fromJson(json.getJSONObject("board"));
            if (json.has("currentCard"))
                currentCard = Card.fromJson(json.getJSONObject("currentCard"));
            players = new HashMap<String, Player>();
            JSONArray playersJson = json.getJSONArray("players");
            for (int i = 0; i < playersJson.length(); i++) {
                Player p = Player.fromJson(playersJson.getJSONObject(i));
                players.put(p.puuid, p);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new Game(board, players, currentCard);
    }

    public Suit getWinner() {
        Suit winSuit = null;
        for (Suit suit : Suit.values()) {
            if (this.board.riderPos.get(suit) == Board.LENGTH + 1) {
                winSuit = suit;
            }
        }

        return winSuit;
    }
}
