package br.stm.uno;

public class DrawPile extends Pile{

    public DrawPile() {
        super(Card.getDeck());
        shuffle();
    }

    public void refill(DiscardPile discardPile) {
        int c = discardPile.getCards().size() - 1;
        while(c > 0) {
            getCards().add(discardPile.getCards().get(0));
            discardPile.getCards().remove(0);
            c--;
        }
        shuffle();
        System.out.println("Discard pile's cards were sent back to the draw pile");
    }

    public Card getFirstCard() {
        Card card;

        while (true) {
            card = popOne();
            if (!card.getType().equals(CardType.WILDCARD_BUY_FOUR)) {
                break;
            } else {
                put(card, true);
            }
        }
        return card;
    }


}
