package br.stm.uno;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Game {

    private PlayersList players;
    private DrawPile drawPile;
    private DiscardPile discardPile;
    private boolean actionTaken;
    private int totalTurns;
    private ThreadLocalRandom random;
    //private Scanner input;

    public Game(int nOpponents) {
        discardPile = new DiscardPile();
        drawPile = new DrawPile();
        players = new PlayersList(nOpponents, drawPile);
        totalTurns = 0;
        actionTaken = false;
        random = ThreadLocalRandom.current();
        //input = new Scanner(System.in);
    }

    private int getScore(int winnerIndex) {
        int score = 0;
        for (int i = 0; i < players.getPlayers().size(); i++) {
            if (i != winnerIndex) {
                for (Card c : players.getPlayers().get(i).getCards()) {
                    score += c.getValue();
                }
            }
        }
        return score;
    }

    private int gameOver() {
        for (int i = 0; i < players.getPlayers().size(); i++) {
            int nCards = players.getPlayers().get(i).getCards().size();
            if (nCards == 0) {
                System.out.println("Player " + i + " won, scoring " + getScore(i) + " points");
                return i;
            }
        }
        return -1;
    }

    private CardColor getMostCommon(List<Card> cards) {
        HashMap<CardColor, Integer> freqs = new HashMap<>();
        for (Card c : cards) {
            if (c.getColor() != null) {
                if (freqs.containsKey(c.getColor())) {
                    freqs.put(c.getColor(), freqs.get(c.getColor()) + 1);
                } else {
                    freqs.put(c.getColor(), 1);
                }
            }
        }
        return freqs.isEmpty() ? null :
                Collections.max(freqs.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }

    private List<Integer> getMatches(List<Card> available) {
        List<Integer> matches = new ArrayList<>();
        boolean colorMatch = false;
        int wildCardIndex = -1;
        for (int i = 0; i < available.size(); i++) {
            Card c = available.get(i);
            if (!c.getType().equals(CardType.WILDCARD_BUY_FOUR)) {
                boolean sameColor = c.getColor() != null && c.getColor().equals(discardPile.getLastColor());
                if (sameColor) {
                    colorMatch = true;
                    matches.add(i);
                } else {
                    boolean isWildCard = c.getType().equals(CardType.WILDCARD);
                    boolean sameType = c.getType().equals(discardPile.getLastCard().getType());
                    boolean isNumber = c.getType().equals(CardType.NUMBER);
                    boolean sameValue = c.getValue() ==  discardPile.getLastCard().getValue();
                    if (isWildCard || ((sameType && !isNumber) || (isNumber && sameValue))) {
                        matches.add(i);
                    }
                }
            } else if (wildCardIndex == -1) {
                wildCardIndex = i;
            }
            if (i == available.size() - 1 && !colorMatch && wildCardIndex > -1) {
                matches.add(wildCardIndex);
            }
        }
        return matches;
    }

    private boolean stepTurn() {
        players.next();
        totalTurns++;
        System.out.println("---------------------------");
        return true;
    }

    private void chooseColor(boolean currentPlayer) {
        Player player = currentPlayer ? players.getCurrent() : players.getNext();
        int index = currentPlayer ? players.getCurrentIndex() : players.getNextIndex();
        System.out.println("\nWildcard! Player " + index + " will choose a color");
        CardColor frequentColor = getMostCommon(player.getCards());
        discardPile.setLastColor(frequentColor != null ? frequentColor
                : CardColor.values()[random.nextInt(CardColor.values().length)]);
        System.out.println("Player " + index + " has chosen the color "
                + discardPile.getLastColor().toString().toLowerCase());
    }

    public boolean nextMove() {
        if (gameOver() == -1) {
            if (totalTurns == 0) {
                System.out.println("Game started");
                Card firstCard = drawPile.getFirstCard();
                discardPile.put(firstCard);
                if (firstCard.getType().equals(CardType.WILDCARD)) {
                    chooseColor(false);
                } else if (firstCard.getType().equals(CardType.BUY_TWO)) {
                    Player nextPlayer = players.getNext();
                    System.out.println("Game started with a buy two. Player "
                            + players.getNextIndex() + " will draw two cards");
                    nextPlayer.buyCard(drawPile, discardPile, 2);
                }
                actionTaken = true;
            } else {
                System.out.println("It's player " + players.getCurrentIndex() + "'s turn");
                System.out.println("Cards before turn: " + players.getCurrent().getCards());
                Player currentPlayer = players.getCurrent();

                if (!actionTaken) {
                    System.out.println("Gotta suffer an action");
                    switch (discardPile.getLastCard().getType()) {
                        case BUY_TWO:
                            System.out.println("Buy two");
                            currentPlayer.buyCard(drawPile, discardPile, 2);
                            break;
                        case INVERT:
                            System.out.println("Invert");
                            if (players.getPlayers().size() > 2) {
                                players.invertOrder();
                                players.next();
                            }
                            break;
                        case SKIP:
                            System.out.println("Skip");
                            break;
                        case WILDCARD_BUY_FOUR:
                            System.out.println("Buy four");
                            currentPlayer.buyCard(drawPile, discardPile, 4);
                            break;
                        default:
                            break;
                    }
                    actionTaken = true;
                    return stepTurn();
                }
                List<Integer> possibleActions = getMatches(currentPlayer.getCards());
                System.out.println("Possible actions: " + possibleActions);
                if (!possibleActions.isEmpty()) {
                    int actionIndex = possibleActions.get(random.nextInt(possibleActions.size()));
                    Card chosenCard = currentPlayer.pickCard(actionIndex);
                    discardPile.put(chosenCard);
                    if (chosenCard.getColor() == null) {
                        chooseColor(true);
                    }
                    Card lastCard= discardPile.getLastCard();
                    System.out.println("Last card type is " + lastCard.getType());
                    actionTaken = lastCard.getType().equals(CardType.NUMBER)
                            || lastCard.getType().equals(CardType.WILDCARD);
                } else {
                    System.out.println("Gotta buy some cards");
                    currentPlayer.buyCard(drawPile, discardPile, 1);
                }
                System.out.println("Cards after turn: " + currentPlayer.getCards());
                if (currentPlayer.getCards().size() == 1) {
                    System.out.println("Player " + players.getCurrentIndex() + " says UNO!");
                }
            }
            return stepTurn();
        }
        System.out.println("Game ended after " + totalTurns + " turns");
        return false;
    }

}
