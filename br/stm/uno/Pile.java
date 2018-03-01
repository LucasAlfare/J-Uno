package br.stm.uno;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pile {

    private List<Card> cards;

    public Pile(List<Card> cards) {
        this.cards = cards;
    }

    public List<Card> getCards() {
        return cards;
    }

    public List<Card> pop(int amount) {
        List<Card> cards = new ArrayList<>();
        if (this.cards.size() >= amount) {
            while (cards.size() < amount) {
                cards.add(this.cards.get(this.cards.size() - 1));
                this.cards.remove(this.cards.size() - 1);
            }
        }
        return cards;
    }

    public Card popOne() {
        return pop(1).get(0);
    }

    public void put(Card c, boolean bottom) {
        if (bottom || cards.size() == 0) {
            cards.add(0, c);
        } else {
            cards.add(c);
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }
}
