package br.stm.uno;

import java.util.ArrayList;
import java.util.List;

public class PlayersList {

    private List<Player> players;
    private boolean clockwise;
    private int currentPlayer;

    public PlayersList(int nOpponents, DrawPile drawPile) {
        players = new ArrayList<>();
        clockwise = true;
        currentPlayer = 0;
        // Each player starts with 7 cards
        for (int i = 0; i < nOpponents + 1; i++) {
            players.add(new Player(drawPile.pop(7)));
        }
    }

    public void invertOrder() {
        clockwise = !clockwise;
    }

    public int getNextIndex() {
        return (clockwise ? currentPlayer + 1 : currentPlayer + players.size() - 1) % players.size();
    }

    public int getCurrentIndex() {
        return currentPlayer;
    }

    public Player getNext() {
        return players.get(getNextIndex());
    }

    public Player getCurrent() {
        return players.get(currentPlayer);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void next() {
        currentPlayer = getNextIndex();
    }

}
