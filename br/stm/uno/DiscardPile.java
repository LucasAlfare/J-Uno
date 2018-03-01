package br.stm.uno;

import java.util.ArrayList;

public class DiscardPile extends Pile {

    private CardColor lastColor;

    public DiscardPile() {
        super(new ArrayList<>());
    }

    public void put(Card c) {
        put(c, false);
        System.out.println("Discard pile: "  + getLastCard());
        lastColor = c.getColor();
    }

    public void setLastColor(CardColor lastColor) {
        this.lastColor = lastColor;
    }

    public CardColor getLastColor() {
        return lastColor;
    }

    public Card getLastCard() {
        return getCards().get(getCards().size() - 1);
    }

}
