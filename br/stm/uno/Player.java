package br.stm.uno;

import java.util.List;

public class Player {

    private Pile cards;

    public Player(List<Card> cardsList) {
        cards = new Pile(cardsList);
    }

    public List<Card> getCards() {
        return cards.getCards();
    }

    public Card pickCard(int index) {
        Card c = cards.getCards().get(index);
        cards.getCards().remove(index);
        return c;
    }

    public void buyCard(DrawPile drawPile, DiscardPile discardPile, int amount) {
        if (drawPile.getCards().size() < amount) {
            drawPile.refill(discardPile);
        }
        for (int i = 0; i < amount; i++) {
            cards.put(drawPile.popOne(), false);
        }
        System.out.println(amount + " cards have been bought from the pile");
    }

}
